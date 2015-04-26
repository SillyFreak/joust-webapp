/**
 * SlotService.scala
 *
 * Created on 26.04.2015
 */

package at.pria.joust.service

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.model._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service

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
  private[this] var tpl: SimpMessagingTemplate = _

  def addTableSlot(slot: TableSlot) = {
    slotRepo.save(slot)
    tpl.convertAndSend("/topic/slots", SlotUpdate(0)) //TODO re-add team IDs
  }

  def addPracticeSlot(team: Team) = {
    val slot = new PracticeSlot
    slot.team = team
    addTableSlot(slot)
  }

  def addSeedingSlot(game: SeedingGame) = {
    val slot = new SeedingSlot
    slot.game = game
    addTableSlot(slot)
  }

  def addBracketSlot(game: BracketGame) = {
    val slot = new BracketSlot
    slot.game = game
    addTableSlot(slot)
  }

  def addAllianceSlot(game: AllianceGame) = {
    val slot = new AllianceSlot
    slot.game = game
    addTableSlot(slot)
  }
}

case class SlotUpdate(
  @BeanProperty teamId: Long)
