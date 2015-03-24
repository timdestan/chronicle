package chronicle.enrichments

import scala.io.Source
import java.io.InputStream

class EnrichedStream(stream: InputStream) {
  def readText():String = Source.fromInputStream(stream).mkString
}
