package chronicle

import kotlin.io

class LastFmImporter(username: String, apiKey: String) {
  companion object {
    // Name of the config file to specify Last.FM credentials
    val configFileName: String = "/lastfm.json"
  }

  fun import(): Unit {
    val config =
        javaClass
            .getResourceAsStream(configFileName)
            ?.reader(Charsets.UTF_8)
            ?.readText()
            ?: "Could not find $configFileName"
    println(config)
  }
}