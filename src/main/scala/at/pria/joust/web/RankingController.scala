package at.pria.joust.web

import scala.beans.BeanProperty

import at.pria.joust.model._
import at.pria.joust.service._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import scala.collection.JavaConversions._
import java.util.{ List => juList }

@Controller
class RankingController {
  @Autowired
  private[this] var tournamentService: TournamentService = _

  @RequestMapping(value = Array("/ranking/"), method = Array(RequestMethod.GET))
  def ranking(model: Model) = {
    val tInfo = tournamentService("Botball").getOrElse { throw new NotFoundException }
    val bracket = tInfo.bracket

    val teams = tInfo.tournament.teams.toList.map { TeamRanks(bracket, _) }

    val byOverall = teams.sortBy(_.overallRank)
    val byDoc = teams.sortBy(_.docRank)
    val bySeeding = teams.sortBy(_.seedingRank)
    val byBracket = teams.sortBy(_.bracketRank)

    val ranking =
      for ((((o, d), s), b) <- (byOverall zip byDoc zip bySeeding zip byBracket))
        yield RankItem(o, d, s, b)

    model.addAttribute("ranking", ranking: juList[RankItem])
    "joust/ranking"
  }

  case class RankItem(
    @BeanProperty overall: TeamRanks,
    @BeanProperty doc: TeamRanks,
    @BeanProperty seeding: TeamRanks,
    @BeanProperty bracket: TeamRanks)

  case class TeamRanks(
      bracket: BracketStructure,
      @BeanProperty team: Team) {
    @BeanProperty val overallScore = team.overallScore(bracket)
    @BeanProperty val overallRank = team.overallRank(bracket)

    @BeanProperty val docScore = team.docScore
    @BeanProperty val docRank = team.docRank

    @BeanProperty val seedingScore = team.seedingScore
    @BeanProperty val seedingRank = team.seedingRank

    @BeanProperty val bracketScore = team.bracketScore(bracket)
    @BeanProperty val bracketRank = team.bracketRank(bracket)
  }
}
