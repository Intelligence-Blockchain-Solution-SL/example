package es.ibs.example

import scala.concurrent.{ExecutionContext, Future}
import com.typesafe.scalalogging.LazyLogging
import com.zaxxer.hikari.HikariDataSource
import javax.inject.Inject
import es.ibs.util.Logging
import es.ibs.util.pg.PgDriver

class PgService @Inject() (CFG: Config)(implicit ec: ExecutionContext) extends PgDriver with Logging with LazyLogging {

  override protected val dataSource: HikariDataSource = PgDriver.dataSourceFromProperties(CFG.pg_prop)

  def ins_rate(p: (String, BigDecimal)): Future[Int] = Future {
    dml("insert into rates(curr, rate) values (?,?) on conflict(curr) do update set rate = excluded.rate".stripMargin,
      Seq(PG_TEXT -> p._1, PG_NUMERIC -> p._2.bigDecimal))
  } recover { case err: Exception =>
    LOG_E(err)
    throw err
  }

}
