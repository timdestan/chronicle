package chronicle.enrichments

import java.io.InputStream

package object Implicits {
  implicit def enrich(stream:InputStream) = new EnrichedStream(stream)
  implicit def enrich[A](option:Option[A]) = new EnrichedOption[A](option)
}
