package chronicle

import scala.io.Source

class LastFmImporter(username: String, apiKey: String) {
  final val configFileName: String = "/lastfm.json"
  
  def run(): Unit = {
    val config =
        Option(getClass.getResourceAsStream(configFileName))
            .map { stream => Source.fromInputStream(stream).mkString }
            .getOrElse(s"Could not read $configFileName")
    println(config)
  }
}
