package at.pria.joust.webapp.web.joust;

import at.pria.joust.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RankingController {
    @Autowired
    private Tournament tournament;

    @RequestMapping(value = "/ranking/", method = RequestMethod.GET)
    public String adminBracket(Model model) {
        model.addAttribute("tournament", tournament);
        return "joust/ranking";
    }
}
