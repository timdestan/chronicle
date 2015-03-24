package chronicle.lastfm

import org.json4s._
import org.json4s.native.JsonMethods._
import scala.util.Try

case class Config(apiKey: String)

object Config {
  final val configFileName: String = "/lastfm.json"
  
  def loadFromJson(json: String): Option[Config] =
    Try(for {
      JObject(obj) <- parse(json)
      JField("apiKey", JString(apiKey)) <- obj
    } yield Config(apiKey))
      .toOption
      .flatMap { _.headOption }
}
