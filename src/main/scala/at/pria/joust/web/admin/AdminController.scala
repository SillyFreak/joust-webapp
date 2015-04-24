/**
 * AdminController.scala
 *
 * Created on 24.04.2015
 */

package at.pria.joust.web.admin

import at.pria.joust.model._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * <p>
 * {@code AdminController}
 * </p>
 *
 * @version V0.0 24.04.2015
 * @author SillyFreak
 */
@Controller
class AdminController {
  @Autowired
  private[this] var tournaments: TournamentRepository = _

  @RequestMapping(value = Array("/admin/"), method = Array(RequestMethod.GET))
  def admin(model: Model) = {
    model.addAttribute("tournaments", tournaments.findAll())
    model.addAttribute("newTournament", new Tournament)
    "joust/admin"
  }

  @RequestMapping(value = Array("/admin/"), method = Array(RequestMethod.POST))
  def adminPost(model: Model, @ModelAttribute("newTournament") newTournament: Tournament, bindingResult: BindingResult) = {
    if (!bindingResult.hasErrors()) {
      tournaments.save(newTournament)
      model.addAttribute("newTournamentSuccess", true)
    }

    admin(model)
  }
}
