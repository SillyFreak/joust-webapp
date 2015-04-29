/**
 * AerialService.scala
 *
 * Created on 26.04.2015
 */

package at.pria.joust.service

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.model._
import at.pria.joust.model.TableSlot._
import at.pria.joust.service.TournamentService.{ TournamentInfo => TInfo }

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

import java.util.{ List => juList }

/**
 * <p>
 * {@code AerialService}
 * </p>
 *
 * @version V0.0 26.04.2015
 * @author SillyFreak
 */
@Service
class AerialService {
  @Autowired
  private[this] var gameRepo: GameRepository = _

  def dayScores(team: Team, day: Int) = ??? : List[Int]
  def setDayScores(team: Team, day: Int, scores: List[Int]) = {
    val games = team.getAerialGames().filter(_.day == day).sortBy(_.day)

    val pairs =
      games.map(Some(_)).zipAll(scores.map(Some(_)), None, None)
    for (elem <- pairs.zipWithIndex)
      elem match {
        case ((Some(g), Some(score)), _) =>
          g.finished = true
          g.score = score
          gameRepo.save(g)
        case ((Some(g), None), _) =>
          g.finished = false
          gameRepo.save(g)
        case ((None, Some(score)), round) =>
          val g = new AerialGame
          g.tournament = team.tournament
          g.aerialTeam = team
          g.day = day
          g.round = round
          g.finished = true
          g.score = score
          gameRepo.save(g)
      }
  }
}
