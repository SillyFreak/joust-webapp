package at.pria.joust.webapp.web.joust;

import at.pria.joust.ByeTeam$;
import at.pria.joust.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BracketController {
    @Autowired
    private Tournament tournament;

    @RequestMapping(value = "/admin/bracket/", method = RequestMethod.GET)
    public String adminBracket(Model model) {
        model.addAttribute("tournament", tournament);
        model.addAttribute("ByeTeam", ByeTeam$.MODULE$);
        return "joust/bracket_admin";
    }

    @RequestMapping(value = "/bracket/", method = RequestMethod.GET)
    public String bracket(Model model) {
        model.addAttribute("tournament", tournament);
        model.addAttribute("ByeTeam", ByeTeam$.MODULE$);
        return "joust/bracket";
    }
}
