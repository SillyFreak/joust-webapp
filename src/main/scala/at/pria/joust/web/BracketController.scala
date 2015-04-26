package at.pria.joust.web

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.model._
import at.pria.joust.service._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import java.util.{ List => juList }

@Controller
class BracketController {
  @Autowired
  private[this] var tournamentService: TournamentService = _
  @Autowired
  private[this] var slotService: SlotService = _

  private[this] def view(model: Model, tInfo: TournamentService#TournamentInfo, admin: Boolean) = {
    val bracket = tInfo.bracket
    val games = bracket.games.map { BracketGameView(_) }

    model.addAttribute("bracket", games: juList[BracketGameView])
    if (admin) {
      //for form processing
      model.addAttribute(new BracketInput())
    }

    if (admin) "joust/bracket_admin" else "joust/bracket"
  }

  @RequestMapping(value = Array("/admin/bracket/"), method = Array(RequestMethod.GET))
  def bracketAdmin(model: Model) = {
    val tInfo = tournamentService("Botball").getOrElse { throw new NotFoundException }
    view(model, tInfo, true)
  }

  @RequestMapping(value = Array("/admin/bracket/"), method = Array(RequestMethod.POST))
  def bracketAdminPost(model: Model, in: BracketInput) = {
    val tInfo = tournamentService("Botball").getOrElse { throw new NotFoundException }

    in.item match {
      case "winnerA"   => tInfo.scoreBracketGame(in.gameId, true)
      case "winnerB"   => tInfo.scoreBracketGame(in.gameId, false)
      case "unresolve" => tInfo.unscoreBracketGame(in.gameId)
      case "call"      => slotService.addBracketSlot(tInfo.bracket.games(in.gameId).game)
    }

    view(model, tInfo, true)
  }

  @RequestMapping(value = Array("/bracket/"), method = Array(RequestMethod.GET))
  def bracket(model: Model) = {
    val tInfo = tournamentService("Botball").getOrElse { throw new NotFoundException }
    view(model, tInfo, false)
  }

  @RequestMapping(value = Array("/bracket/main/"), method = Array(RequestMethod.GET))
  def mainBracket(model: Model) = {
    val tInfo = tournamentService("Botball").getOrElse { throw new NotFoundException }
    val bracket = tInfo.bracket
    val rounds = bracket.mainRounds.map { round =>
      val first = round.first
      val last = first + round.count
      val result =
        for (id <- List(first until last: _*))
          yield BracketGameView(bracket.games(id))
      result: juList[BracketGameView]
    }

    model.addAttribute("rounds", rounds: juList[juList[BracketGameView]])
    model.addAttribute("mode", "main")
    "joust/bracket2"
  }

  @RequestMapping(value = Array("/bracket/consolation/"), method = Array(RequestMethod.GET))
  def consolationBracket(model: Model) = {
    val tInfo = tournamentService("Botball").getOrElse { throw new NotFoundException }
    val bracket = tInfo.bracket
    val rounds = bracket.consolationRounds.reverse.map { round =>
      val first = round.first
      val last = first + round.count
      val result =
        for (id <- List(first until last: _*))
          yield BracketGameView(bracket.games(id))
      result: juList[BracketGameView]
    }

    model.addAttribute("rounds", rounds: juList[juList[BracketGameView]])
    model.addAttribute("mode", "consolation")
    "joust/bracket2"
  }

  @RequestMapping(value = Array("/bracket/final/"), method = Array(RequestMethod.GET))
  def finalBracket(model: Model) = {
    val tInfo = tournamentService("Botball").getOrElse { throw new NotFoundException }
    val bracket = tInfo.bracket
    val round = {
      val round = bracket.finalRound
      val first = round.first
      val last = first + round.count
      val result =
        for (id <- List(first until last: _*))
          yield BracketGameView(bracket.games(id))
      result: juList[BracketGameView]
    }

    model.addAttribute("round", round)
    model.addAttribute("mode", "final")
    "joust/bracket2"
  }

  case class BracketGameView(
      @BeanProperty game: BracketStructure#BracketGame) {
    def getId() = game.id

    def isScorable() = {
      val a = getATeam()
      val b = getBTeam()
      (a, b) match {
        case (Some(a), Some(b)) if a != null && b != null => true
        case _ => false
      }
    }

    def isFinished() = game.finished

    def getATeam() = game.aTeam()
    def getBTeam() = game.bTeam()

    def getWinner() = game.winner()
    def getLoser() = game.loser()
  }
}

class BracketInput {
  @BeanProperty var gameId: Int = _
  @BeanProperty var item: String = _
}
