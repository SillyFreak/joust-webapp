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
  @Autowired
  private[this] var tableRepo: TableRepository = _
  @Autowired
  private[this] var tableSlotRepo: TableSlotRepository = _

  private[this] def mkTournament(name: String) = {
    val tournament = new Tournament
    tournament.name = name
    tournamentRepo.save(tournament)
  }

  private[this] def mkTeam(tournament: Tournament, teamId: String, name: String) = {
    val team = new Team
    team.tournament = tournament
    team.teamId = teamId
    team.name = name
    val result = teamRepo.save(team)
    //TODO can I avoid this by "refreshing" the tournament?
    tournament.teams.add(result)
    result
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

  private[this] def mkTable(description: String) = {
    val table = new Table
    table.description = description
    tableRepo.save(table)
  }

  def apply(): Unit =
    if (tournamentRepo.count() == 0) {
      val botball = mkTournament("Botball")
      for (i <- List(0 to 16: _*))
        mkTeam(botball, "15-%04d".format(i), "TGM")
      mkSeeding(botball)
      mkBracket(botball)

      val table1 = mkTable("Botball 1")
      val slot1 = {
        val slot = new PracticeSlot
        slot.table = table1
        slot.team = botball.teams(0)
        tableSlotRepo.save(slot)
      }
      val slot2 = {
        val slot = new PracticeSlot
        slot.table = table1
        slot.team = botball.teams(1)
        slot.state = TableSlot.CALLED
        tableSlotRepo.save(slot)
      }
    }
}
