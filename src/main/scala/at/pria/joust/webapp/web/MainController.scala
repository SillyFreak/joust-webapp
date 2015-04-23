package at.pria.joust.webapp.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class MainController {
  @RequestMapping(value = Array("/error"), method = Array(RequestMethod.GET))
  def error(model: Model) = "error"
}
