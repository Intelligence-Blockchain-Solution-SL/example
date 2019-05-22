package es.ibs.example

import java.util.Properties
import akka.actor.ActorSystem
import javax.inject.Inject
import es.ibs.util.ConfigEx

class Config @Inject() (implicit as: ActorSystem) {
  import ConfigEx._

  private val c = as.settings.config

  val pg_prop: Properties = c.getProperties("pg")

  val oer_apiKey: String = c.getString("main.oer.api-key")
}

object Config {
  val APP_NAME = "example"
}
