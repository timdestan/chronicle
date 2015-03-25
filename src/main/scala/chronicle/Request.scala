package chronicle

import scala.collection.immutable.List

object Request {
  def forBaseUrl(baseUrl: String) = Request(baseUrl, Nil)
}

case class Request(baseUrl: String, queryParameters:List[QueryParameter[_]]) {
  def withParameter[V](key: String, value:V) =
    Request(baseUrl, QueryParameter(key, value)::queryParameters)

  lazy val queryString = queryParameters.mkString("&")

  override def toString():String = s"$baseUrl?$queryString"
}