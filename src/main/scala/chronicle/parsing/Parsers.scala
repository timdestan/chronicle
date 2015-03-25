package chronicle.parsing

import java.lang.NumberFormatException

object Parsers {
  object Int {
    def apply(mightBeInt: String) : Option[Int] = try {
      Some(mightBeInt.toInt)
    } catch {
      case _ : NumberFormatException => None
    }
  }
}
