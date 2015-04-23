package at.pria.joust.webapp.web.joust

import scala.beans.BeanProperty

import at.pria.joust._

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
  private[this] var tournament: Tournament = _

  @RequestMapping(value = Array("/ranking/"), method = Array(RequestMethod.GET))
  def adminBracket(model: Model) = {
    val ov = tournament.overallResults.ranking
    val doc = tournament.documentationResults.ranking
    val seed = tournament.seedingResults.ranking
    val br = tournament.bracketResults.ranking

    val ranking =
      for ((((o, d), s), b) <- (ov zip doc zip seed zip br))
        yield RankItem(o, d, s, b)

    model.addAttribute("tournament", tournament)
    model.addAttribute("ranking", ranking: juList[RankItem])
    "joust/ranking"
  }

  case class RankItem(
    @BeanProperty overall: OverallScore,
    @BeanProperty doc: DocumentationScore,
    @BeanProperty seed: SeedingScore,
    @BeanProperty de: BracketScore)
}
