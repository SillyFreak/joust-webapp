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
class SeedingController {
  @Autowired
  private[this] var tournamentService: TournamentService = _
  @Autowired
  private[this] var slotService: SlotService = _

  private[this] def view(model: Model, tInfo: TInfo, admin: Boolean) = {
    model.addAttribute("tournaments", tournamentService.getTournaments())
    model.addAttribute("tName", tInfo.tournament.name)
    model.addAttribute("tournament", tInfo.tournament)
    if (admin) {
      //for form processing
      model.addAttribute(new SeedingInput())
    }

    if (admin) "joust/seeding_admin" else "joust/seeding"
  }

  @RequestMapping(value = Array("/admin/seeding/{tournament}/"), method = Array(RequestMethod.GET))
  def seedingAdmin(model: Model, @PathVariable("tournament") t: String) = {
    val tInfo = tournamentService(t).getOrElse { throw new NotFoundException }
    if (!tInfo.tournament.isBotballMode) throw new NotFoundException
    view(model, tInfo, true)
  }

  @RequestMapping(value = Array("/admin/seeding/{tournament}/"), method = Array(RequestMethod.POST))
  def seedingAdminPost(model: Model, in: SeedingInput, @PathVariable("tournament") t: String) = {
    val tInfo = tournamentService(t).getOrElse { throw new NotFoundException }
    if (!tInfo.tournament.isBotballMode) throw new NotFoundException

    val team = tInfo.team(in.teamId)
    in.item match {
      case period @ ("p1doc" | "p2doc" | "p3doc" | "onsite" | "paper") =>
        tInfo.scoreDocumentation(in.teamId, period, in.score)
      case "practiceCall" => slotService.addPracticeSlot(team)
      case "s0Call"       => slotService.addSeedingSlot(team.seedingGames(0))
      case "s1Call"       => slotService.addSeedingSlot(team.seedingGames(1))
      case "s2Call"       => slotService.addSeedingSlot(team.seedingGames(2))
      case "s0"           => tInfo.scoreSeedingGame(in.teamId, 0, in.score)
      case "s1"           => tInfo.scoreSeedingGame(in.teamId, 1, in.score)
      case "s2"           => tInfo.scoreSeedingGame(in.teamId, 2, in.score)
    }

    view(model, tInfo, true)
  }

  @RequestMapping(value = Array("/seeding/{tournament}/"), method = Array(RequestMethod.GET))
  def seeding(model: Model, @PathVariable("tournament") t: String) = {
    val tInfo = tournamentService(t).getOrElse { throw new NotFoundException }
    if (!tInfo.tournament.isBotballMode) throw new NotFoundException
    view(model, tInfo, false)
  }
}

class SeedingInput {
  @BeanProperty var teamId: Long = _
  @BeanProperty var item: String = _
  @BeanProperty var score: Int = _
}
