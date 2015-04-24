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

  def apply(): Unit =
    if (tournamentRepo.count() == 0) {
      mkTournament("Botball",
        (for (i <- List(0 to 16: _*)) yield mkTeam("15-%04d".format(i), "TGM")): _*)
    }
}
