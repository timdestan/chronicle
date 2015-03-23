package chronicle

import java.util.UUID

// Domain classes
// "mbid" is an optional MusicBrainz ID to uniquely identify the
// entity better than a name alone could.

data class Artist(val name: String,
                  val mbid:UUID?)

data class Track(val name: String,
                 val artist:Artist,
                 val mbid:UUID?)
