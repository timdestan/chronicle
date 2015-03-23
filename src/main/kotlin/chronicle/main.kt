package chronicle

import java.util.UUID;

fun main(args: Array<String>) {
  val weezer =
      Artist("Weezer",
             UUID.fromString("6fe07aa5-fec0-4eca-a456-f29bff451b04"))
  val elScorcho =
      Track("El Scorcho",
            weezer,
            UUID.fromString("58d5ee31-a92c-347c-89d6-3acc765cab9b"))
  println(elScorcho)

  LastFmImporter("foo", "bar").import()
}
