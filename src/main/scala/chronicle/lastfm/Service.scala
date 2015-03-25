package chronicle.lastfm

import chronicle.Request

class Service(config: Config) {
  // The base URl for all requests to the LastFM API service.
  val baseUrl = "http://ws.audioscrobbler.com/2.0/"
  
  val baseRequest =
    Request.forBaseUrl(baseUrl)
           .withParameter("api_key", config.apiKey)
           .withParameter("format", "json")

  def userRequestFor(username: String) =
    baseRequest.withParameter("method", "user.getinfo")
               .withParameter("user", username)
               .toString

  def listeningHistoryRequestFor(user: User) =
    baseRequest.withParameter("method", "user.gettoptracks")
               .withParameter("user", user.name)
               .toString
}


