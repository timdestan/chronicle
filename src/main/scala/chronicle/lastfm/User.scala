package chronicle.lastfm

import chronicle.enrichments.Implicits._
import chronicle.parsing.Parsers.Int
import dispatch._, Defaults._
import org.json4s._
import org.json4s.native.JsonMethods._
import scala.collection.immutable.List
import scala.util.Try

// A Last.fm User, together with their play count, which is honestly
// the only thing we really need right now.
case class User(name: String, playCount:Int)

class NoValue extends Exception

object User {
  def buildRequestFor(username: String, config: Config):String =
    new Service(config).userRequestFor(username)
  
  def parseJson(json: String): Option[User] =
    Try(for {
      JObject(obj) <- parse(json)
      JField("name", JString(name)) <- obj
      JField("playcount", JString(playCountStr)) <- obj
      playCount <- Int(playCountStr)
    } yield User(name, playCount))
      .toOption
      .flatMap { _.headOption }

  def lookupByName(username: String, config:Config): Future[User] = {
    val service = url(buildRequestFor(username, config))
    for {
      response <- Http(service OK as.String)
      user <- parseJson(response).toFuture
    } yield user
  }
}
