package chronicle

import java.util.UUID

// Domain classes
// "mbid" is an optional MusicBrainz ID to uniquely identify the
// entity better than a name alone could.

case class Artist(name: String,
                  mbid: Option[UUID])

case class Track(name: String,
                 artist: Artist,
                 mbid: Option[UUID])
