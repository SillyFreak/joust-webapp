package at.pria.joust.webapp.web.joust

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust._

import java.util.{ List => juList }

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class BracketController {
  @Autowired
  private[this] var tournament: Tournament = _

  @RequestMapping(value = Array("/admin/bracket/"), method = Array(RequestMethod.GET))
  def adminBracket(model: Model) = {
    model.addAttribute("bracket", new Bracket())
    model.addAttribute("ByeTeam", ByeTeam)
    model.addAttribute(new BracketControllerInput())
    "joust/bracket_admin"
  }

  @RequestMapping(value = Array("/admin/bracket/"), method = Array(RequestMethod.POST))
  def adminBracketPost(model: Model, bracketControllerInput: BracketControllerInput) = {
    bracketControllerInput.apply(tournament)
    adminBracket(model)
  }

  @RequestMapping(value = Array("/bracket/"), method = Array(RequestMethod.GET))
  def bracket(model: Model) = {
    model.addAttribute("bracket", new Bracket())
    model.addAttribute("ByeTeam", ByeTeam)
    "joust/bracket"
  }

  @RequestMapping(value = Array("/bracket/main/"), method = Array(RequestMethod.GET))
  def mainBracket(model: Model) = {
    model.addAttribute("bracket", new Bracket())
    model.addAttribute("mode", "main")
    model.addAttribute("ByeTeam", ByeTeam)
    "joust/bracket2"
  }

  @RequestMapping(value = Array("/bracket/consolation/"), method = Array(RequestMethod.GET))
  def consolationBracket(model: Model) = {
    model.addAttribute("bracket", new Bracket())
    model.addAttribute("mode", "consolation")
    model.addAttribute("ByeTeam", ByeTeam)
    "joust/bracket2"
  }

  @RequestMapping(value = Array("/bracket/final/"), method = Array(RequestMethod.GET))
  def finalBracket(model: Model) = {
    model.addAttribute("bracket", new Bracket())
    model.addAttribute("mode", "final")
    model.addAttribute("ByeTeam", ByeTeam)
    "joust/bracket2"
  }

  case class BracketMatchView(
      @BeanProperty id: Int,
      @BeanProperty ord: Int,
      @BeanProperty aTeam: TeamLike,
      @BeanProperty bTeam: TeamLike,
      @BeanProperty winner: TeamLike) {

    def this(game: BracketMatch) =
      this(game.id, game.ord, game.getATeam(), game.getBTeam(), tournament.bracketResults.result(game) match {
        case None                               => null
        case Some(BracketMatchResult(_, true))  => game.getATeam()
        case Some(BracketMatchResult(_, false)) => game.getBTeam()
      })
  }

  private class Bracket {
    private[this] val m = tournament.bracketMatches.map(new BracketMatchView(_))

    private[this] val brackets = {
      val _rounds = m.groupBy(_.ord)
      val size = _rounds.size
      val rounds =
        _rounds.groupBy {
          case (ord, a) => ord match {
            case 0                  => "main"
            case x if x >= size - 3 => "final"
            case x if x % 3 == 1    => "main"
            case _                  => "consolation"
          }
        }

      rounds.map {
        case (k, round) =>
          val bracket =
            round.toList
              .sortBy { case (k, _) => k }
              .map {
                case (_, v) => v: juList[BracketMatchView]
              }
          (k, (if (k == "consolation") bracket.reverse else bracket): juList[juList[BracketMatchView]])
      }
    }

    @BeanProperty val games = m: juList[BracketMatchView]

    @BeanProperty val mainRounds = brackets("main")
    @BeanProperty val consolationRounds = brackets("consolation")
    @BeanProperty val finalRounds = brackets("final")
  }
}

class BracketControllerInput {
  @BeanProperty var id: Int = _
  @BeanProperty var winnerSideA: Boolean = _
  @BeanProperty var resolved: Boolean = _

  def apply(t: Tournament) = {
    val game = t.getBracketMatches().get(id)
    val result = if (resolved) Some(winnerSideA) else None
    t.bracketResults.result(game, result)
  }
}
