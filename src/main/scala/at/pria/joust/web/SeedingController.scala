package at.pria.joust.web

import scala.beans.BeanProperty

import at.pria.joust.model._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class SeedingController {
  @Autowired
  private[this] var tournamentRepo: TournamentRepository = _
  @Autowired
  private[this] var gameRepo: GameRepository = _
  @Autowired
  private[this] var teamRepo: TeamRepository = _

  @RequestMapping(value = Array("/admin/seeding/"), method = Array(RequestMethod.GET))
  def seedingAdmin(model: Model) = {
    val tournament = tournamentRepo.findByName("Botball")

    model.addAttribute("tournament", tournament)
    model.addAttribute(new SeedingControllerInput())
    "joust/seeding_admin"
  }

  @RequestMapping(value = Array("/admin/seeding/"), method = Array(RequestMethod.POST))
  def seedingAdminPost(model: Model, in: SeedingControllerInput) = {
    if (in.gameId != 0) {
      val game = gameRepo.findOne(in.gameId).asInstanceOf[SeedingGame]
      game.finished = true
      game.score = in.score
      gameRepo.save(game)
    } else {
      val team = teamRepo.findOne(in.teamId)
      in.phase match {
        case "p1doc"  => team.p1doc = in.score
        case "p2doc"  => team.p2doc = in.score
        case "p3doc"  => team.p3doc = in.score
        case "onsite" => team.onsite = in.score
      }
      teamRepo.save(team)
    }

    seedingAdmin(model)
  }

  @RequestMapping(value = Array("/seeding/"), method = Array(RequestMethod.GET))
  def seeding(model: Model) = {
    val tournament = tournamentRepo.findByName("Botball")

    model.addAttribute("tournament", tournament)
    "joust/seeding"
  }
}

class SeedingControllerInput {
  @BeanProperty var teamId: Long = _
  @BeanProperty var phase: String = _
  @BeanProperty var gameId: Long = _
  @BeanProperty var score: Int = _
}
