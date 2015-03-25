package chronicle.lastfm

import chronicle.Listen
import scala.concurrent.Future

object ListeningHistory {
  def forUser(user: User, config: Config):Future[List[Listen]] = ???
}