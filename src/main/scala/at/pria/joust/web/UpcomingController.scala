package at.pria.joust.web

import scala.beans.BeanProperty

import at.pria.joust._
import at.pria.joust.service.InitService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import scala.collection.JavaConversions._
import java.util.{ List => juList }

@Controller
class UpcomingController {
  @Autowired
  private[this] var tournament: Tournament = _

  @Autowired
  private[this] var init: InitService = _

  @RequestMapping(value = Array("/"), method = Array(RequestMethod.GET))
  def upcoming(model: Model) = {
    init()

    val seeding = tournament.seedingRoundsList
      .filter { sr => tournament.seedingResults.result(sr.team, sr.round).isEmpty }
      .map { new Game(_) }
    val bracket = tournament.bracketMatches
      .filter { bm => tournament.bracketResults.result(bm).isEmpty }
      .map { new Game(_) }

    val games = seeding ++ bracket

    for (game <- games.take(3))
      game.called = true

    model.addAttribute("upcoming", games: juList[Game])
    model.addAttribute("ByeTeam", ByeTeam)
    "joust/upcoming"
  }

  private[this] class Game private[this] (
      @BeanProperty val id: String,
      @BeanProperty val aTeam: TeamLike,
      @BeanProperty val bTeam: TeamLike) {

    @BeanProperty var called: Boolean = _

    def this(game: SeedingRound) =
      this(s"S ${game.id + 1}", game.team, ByeTeam)

    def this(game: BracketMatch) =
      this(s"DE ${game.id + 1}", game.getATeam(), game.getBTeam())
  }
}
