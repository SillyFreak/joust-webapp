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
  @Autowired
  private[this] var gameRepo: GameRepository = _
  @Autowired
  private[this] var teamRepo: TeamRepository = _

  def apply(name: String) =
    tournamentRepo.findByName(name) match {
      case null => None
      case t    => Some(new TournamentInfo(t))
    }

  class TournamentInfo(val tournament: Tournament) {
    lazy val bracket = new BracketStructure(tournament)

    def team(id: Long) = teamRepo.findOne(id)

    def scoreDocumentation(teamId: Long, period: String, score: Int) = {
      val team = this.team(teamId)
      period match {
        case "p1doc"  => team.p1doc = score
        case "p2doc"  => team.p2doc = score
        case "p3doc"  => team.p3doc = score
        case "onsite" => team.onsite = score
      }
      teamRepo.save(team)
    }

    def scoreSeedingGame(teamId: Long, round: Int, score: Int) = {
      val game = team(teamId).seedingGames(round)
      game.finished = true
      game.score = score
      gameRepo.save(game)
    }

    def unscoreSeedingGame(teamId: Long, round: Int) = {
      val game = team(teamId).seedingGames(round)
      game.finished = false
      gameRepo.save(game)
    }

    def scoreBracketGame(gameId: Int, winnerSideA: Boolean) = {
      val game = bracket.games(gameId).game
      game.finished = true
      game.winnerSideA = winnerSideA
      gameRepo.save(game)
    }

    def unscoreBracketGame(gameId: Int) = {
      val game = bracket.games(gameId).game
      game.finished = false
      gameRepo.save(game)
    }
  }
}
