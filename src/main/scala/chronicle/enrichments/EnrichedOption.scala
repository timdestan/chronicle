package chronicle.enrichments

import scala.concurrent.{Future, Promise}

class NoValue extends Exception

class EnrichedOption[+A](option: Option[A]) {

  // Turn us into a future that is immediately completed.
  // Represent None with a NoValue exception.
  def toFuture():Future[A] = {
    val promise = Promise[A]()
    option match {
      case Some(value) => promise.success(value)
      case None => promise.failure(new NoValue())
    }
    promise.future
  }
}