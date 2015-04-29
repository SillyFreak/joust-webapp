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

  def apply(): Unit = {
    //tables
    if (tableRepo.count == 0) {
      val botball1 = mkTable("Botball GT 1")
      val botball2 = mkTable("Botball GT 2")
      val aerial = mkTable("PRIA Aerial")
      val botballPractice = mkTable("Botball Free-for-all")
    }

    def tournament(name: String, mode: Int = BOTBALL)(init: Tournament => Unit) =
      if (tournamentRepo.findByName(name) == null) {
        println(s"new tournament: $name")
        init(mkTournament(name, mode))
      }

    tournament("Botball") { botball =>
      mkTeam(botball, "15-0233", "iBot αlpha")
      mkTeam(botball, "15-0270", "Hayah International Academy")
      mkTeam(botball, "15-0362", "Primotic")
      mkTeam(botball, "15-0364", "Eat, Sleep, Program, Repeat")
      mkTeam(botball, "15-0367", "TGM Battledroids")
      mkTeam(botball, "15-0369", "3oT")
      mkTeam(botball, "15-0385", "HTBLVA Spengergasse")
      mkTeam(botball, "15-0397", "RPA")
      mkTeam(botball, "15-0400", "Schiffrad")
      //mkTeam(botball, "15-0403", "Queen Elizabeth's Grammar School for Boys")
      //mkTeam(botball, "15-0411", "International School of Islamabad Society")
      //mkTeam(botball, "15-0423", "Eastbrook Comprehensive School, Daggenham")
      mkTeam(botball, "15-0536", "Al ru'ya Bilingual School of Kuwait")
      mkTeam(botball, "15-0600", "Scorp Robotics")
      mkTeam(botball, "15-0601", "StormRobotics")
      mkTeam(botball, "15-0602", "items")
      mkTeam(botball, "15-0603", "SCIPIC")
      //mkTeam(botball, "15-0616", "MiracleRobotics")
      mkTeam(botball, "15-0647", "The Franciszek Leja State School in Grodzisko Gorne")
      mkTeam(botball, "15-1000", "HTL Saalfelden/St. Johann")
      mkTeam(botball, "15-1001", "HTL Saalfelden")

      em.refresh(botball)
      mkSeeding(botball)
      mkBracket(botball)
    }

    tournament("PRIA Open") { open =>
      mkTeam(open, "OP-0001", "ACE-Bots")
      mkTeam(open, "OP-0002", "GG-OPEN")
      mkTeam(open, "OP-0003", "Robot0nFire")
      mkTeam(open, "OP-0004", "Andrej")

      em.refresh(open)
      mkSeeding(open)
      mkBracket(open)
    }

    tournament("PRIA Aerial", AERIAL) { aerial =>
      mkTeam(aerial, "AE-0001", "GG-AERIAL")
      mkTeam(aerial, "AE-0002", "Johannes Wang")
      mkTeam(aerial, "AE-0003", "LastMinute-Flight")
    }

    tournament("Botball Alliance") { alliance =>
      mkTeam(alliance, "15-1000 & 15-0369", "St. Johann & 3oT")
      mkTeam(alliance, "15-1001 & 15-0603", "Saalfelden & SCIPIC")
      mkTeam(alliance, "15-0385 & 15-0602", "Spengergasse & Items")
      mkTeam(alliance, "15-0600 & 15-0367", "Scorp & Battledroids")
      mkTeam(alliance, "15-0270 & 15-0362", "Hayah & Primotic")
      mkTeam(alliance, "15-0397 & 15-0647", "RPA & Grdzisko Gorne")
      mkTeam(alliance, "OP-0002 & 15-0400", "GG-OPEN & Schiffrad")
    }
  }
}
