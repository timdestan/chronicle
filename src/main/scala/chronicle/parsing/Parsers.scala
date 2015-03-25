package chronicle.parsing

import java.lang.NumberFormatException
import org.json4s._
import org.json4s.native.JsonMethods._

object Parsers {
  object Int {
    def apply(mightBeInt: String) : Option[Int] = try {
      Some(mightBeInt.toInt)
    } catch {
      case _ : NumberFormatException => None
    }
  }

  object Json {
    implicit val jsonFormats = org.json4s.DefaultFormats

    def parse[T: Manifest](json: String) : Option[T] =
      parseOpt(json) flatMap { _.extractOpt[T] }
  }
}
