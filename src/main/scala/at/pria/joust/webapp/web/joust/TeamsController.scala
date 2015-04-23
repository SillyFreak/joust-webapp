package at.pria.joust.webapp.web.joust

import scala.beans.BeanProperty

import at.pria.joust._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class TeamsController {
  @Autowired
  private[this] var tournament: Tournament = _

  @RequestMapping(value = Array("/admin/teams/"), method = Array(RequestMethod.GET))
  def adminTeams(model: Model) = {
    model.addAttribute("tournament", tournament)
    model.addAttribute(new TeamsControllerInput())
    "joust/teams_admin"
  }

  @RequestMapping(value = Array("/admin/teams/"), method = Array(RequestMethod.POST))
  def adminTeamsPost(model: Model, teamsControllerInput: TeamsControllerInput) = {
    teamsControllerInput.apply(tournament)
    adminTeams(model)
  }

  @RequestMapping(value = Array("/teams/"), method = Array(RequestMethod.GET))
  def teams(model: Model) = {
    model.addAttribute("tournament", tournament)
    "joust/teams"
  }
}

class TeamsControllerInput {
  @BeanProperty var team: String = _
  @BeanProperty var round: Int = _
  @BeanProperty var score: Int = _

  def apply(t: Tournament) = {
    val team = t.teams.find { _.id == this.team }.get
    t.seedingResults.result(team, round, score)
  }
}
