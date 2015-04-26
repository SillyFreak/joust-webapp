package at.pria.joust.web

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.model._
import at.pria.joust.model.Tournament._
import at.pria.joust.service._
import at.pria.joust.service.TournamentService.{ TournamentInfo => TInfo }

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class AerialController {
  @Autowired
  private[this] var tournamentService: TournamentService = _
  @Autowired
  private[this] var slotService: SlotService = _

  private[this] def view(model: Model, tInfo: TInfo, admin: Boolean) = {
    model.addAttribute("tName", tInfo.tournament.name)
    model.addAttribute("tournament", tInfo.tournament)
    if (admin) {
      //for form processing
      model.addAttribute(new AerialInput())
    }

    if (admin) "joust/aerial_admin" else "joust/aerial"
  }

  @RequestMapping(value = Array("/admin/aerial/{tournament}/"), method = Array(RequestMethod.GET))
  def seedingAdmin(model: Model, @PathVariable("tournament") t: String) = {
    val tInfo = tournamentService(t).getOrElse { throw new NotFoundException }
    if (tInfo.tournament.mode != AERIAL) throw new NotFoundException
    view(model, tInfo, true)
  }

  @RequestMapping(value = Array("/admin/aerial/{tournament}/"), method = Array(RequestMethod.POST))
  def seedingAdminPost(model: Model, in: AerialInput, @PathVariable("tournament") t: String) = {
    val tInfo = tournamentService(t).getOrElse { throw new NotFoundException }
    if (tInfo.tournament.mode != AERIAL) throw new NotFoundException

    //TODO

    view(model, tInfo, true)
  }

  @RequestMapping(value = Array("/aerial/{tournament}/"), method = Array(RequestMethod.GET))
  def seeding(model: Model, @PathVariable("tournament") t: String) = {
    val tInfo = tournamentService(t).getOrElse { throw new NotFoundException }
    if (tInfo.tournament.mode != AERIAL) throw new NotFoundException
    view(model, tInfo, false)
  }
}

class AerialInput {
  @BeanProperty var teamId: Long = _
  @BeanProperty var item: String = _
  @BeanProperty var score: Int = _
}
