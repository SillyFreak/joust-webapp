package at.pria.joust.web

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.model._
import at.pria.joust.model.TableSlot._
import at.pria.joust.service._

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
  private[this] var slotService: SlotService = _
  @Autowired
  private[this] var init: InitService = _

  private[this] def view(model: Model, admin: Boolean) = {
    val slots =
      slotService.allSlots
        .filter(_.state != FINISHED)
        //most urgent slots first
        .sortBy(-_.state)

    model.addAttribute("upcoming", slots: juList[TableSlot])
    if (admin) {
      //for options
      model.addAttribute("tables", slotService.allTables: juList[Table])
    }

    if (admin) "joust/upcoming_admin" else "joust/upcoming"
  }

  @RequestMapping(value = Array("/admin/"), method = Array(RequestMethod.GET))
  def upcomingAdmin(model: Model) = {
    init()
    view(model, true)
  }

  @RequestMapping(value = Array("/admin/"), method = Array(RequestMethod.POST))
  def upcomingAdminPost(model: Model, in: UpcomingInput) = {
    in.item match {
      case "next"   => slotService.advance(in.slotId)
      case "cancel" => slotService.cancel(in.slotId)
      case "table"  => slotService.assignTable(in.slotId, slotService.table(in.table))
    }
    view(model, true)
  }

  @RequestMapping(value = Array("/"), method = Array(RequestMethod.GET))
  def upcoming(model: Model) = {
    init()
    view(model, false)
  }
}

class UpcomingInput {
  @BeanProperty var slotId: Long = _
  @BeanProperty var item: String = _
  @BeanProperty var table: Long = _
}
