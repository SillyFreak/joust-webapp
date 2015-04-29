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

import java.util.{ List => juList, ArrayList => juArrayList }

@Controller
class AerialController {
  @Autowired
  private[this] var tournamentService: TournamentService = _
  @Autowired
  private[this] var slotService: SlotService = _
  @Autowired
  private[this] var aerialService: AerialService = _

  private[this] def view(model: Model, tInfo: TInfo, admin: Boolean) = {
    model.addAttribute("tournaments", tournamentService.getTournaments())
    model.addAttribute("tName", tInfo.tournament.name)
    model.addAttribute("days", (0 until 2): juList[Int])
    model.addAttribute("tournament", tInfo.tournament)

    for (team <- tInfo.tournament.teams; (day, games) <- team.aerialGames.groupBy(_.day)) {
      val scores = games.map { game => if (game.finished) s"${game.score / 10d}" else "" }
      val key = s"d${day}t${team.id}score"
      val score = scores.mkString("; ")
      model.addAttribute(key, score)
    }

    if (admin) {
      //for form processing
      model.addAttribute(new AerialInput())
    }

    if (admin) "joust/aerial_admin" else "joust/aerial"
  }

  @RequestMapping(value = Array("/admin/aerial/{tournament}/"), method = Array(RequestMethod.GET))
  def seedingAdmin(model: Model, @PathVariable("tournament") t: String) = {
    val tInfo = tournamentService(t).getOrElse { throw new NotFoundException }
    if (!tInfo.tournament.isAerialMode) throw new NotFoundException
    view(model, tInfo, true)
  }

  @RequestMapping(value = Array("/admin/aerial/{tournament}/"), method = Array(RequestMethod.POST))
  def seedingAdminPost(model: Model, in: AerialInput, @PathVariable("tournament") t: String) = {
    val tInfo = tournamentService(t).getOrElse { throw new NotFoundException }
    if (!tInfo.tournament.isAerialMode) throw new NotFoundException

    def scores(team: Long, day: Int, scores: String) =
      aerialService.setDayScores(tInfo.team(in.teamId), day, List(scores.split("\\s*;\\s*"): _*).map { s =>
        (java.lang.Double.parseDouble(s) * 10).intValue()
      })

    in.item match {
      case "practiceCall" => slotService.addPracticeSlot(tInfo.team(in.teamId))
      case "aDay0Call"    => //TODO
      case "aDay1Call"    => //TODO
      case "aDay0Score"   => scores(in.teamId, 0, in.scores)
      case "aDay1Score"   => scores(in.teamId, 1, in.scores)
    }

    view(model, tInfo, true)
  }

  @RequestMapping(value = Array("/aerial/{tournament}/"), method = Array(RequestMethod.GET))
  def seeding(model: Model, @PathVariable("tournament") t: String) = {
    val tInfo = tournamentService(t).getOrElse { throw new NotFoundException }
    if (!tInfo.tournament.isAerialMode) throw new NotFoundException
    view(model, tInfo, false)
  }
}

class AerialInput {
  @BeanProperty var teamId: Long = _
  @BeanProperty var item: String = _
  @BeanProperty var scores: String = _
}
