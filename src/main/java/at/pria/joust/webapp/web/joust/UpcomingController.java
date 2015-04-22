package at.pria.joust.webapp.web.joust;

import at.pria.joust.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedList;
import java.util.List;

@Controller
public class UpcomingController {
    @Autowired
    private Tournament tournament;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String upcoming(Model model) {
        List<Game> games = new LinkedList<>();
        for (SeedingRound sr : tournament.getSeedingRounds())
            if (tournament.getSeedingResults().getResult(sr.getTeam(), sr.getRound()) == null)
                games.add(new Game(sr));
        for (BracketMatch bm : tournament.getBracketMatches())
            if (tournament.getBracketResults().getResult(bm) == null)
                games.add(new Game(bm));

        model.addAttribute("upcoming", games);
        model.addAttribute("ByeTeam", ByeTeam$.MODULE$);
        return "joust/upcoming";
    }

    private static class Game {
        private final String id;
        private final TeamLike aTeam;
        private final TeamLike bTeam;

        public Game(SeedingRound game) {
            id = "S " + (game.getId() + 1);
            aTeam = game.getTeam();
            bTeam = ByeTeam$.MODULE$;
        }

        public Game(BracketMatch game) {
            id = "DE " + (game.getId() + 1);
            aTeam = game.getATeam();
            bTeam = game.getBTeam();
        }

        public String getId() {
            return id;
        }

        public TeamLike getATeam() {
            return aTeam;
        }

        public TeamLike getBTeam() {
            return bTeam;
        }
    }
}
