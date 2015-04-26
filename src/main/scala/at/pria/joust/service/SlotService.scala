/**
 * SlotService.scala
 *
 * Created on 26.04.2015
 */

package at.pria.joust.service

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.model._
import at.pria.joust.model.TableSlot._
import at.pria.joust.service.TournamentService.{ TournamentInfo => TInfo }

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

import java.util.{ List => juList }

/**
 * <p>
 * {@code SlotService}
 * </p>
 *
 * @version V0.0 26.04.2015
 * @author SillyFreak
 */
@Service
class SlotService {
  @Autowired
  private[this] var slotRepo: TableSlotRepository = _
  @Autowired
  private[this] var tableRepo: TableRepository = _
  @Autowired
  private[this] var tpl: SimpMessagingTemplate = _
  @Autowired
  private[this] var tournamentService: TournamentService = _

  def allSlots = slotRepo.findAll().toList
  def slot(id: Long) = slotRepo.findOne(id)

  def allTables = tableRepo.findAll().toList
  def table(id: Long) = tableRepo.findOne(id)

  private[this] def addTableSlot(slot: TableSlot) = {
    slotRepo.save(slot)
  }

  def addPracticeSlot(team: Team) = {
    val slot = new PracticeSlot
    slot.team = team
    addTableSlot(slot)
  }

  def addSeedingSlot(game: SeedingGame) = {
    val slot = new SeedingSlot
    slot.sGame = game
    addTableSlot(slot)
  }

  def addBracketSlot(game: BracketGame) = {
    val slot = new BracketSlot
    slot.bGame = game
    addTableSlot(slot)
  }

  def addAllianceSlot(game: AllianceGame) = {
    val slot = new AllianceSlot
    slot.aGame = game
    addTableSlot(slot)
  }

  def advance(id: Long) = {
    val slot = this.slot(id)
    slot.state += 1
    slotRepo.save(slot)

    if (slot.state == CALLED) {
      implicit val tInfo = tournamentService(slot.tournament)
      val teamIds = slot.participants.map(_.id)
      tpl.convertAndSend("/topic/slots", SlotUpdate(teamIds))
    }
  }

  def cancel(id: Long) = {
    slotRepo.delete(id)
  }

  def assignTable(id: Long, table: Table) = {
    val slot = this.slot(id)
    slot.table = table
    slotRepo.save(slot)
  }
}

case class SlotUpdate(
  @BeanProperty teamIds: juList[Long])
