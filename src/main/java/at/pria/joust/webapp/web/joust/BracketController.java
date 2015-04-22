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

    @RequestMapping(value = "/bracket/main/", method = RequestMethod.GET)
    public String mainBracket(Model model) {
        model.addAttribute("bracket", new Bracket(tournament));
        model.addAttribute("mode", "main");
        model.addAttribute("ByeTeam", ByeTeam$.MODULE$);
        return "joust/bracket2";
    }

    @RequestMapping(value = "/bracket/consolation/", method = RequestMethod.GET)
    public String consolationBracket(Model model) {
        model.addAttribute("bracket", new Bracket(tournament));
        model.addAttribute("mode", "consolation");
        model.addAttribute("ByeTeam", ByeTeam$.MODULE$);
        return "joust/bracket2";
    }

    private static class Bracket {
        private final List<List<Map<String, Object>>> mainRounds, consolationRounds;
        private final List<Map<String, Object>> finalGames;
        private final List<Map<String, Object>> games;

        public Bracket(Tournament t) {
            //ordinals are first, (main, cons, cons)*, final, final, final
            int ord = -1;
            mainRounds = new LinkedList<List<Map<String, Object>>>();
            consolationRounds = new LinkedList<List<Map<String, Object>>>();
            finalGames = new LinkedList<Map<String, Object>>();
            games = new LinkedList<Map<String, Object>>();
            List<Map<String, Object>> round = null;
            for (BracketMatch m : t.getBracketMatches()) {
                if (m.getOrd() != ord) {
                    ord = m.getOrd();
                    if (m.getId() >= t.getBracketMatches().size() - 3) round = finalGames;
                    else if (ord == 0 || (ord - 1) % 3 == 0) mainRounds.add(round = new ArrayList<Map<String, Object>>());
                    else consolationRounds.add(0, round = new ArrayList<Map<String, Object>>());
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

        public List<List<Map<String, Object>>> getMainRounds() {
            return mainRounds;
        }

        public List<List<Map<String, Object>>> getConsolationRounds() {
            return consolationRounds;
        }

        public List<Map<String, Object>> getFinalGames() {
            return finalGames;
        }

        public List<Map<String, Object>> getGames() {
            return games;
        }
    }
}
