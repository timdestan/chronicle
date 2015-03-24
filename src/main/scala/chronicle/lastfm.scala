package chronicle

import org.json4s._
import org.json4s.native.JsonMethods._
import StreamEnrichments._

case class LastFmConfig(apiKey: String)

object LastFmConfig {
  final val configFileName: String = "/lastfm.json"
  
  def loadFromJson(json: String): Option[LastFmConfig] =
    (for {
      JObject(obj) <- parse(json)
      JField("apiKey", JString(apiKey)) <- obj
    } yield LastFmConfig(apiKey)).headOption

  def load(): Unit =
    println(Option(getClass.getResourceAsStream(configFileName))
        .flatMap(stream => loadFromJson(stream.readText))
        .map(config => "Your API key is: ${config.apiKey}")
        .getOrElse(s"Couldn't load configuration file: $configFileName. " +
          "You may need to put one in src/main/resources."))
}
