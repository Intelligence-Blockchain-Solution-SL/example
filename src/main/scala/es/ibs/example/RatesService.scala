package es.ibs.example

import java.util.concurrent.ConcurrentHashMap
import scala.math.BigDecimal.RoundingMode
import javax.inject.Inject

class RatesService @Inject() () {

  // empty concurrent list to simultaneous read / update access
  val rates: ConcurrentHashMap[String, BigDecimal] = new ConcurrentHashMap(300)

  def convert(from: String, to: String, amount: BigDecimal = 1): BigDecimal = {
    if(rates.containsKey(from) && rates.containsKey(to))
      (amount * rates.get(to) / rates.get(from)).setScale(2, RoundingMode.HALF_EVEN)
    else
      throw new IllegalArgumentException
  }

}
