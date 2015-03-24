package chronicle

import java.util.UUID;

object Main {
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

    new LastFmImporter("foo", "bar").run
  }
}
