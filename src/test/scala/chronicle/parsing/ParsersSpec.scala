import chronicle.parsing.Parsers
import org.scalatest.{WordSpec, Matchers}

class ParserSpec extends WordSpec with Matchers {
  "Parsers.Int" when {
    "Given a string containing an Int" should {
      "parse it to an Int" in {
        Parsers.Int("747") should be (Some(747))    
      }
    }
    "Given a string containing a Floating point number" should {
      "return none" in {
        Parsers.Int("3.1415") should be (None) 
      }
    }
    "Given a non-numeric string" should {
      "return none" in {
        Parsers.Int("charles") should be (None) 
      }
    }
  }
}
