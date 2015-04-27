package at.pria.joust.web

import at.pria.joust.service._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class MainController {
  @Autowired
  private[this] var tournamentService: TournamentService = _

  @RequestMapping(value = Array("/error"), method = Array(RequestMethod.GET))
  def error(model: Model) = {
    model.addAttribute("tournaments", tournamentService.getTournaments())
    "error"
  }
}
