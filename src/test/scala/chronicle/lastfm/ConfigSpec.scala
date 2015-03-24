import chronicle.lastfm.Config
import org.scalatest.{WordSpec, Matchers}

class ConfigSpec extends WordSpec with Matchers {
  "A Last.FM Config" when {
    "Loading from JSON" should {
      "return a loaded config when given JSON with an apiKey" in {
        val json = """{
          "apiKey": "narlzac"
        }"""
        Config.loadFromJson(json) should be (Some(Config("narlzac")))    
      }

      "return None when there isn't an apiKey" in {
        val json = """{
          "suchApi": "very wow"
        }"""
        Config.loadFromJson(json) should be (None)
      }

      "return None when given garbage" in {
        Config.loadFromJson("he thinks he's JSON") should be (None)
      }
    }
  }
}