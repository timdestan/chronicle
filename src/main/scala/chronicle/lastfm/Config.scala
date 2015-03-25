package chronicle.lastfm

import chronicle.parsing.Parsers

case class Config(apiKey: String)

object Config {
  def loadFromJson(json: String) = Parsers.Json.parse[Config](json)
}
