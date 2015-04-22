package at.pria.joust.webapp.web.joust;

import at.pria.joust.*;
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
        model.addAttribute(new BracketInput());
        return "joust/bracket_admin";
    }

    @RequestMapping(value = "/admin/bracket/", method = RequestMethod.POST)
    public String adminBracketPost(Model model, BracketInput bracketInput) {
        bracketInput.apply(tournament);
        return adminBracket(model);
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

    @RequestMapping(value = "/bracket/final/", method = RequestMethod.GET)
    public String finalBracket(Model model) {
        model.addAttribute("bracket", new Bracket(tournament));
        model.addAttribute("mode", "final");
        model.addAttribute("ByeTeam", ByeTeam$.MODULE$);
        return "joust/bracket2";
    }

    private static class Bracket {
        private final List<List<Map<String, Object>>> mainRounds, consolationRounds, finalRounds;
        private final List<Map<String, Object>> games;

        public Bracket(Tournament t) {
            //ordinals are first, (main, cons, cons)*, final, final, final
            int ord = -1;
            mainRounds = new LinkedList<List<Map<String, Object>>>();
            consolationRounds = new LinkedList<List<Map<String, Object>>>();
            finalRounds = new LinkedList<List<Map<String, Object>>>();
            games = new LinkedList<Map<String, Object>>();
            List<Map<String, Object>> round = null;
            for (BracketMatch m : t.getBracketMatches()) {
                if (m.getId() >= t.getBracketMatches().size() - 3)
                    finalRounds.add(round = new ArrayList<Map<String, Object>>());
                else if (m.getOrd() != ord) {
                    ord = m.getOrd();
                    if (ord == 0 || (ord - 1) % 3 == 0)
                        mainRounds.add(round = new ArrayList<Map<String, Object>>());
                    else
                        consolationRounds.add(0, round = new ArrayList<Map<String, Object>>());
                }
                assert round != null;
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

        public List<List<Map<String, Object>>> getFinalRounds() {
            return finalRounds;
        }

        public List<Map<String, Object>> getGames() {
            return games;
        }
    }

    private static class BracketInput {
        private int id;
        private boolean winnerSideA;
        private boolean resolved;

        public void apply(Tournament t) {
            BracketMatch match = t.getBracketMatches().get(id);
            if (!resolved) t.getBracketResults().removeResult(match);
            else t.getBracketResults().setResult(match, winnerSideA);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isWinnerSideA() {
            return winnerSideA;
        }

        public void setWinnerSideA(boolean winnerSideA) {
            this.winnerSideA = winnerSideA;
        }

        public boolean isResolved() {
            return resolved;
        }

        public void setResolved(boolean resolved) {
            this.resolved = resolved;
        }
    }
}
