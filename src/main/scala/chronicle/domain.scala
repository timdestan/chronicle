package chronicle

import java.util.UUID

import org.joda.time.Instant

// Domain classes
// "mbid" is an optional MusicBrainz ID to uniquely identify the
// entity better than a name alone could.

case class Artist(name: String,
                  mbid: Option[UUID])

case class Track(name: String,
                 artist: Artist,
                 mbid: Option[UUID])

// A single time that a person listened to a particular track.
case class Listen(track: Track,
                  time: Instant)
