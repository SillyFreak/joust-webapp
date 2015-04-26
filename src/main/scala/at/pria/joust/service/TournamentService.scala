/**
 * TournamentService.scala
 *
 * Created on 26.04.2015
 */

package at.pria.joust.service

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.model._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * <p>
 * {@code TournamentService}
 * </p>
 *
 * @version V0.0 26.04.2015
 * @author SillyFreak
 */
@Service
class TournamentService {
  @Autowired
  private[this] var tournamentRepo: TournamentRepository = _

  def apply(name: String) =
    tournamentRepo.findByName(name) match {
      case null => None
      case x    => Some(x)
    }
}
