package at.pria.joust.web

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.model._
import at.pria.joust.model.TableSlot._
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
  private[this] var slotRepo: TableSlotRepository = _

  @Autowired
  private[this] var init: InitService = _

  @RequestMapping(value = Array("/"), method = Array(RequestMethod.GET))
  def upcoming(model: Model) = {
    init()

    val slots =
      slotRepo.findAll().toList
        .filter(_.state != FINISHED)
        //most urgent slots first
        .sortBy(-_.state)

    model.addAttribute("upcoming", slots: juList[TableSlot])
    "joust/upcoming"
  }
}
