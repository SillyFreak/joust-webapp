package at.pria.joust.webapp.web.joust;

import at.pria.joust.Team;
import at.pria.joust.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TeamsController {
    @Autowired
    private Tournament tournament;

    @RequestMapping(value = "/admin/teams/", method = RequestMethod.GET)
    public String adminTeams(Model model) {
        model.addAttribute("tournament", tournament);
        model.addAttribute(new SeedingInput());
        return "joust/teams_admin";
    }

    @RequestMapping(value = "/admin/teams/", method = RequestMethod.POST)
    public String adminTeamsPost(Model model, SeedingInput seedingInput) {
        seedingInput.apply(tournament);
        return adminTeams(model);
    }

    @RequestMapping(value = "/teams/", method = RequestMethod.GET)
    public String teams(Model model) {
        model.addAttribute("tournament", tournament);
        return "joust/teams";
    }

    private static class SeedingInput {
        private String id;
        private int score;

        public void apply(Tournament t) {
            String[] parts = id.split("/");
            String id = parts[0];
            int round = Integer.parseInt(parts[1]) - 1;
            for (Team team : t.getTeams())
                if (team.getId().equals(id)) {
                    t.getSeedingResults().setResult(team, round, score);
                    break;
                }
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
