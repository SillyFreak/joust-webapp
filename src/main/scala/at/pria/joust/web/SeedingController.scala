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

  @RequestMapping(value = Array("/admin/seeding/"), method = Array(RequestMethod.GET))
  def seedingAdmin(model: Model) = {
    val tournament = tournamentRepo.findByName("Botball")

    model.addAttribute("tournament", tournament)
    model.addAttribute(new SeedingControllerInput())
    "joust/seeding_admin"
  }

  @RequestMapping(value = Array("/admin/seeding/"), method = Array(RequestMethod.POST))
  def seedingAdminPost(model: Model, teamsControllerInput: SeedingControllerInput) = {
    val game = gameRepo.findOne(teamsControllerInput.id).asInstanceOf[SeedingGame]
    game.finished = true
    game.score = teamsControllerInput.score
    gameRepo.save(game)

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
  @BeanProperty var id: Long = _
  @BeanProperty var score: Int = _
}
