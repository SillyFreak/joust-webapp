/**
 * InitService.scala
 *
 * Created on 24.04.2015
 */

package at.pria.joust.service

import scala.collection.JavaConversions._

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

import at.pria.joust.model._
import at.pria.joust.model.Tournament._

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

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
  @PersistenceContext
  private[this] var em: EntityManager = _

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

  private[this] def mkTournament(name: String, mode: Int = BOTBALL) = {
    val tournament = new Tournament
    tournament.name = name
    tournament.mode = mode
    tournamentRepo.save(tournament)
  }

  private[this] def mkTeam(tournament: Tournament, teamId: String, name: String) = {
    val team = new Team
    team.tournament = tournament
    team.teamId = teamId
    team.name = name
    teamRepo.save(team)
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
      gameRepo.save(game)
    }
    em.refresh(tournament)
    for (g <- bracket.games) {
      val game = g.game
      if (g.aTeam() == Some(null)) {
        game.finished = true
        game.winnerSideA = false
      } else if (g.bTeam() == Some(null)) {
        game.finished = true
        game.winnerSideA = true
      }
    }
  }

  private[this] def mkTable(description: String) = {
    val table = new Table
    table.description = description
    tableRepo.save(table)
  }

  def apply(): Unit =
    if (tournamentRepo.count() == 0) {
      { //tables
        val botball1 = mkTable("Botball Left")
        val botball2 = mkTable("Botball Right")
        val aerial = mkTable("PRIA Aerial")
        val botballPractice = mkTable("Botball Free-for-all")
      }

      { //botball
        val botball = mkTournament("Botball")
        for (i <- List(0 to 16: _*))
          mkTeam(botball, "15-%04d".format(i), "TGM")

        em.refresh(botball)
        mkSeeding(botball)
        mkBracket(botball)
      }

      { //PRIA Open
        val open = mkTournament("PRIA Open")
        for (i <- List(0 to 3: _*))
          mkTeam(open, "OPEN-%04d".format(i), "TGM")

        em.refresh(open)
        mkSeeding(open)
        mkBracket(open)
      }

      { //PRIA Aerial
        val aerial = mkTournament("PRIA Aerial", AERIAL)
        for (i <- List(0 to 4: _*))
          mkTeam(aerial, "AERIAL-%04d".format(i), "TGM")

        em.refresh(aerial)
        //TODO this is only superficially a seeding
        mkSeeding(aerial)
      }
    }
}
