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
        mkTeam(botball, "15-0233", "iBot αlpha")
        mkTeam(botball, "15-0270", "Hayah International Academy")
        mkTeam(botball, "15-0362", "Primotic")
        mkTeam(botball, "15-0364", "Eat, Sleep, Programm, Repeat")
        mkTeam(botball, "15-0367", "TGM Battledroids")
        mkTeam(botball, "15-0369", "3oT")
        mkTeam(botball, "15-0385", "HTBLVA Spengergasse")
        mkTeam(botball, "15-0397", "RPA")
        mkTeam(botball, "15-0400", "Schiffrad")
        mkTeam(botball, "15-0403", "Queen Elizabeth's Grammar School for Boys")
        mkTeam(botball, "15-0411", "International School of Islamabad Society")
        mkTeam(botball, "15-0423", "Eastbrook Comprehensive School, Daggenham")
        mkTeam(botball, "15-0536", "Al ru'ya Bilingual School of Kuwait")
        mkTeam(botball, "15-0600", "Scorp Robotics")
        mkTeam(botball, "15-0602", "items")
        mkTeam(botball, "15-0603", "SCIPIC")
        mkTeam(botball, "15-0616", "MiracleRobotics")
        mkTeam(botball, "15-0647", "The Franciszek Leja State School in Grodzisko Gorne")

        em.refresh(botball)
        mkSeeding(botball)
        mkBracket(botball)
      }

      { //PRIA Open
        val open = mkTournament("PRIA Open")
        mkTeam(open, "OP-0001", "HTL Saalfelden/St. Johann")
        mkTeam(open, "OP-0002", "ACE-Bots")
        mkTeam(open, "OP-0003", "GG-OPEN")
        mkTeam(open, "OP-0004", "HTL Saalfelden")
        mkTeam(open, "OP-0005", "Robot0nFire")

        em.refresh(open)
        mkSeeding(open)
        mkBracket(open)
      }

      { //PRIA Aerial
        val aerial = mkTournament("PRIA Aerial", AERIAL)
        mkTeam(aerial, "AE-0001", "GG-AERIAL")
        mkTeam(aerial, "AE-0002", "Johannes Wang")
        mkTeam(aerial, "AE-0003", "LastMinute-Flight")
        mkTeam(aerial, "AE-0004", "PRIA Allstars")

        em.refresh(aerial)
        //TODO this is only superficially a seeding
        mkSeeding(aerial)
      }
    }
}
