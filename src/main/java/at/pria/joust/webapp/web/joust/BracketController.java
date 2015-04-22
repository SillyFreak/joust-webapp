package at.pria.joust.webapp.web.joust;

import at.pria.joust.BracketMatch;
import at.pria.joust.BracketMatchResult;
import at.pria.joust.ByeTeam$;
import at.pria.joust.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@Controller
public class BracketController {
    @Autowired
    private Tournament tournament;

    @RequestMapping(value = "/admin/bracket/", method = RequestMethod.GET)
    public String adminBracket(Model model) {
        model.addAttribute("bracket", new Bracket(tournament));
        model.addAttribute("ByeTeam", ByeTeam$.MODULE$);
        return "joust/bracket_admin";
    }

    @RequestMapping(value = "/bracket/", method = RequestMethod.GET)
    public String bracket(Model model) {
        model.addAttribute("bracket", new Bracket(tournament));
        model.addAttribute("ByeTeam", ByeTeam$.MODULE$);
        return "joust/bracket";
    }

    @RequestMapping(value = "/bracket2/", method = RequestMethod.GET)
    public String bracket2(Model model) {
        model.addAttribute("bracket", new Bracket(tournament));
        model.addAttribute("ByeTeam", ByeTeam$.MODULE$);
        return "joust/bracket2";
    }

    private static class Bracket {
        private final List<List<Map<String, Object>>> rounds;
        private final List<Map<String, Object>> games;

        public Bracket(Tournament t) {
            int ord = -1;
            rounds = new LinkedList<List<Map<String, Object>>>();
            games = new LinkedList<Map<String, Object>>();
            List<Map<String, Object>> round = null;
            for (BracketMatch m : t.getBracketMatches()) {
                if (m.getOrd() != ord) {
                    ord = m.getOrd();
                    rounds.add(round = new ArrayList<Map<String, Object>>());
                }
                Map<String, Object> match = new HashMap<String, Object>();
                match.put("id", m.getId());
                match.put("aTeam", m.getATeam());
                match.put("bTeam", m.getBTeam());
                BracketMatchResult r = t.getBracketResults().getResult(m);
                match.put("winner", r == null ? null : r.getWinnerSideA() ? m.getATeam() : m.getBTeam());
                round.add(match);
                games.add(match);
            }
        }

        public List<List<Map<String, Object>>> getRounds() {
            return rounds;
        }

        public List<Map<String, Object>> getGames() {
            return games;
        }
    }
}
