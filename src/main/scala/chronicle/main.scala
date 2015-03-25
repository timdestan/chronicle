package chronicle

import chronicle.lastfm.{Config, User}
import chronicle.enrichments.Implicits._
import java.util.UUID
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main {
  // Expected location of the configuration file containing Last.FM API key
  val lastFmConfigFileName: String = "/lastfm.json"

  lazy val config: Option[Config] =
    for {
      configFile <- Option(getClass.getResourceAsStream(lastFmConfigFileName))
      config <- Config.loadFromJson(configFile.readText)
    } yield config

  def main(args: Array[String]):Unit = {
    val weezer =
        Artist(
          "Weezer",
          Some(UUID.fromString("6fe07aa5-fec0-4eca-a456-f29bff451b04")))
    val elScorcho =
        Track("El Scorcho",
              weezer,
              Some(UUID.fromString("58d5ee31-a92c-347c-89d6-3acc765cab9b")))
    println(elScorcho)

    if (config.isEmpty) {
      println("No config")
    } else {
      val futureUser = User.lookupByName("tj6186", config.get)
      val user = Await.result(futureUser, 5.seconds)
      println(user)
    }
  }
}
