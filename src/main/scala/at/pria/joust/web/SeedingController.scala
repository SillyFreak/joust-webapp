package at.pria.joust.web

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.model._
import at.pria.joust.service._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class SeedingController {
  @Autowired
  private[this] var tournamentService: TournamentService = _
  @Autowired
  private[this] var gameRepo: GameRepository = _
  @Autowired
  private[this] var teamRepo: TeamRepository = _
  @Autowired
  private[this] var slotService: SlotService = _

  @RequestMapping(value = Array("/admin/seeding/"), method = Array(RequestMethod.GET))
  def seedingAdmin(model: Model) = {
    val tInfo = tournamentService("Botball").getOrElse { throw new NotFoundException }

    model.addAttribute("tournament", tInfo.tournament)
    model.addAttribute(new SeedingInput())
    "joust/seeding_admin"
  }

  @RequestMapping(value = Array("/admin/seeding/"), method = Array(RequestMethod.POST))
  def seedingAdminPost(model: Model, in: SeedingInput) = {
    val team = teamRepo.findOne(in.teamId)
    in.item match {
      case "practiceCall" =>
        slotService.addPracticeSlot(team)
      case "s0Call" =>
        slotService.addSeedingSlot(team.seedingGames(0))
      case "s1Call" =>
        slotService.addSeedingSlot(team.seedingGames(1))
      case "s2Call" =>
        slotService.addSeedingSlot(team.seedingGames(2))
      case "p1doc" =>
        team.p1doc = in.score
        teamRepo.save(team)
      case "p2doc" =>
        team.p2doc = in.score
        teamRepo.save(team)
      case "p3doc" =>
        team.p3doc = in.score
        teamRepo.save(team)
      case "onsite" =>
        team.onsite = in.score
        teamRepo.save(team)
      case "s0" =>
        val game = team.seedingGames(0)
        game.finished = true
        game.score = in.score
        gameRepo.save(game)
      case "s1" =>
        val game = team.seedingGames(1)
        game.finished = true
        game.score = in.score
        gameRepo.save(game)
      case "s2" =>
        val game = team.seedingGames(2)
        game.finished = true
        game.score = in.score
        gameRepo.save(game)
    }

    seedingAdmin(model)
  }

  @RequestMapping(value = Array("/seeding/"), method = Array(RequestMethod.GET))
  def seeding(model: Model) = {
    val tInfo = tournamentService("Botball").getOrElse { throw new NotFoundException }

    model.addAttribute("tournament", tInfo.tournament)
    "joust/seeding"
  }
}

class SeedingInput {
  @BeanProperty var teamId: Long = _
  @BeanProperty var item: String = _
  @BeanProperty var score: Int = _
}
