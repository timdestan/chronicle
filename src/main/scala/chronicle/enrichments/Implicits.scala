package chronicle.enrichments

import java.io.InputStream

package object Implicits {
  implicit def enrich(stream:InputStream) = new EnrichedStream(stream)
}
