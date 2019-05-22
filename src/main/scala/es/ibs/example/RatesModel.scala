package es.ibs.example

import play.api.libs.json.{Json, OFormat}

case class RatesModel(timestamp: Int, base: String, rates: Map[String, BigDecimal])

object RatesModel {
  implicit val fmt: OFormat[RatesModel] = Json.format[RatesModel]
}