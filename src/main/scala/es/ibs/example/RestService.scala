package es.ibs.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import javax.inject.Inject
import akka.http.scaladsl.model.StatusCodes._
import play.api.libs.json.Json
import es.ibs.util.json.AkkaPlayJsonSupport._

class RestService @Inject() (SVC: RatesService)(implicit as: ActorSystem, fm: ActorMaterializer) {

  val route: Route =
    get {
      path("k" / Segment) { code =>
        complete {
          if(code.length == 10 && code.startsWith("beg") && code.endsWith("fin")) Json.obj("sub_id" -> 218, "region" -> "GLOBAL", "price" -> 10.11)
          else NotFound -> Json.obj()
        }
      } ~
      path(Segment / Segment / DoubleNumber) { case (from, to, amt) =>
        try {
          val res = SVC.convert(from.toUpperCase, to.toUpperCase, BigDecimal(amt))
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, res.toString()))
        } catch { case _: IllegalArgumentException =>
          complete(NotFound -> "Something went wrong.")
        }
      }
    }

  Http().bindAndHandle(route, "localhost", 8080)

}
