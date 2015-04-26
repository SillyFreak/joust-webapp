/**
 * SlotUpdateController.scala
 *
 * Created on 26.04.2015
 */

package at.pria.joust.ws

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.model._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

import java.util.{ List => juList }

/**
 * <p>
 * {@code SlotUpdateController}
 * </p>
 *
 * @version V0.0 26.04.2015
 * @author SillyFreak
 */
@Controller
class SlotUpdateController {
  @Autowired
  private[this] var teamRepo: TeamRepository = _

  @MessageMapping(Array("/test"))
  @SendTo(Array("/topic/slots"))
  def test() = {
    val team = teamRepo.findOne(1)
    slotUpdate(team)
  }

  def slotUpdate(team: Team) = SlotUpdate(team.id)
}

case class SlotUpdate(
  @BeanProperty teamId: Long)
