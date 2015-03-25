package chronicle.lastfm

import chronicle.QueryParameter
import chronicle.parsing.Parsers.Int
import dispatch._, Defaults._
import org.json4s._
import org.json4s.native.JsonMethods._
import scala.collection.immutable.List
import scala.concurrent.{Future, Promise}
import scala.util.Try

// A Last.fm User, together with their track count, which is honestly
// the only thing we really need right now.
case class User(name: String, trackCount:Int)

class NoValue extends Exception

object User {
  private final val apiRoot = "http://ws.audioscrobbler.com/2.0/"
  private final val querySeparator = "&"

  def queryParametersFor(username: String, 
                         config:Config): List[QueryParameter[String]] =
    List(
      QueryParameter("method", "user.getinfo"),
      QueryParameter("user", username),
      QueryParameter("api_key", config.apiKey),
      QueryParameter("format", "json"))

  def formatUrlForRequest(username: String, config: Config):String = {
    val queryString =
      queryParametersFor(username,config)
        .mkString(querySeparator)
    s"$apiRoot?$queryString"
  }
  
  def parseJson(json: String): Option[User] =
    Try(for {
      JObject(obj) <- parse(json)
      JField("name", JString(name)) <- obj
      JField("playcount", JString(playCountStr)) <- obj
      playCount <- Int(playCountStr)
    } yield User(name, playCount))
      .toOption
      .flatMap { _.headOption }

  def futureOfOption[T](option: Option[T]):Future[T] = {
    val promise = Promise[T]()
    option match {
      case Some(v) => promise.success(v)
      case None => promise.failure(new NoValue())
    }
    promise.future
  }

  def lookupByName(username: String, config:Config): Future[User] = {
    val service = url(formatUrlForRequest(username, config))
    for {
      response <- Http(service OK as.String)
      user <- futureOfOption(parseJson(response))
    } yield user
  }
}
