/**
 * InitService.scala
 *
 * Created on 24.04.2015
 */

package at.pria.joust.service

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

import at.pria.joust.model._

import scala.collection.JavaConversions._

/**
 * <p>
 * {@code InitService}
 * </p>
 *
 * @version V0.0 24.04.2015
 * @author SillyFreak
 */
@Service
class InitService {
  @Autowired
  private[this] var tournamentRepo: TournamentRepository = _
  @Autowired
  private[this] var teamRepo: TeamRepository = _
  @Autowired
  private[this] var gameRepo: GameRepository = _

  private[this] def mkTeam(teamId: String, name: String) = {
    val t = new Team
    t.teamId = teamId
    t.name = name
    teamRepo.save(t)
  }

  private[this] def mkTournament(name: String, teams: Team*) = {
    val tournament = new Tournament
    tournament.name = name
    tournament.teams.addAll(teams)
    tournamentRepo.save(tournament)
  }

  private[this] def mkSeeding(tournament: Tournament) = {
    for (round <- 0 until 3; team <- tournament.teams) {
      val game = new SeedingGame
      game.tournament = tournament
      game.team = team
      game.round = round
      gameRepo.save(game)
    }
  }

  private[this] def mkBracket(tournament: Tournament) = {
    val bracket = new BracketStructure(tournament)
    for (g <- bracket.games) {
      val game = new BracketGame
      game.tournament = tournament
      game.gameId = g.id
      if (g.aTeam == bracket.ByeTeamSource) {
        game.finished = true
        game.winnerSideA = false
      } else if (g.bTeam == bracket.ByeTeamSource) {
        game.finished = true
        game.winnerSideA = true
      }
      gameRepo.save(game)
    }
  }

  def apply(): Unit =
    if (tournamentRepo.count() == 0) {
      val botball =
        mkTournament("Botball",
          (for (i <- List(0 to 16: _*)) yield mkTeam("15-%04d".format(i), "TGM")): _*)
      mkSeeding(botball)
      mkBracket(botball)
    }
}
