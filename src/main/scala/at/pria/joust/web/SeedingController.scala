package at.pria.joust.web

import scala.beans.BeanProperty

import at.pria.joust._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class SeedingController {
  @Autowired
  private[this] var tournament: Tournament = _

  @RequestMapping(value = Array("/admin/seeding/"), method = Array(RequestMethod.GET))
  def seedingAdmin(model: Model) = {
    model.addAttribute("tournament", tournament)
    model.addAttribute(new SeedingControllerInput())
    "joust/seeding_admin"
  }

  @RequestMapping(value = Array("/admin/seeding/"), method = Array(RequestMethod.POST))
  def seedingAdminPost(model: Model, teamsControllerInput: SeedingControllerInput) = {
    teamsControllerInput.apply(tournament)
    seedingAdmin(model)
  }

  @RequestMapping(value = Array("/seeding/"), method = Array(RequestMethod.GET))
  def seeding(model: Model) = {
    model.addAttribute("tournament", tournament)
    "joust/seeding"
  }
}

class SeedingControllerInput {
  @BeanProperty var team: String = _
  @BeanProperty var round: Int = _
  @BeanProperty var score: Int = _

  def apply(t: Tournament) = {
    val team = t.teams.find { _.id == this.team }.get
    t.seedingResults.result(team, round, score)
  }
}
