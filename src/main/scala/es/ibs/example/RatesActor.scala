package es.ibs.example

import java.util.Date
import scala.concurrent.duration._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, StatusCodes, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import javax.inject.Inject
import play.api.libs.json.Json
import es.ibs.util.ActorBase

class RatesActor @Inject() (SVC: RatesService, PG: PgService, CFG: Config) extends ActorBase {
  import scala.collection.JavaConverters._
  import RatesActor._

  private val httpReq = HttpRequest(uri = OER_URI
    .withQuery(Query("app_id" -> CFG.oer_apiKey, "show_alternative" -> "true", "prettyprint" -> "false")))

  override def preStart: Unit = {
    // send first message after 1 second from start
    system.scheduler.scheduleOnce(1.second, self, ITER)
  }

  override def receive: Receive = {

    // received periodical signal, fetch & update rates from OER
    case ITER =>
      Http().singleRequest(httpReq) flatMap { resp =>
        Unmarshal(resp.entity).to[String] map { body =>
          resp.status match {
            case StatusCodes.OK =>
              try {
                val rates = Json.parse(body).as[RatesModel]
                SVC.rates.putAll(rates.rates.asJava) // sync use of Rates service to update all rates
                rates.rates foreach PG.ins_rate // async use of DB service, to push rates to DB, errors will be logged and ignored
                LOG_D(s"Fetched ${rates.rates.size} global currency quotes, base ${rates.base}, timestamped `${new Date(rates.timestamp * 1000L)}`")
              } catch { case err: Exception =>
                throw new Exception(s"[${resp.status} $body]", err)
              }
            case _ =>
              throw new Exception(s"[${resp.status} $body]")
          }
        }
      } recover { case err: Exception =>
          LOG_E(err, ITER)
      } andThen { case _ =>
        system.scheduler.scheduleOnce(1.minute, self, ITER)
      }

  }

}

object RatesActor {
  case object ITER

  private val OER_URI = Uri("https://openexchangerates.org/api/latest.json")
}
