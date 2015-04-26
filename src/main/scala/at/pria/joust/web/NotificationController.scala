package at.pria.joust.web

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.model._
import at.pria.joust.service._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class NotificationController {
  @Autowired
  private[this] var init: InitService = _
  @Autowired
  private[this] var teamRepo: TeamRepository = _

  @RequestMapping(value = Array("/notification/"), method = Array(RequestMethod.GET))
  def notificationAdmin(model: Model) = {
    init()
    model.addAttribute("teams", teamRepo.findAll())
    "joust/notification"
  }
}
