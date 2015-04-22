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
        private String team;
        private int round;
        private int score;

        public void apply(Tournament t) {
            for (Team team : t.getTeams())
                if (team.getId().equals(this.team)) {
                    t.getSeedingResults().setResult(team, round, score);
                    break;
                }
        }

        public String getTeam() {
            return team;
        }

        public void setTeam(String team) {
            this.team = team;
        }

        public int getRound() {
            return round;
        }

        public void setRound(int round) {
            this.round = round;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
