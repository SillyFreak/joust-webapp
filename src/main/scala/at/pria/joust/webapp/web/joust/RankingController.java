package at.pria.joust.webapp.web.joust;

import at.pria.joust.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class RankingController {
    @Autowired
    private Tournament tournament;

    @RequestMapping(value = "/ranking/", method = RequestMethod.GET)
    public String adminBracket(Model model) {
        List<RankItem> ranking = new ArrayList<>();
        Iterator<OverallScore> ov = tournament.getOverallResults().getRanking().values().iterator();
        Iterator<DocumentationScore> doc = tournament.getDocumentationResults().getRanking().values().iterator();
        Iterator<SeedingScore> seed = tournament.getSeedingResults().getRanking().values().iterator();
        Iterator<BracketScore> de = tournament.getBracketResults().getRanking().values().iterator();
        for (int i = 0; i < tournament.getTeams().size(); i++) {
            ranking.add(new RankItem(ov.next(), doc.next(), seed.next(), de.next()));
        }

        model.addAttribute("tournament", tournament);
        model.addAttribute("ranking", ranking);
        return "joust/ranking";
    }

    private static class RankItem {
        private final OverallScore overall;
        private final DocumentationScore doc;
        private final SeedingScore seed;
        private final BracketScore de;

        public RankItem(OverallScore overall, DocumentationScore doc, SeedingScore seed, BracketScore de) {
            this.overall = overall;
            this.doc = doc;
            this.seed = seed;
            this.de = de;
        }

        public OverallScore getOverall() {
            return overall;
        }

        public DocumentationScore getDoc() {
            return doc;
        }

        public SeedingScore getSeed() {
            return seed;
        }

        public BracketScore getDe() {
            return de;
        }
    }
}
