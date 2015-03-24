package chronicle.enrichments

import java.lang.NumberFormatException

object Int {
  def unapply(s : String) : Option[Int] = try {
    Some(s.toInt)
  } catch {
    case _ : NumberFormatException => None
  }
}