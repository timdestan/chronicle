package chronicle.lastfm

import chronicle.StreamEnrichments._
import org.json4s._
import org.json4s.native.JsonMethods._

case class Config(apiKey: String)

object Config {
  final val configFileName: String = "/lastfm.json"
  
  def loadFromJson(json: String): Option[Config] =
    (for {
      JObject(obj) <- parse(json)
      JField("apiKey", JString(apiKey)) <- obj
    } yield Config(apiKey)).headOption

  def load(): Unit =
    println(Option(getClass.getResourceAsStream(configFileName))
        .flatMap(stream => loadFromJson(stream.readText))
        .map(config => s"Your API key is: ${config.apiKey}")
        .getOrElse(s"Couldn't load configuration file: $configFileName. " +
          "You may need to put one in src/main/resources."))
}
