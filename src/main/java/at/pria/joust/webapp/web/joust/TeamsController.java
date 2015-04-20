package at.pria.joust.webapp.web.joust;

import at.pria.joust.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static scala.collection.JavaConversions.*;

@Controller
public class TeamsController {
    @Autowired
    private Tournament tournament;

    @RequestMapping(value = "/admin/teams/", method = RequestMethod.GET)
    public String greetingForm(Model model) {
        model.addAttribute("teams", asJavaCollection(tournament.teams()));
        return "joust/teams_admin";
    }
}
