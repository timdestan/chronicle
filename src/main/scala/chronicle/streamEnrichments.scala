package chronicle

import scala.io.Source
import java.io.InputStream;

class EnrichedStream(stream: InputStream) {
  def readText():String = Source.fromInputStream(stream).mkString
}

object StreamEnrichments {
  implicit def enrich(stream:InputStream) = new EnrichedStream(stream)
}
