package at.pria.joust.webapp.config;

import at.pria.joust.Team;
import at.pria.joust.Tournament;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

import java.util.ArrayList;
import java.util.List;

import static scala.collection.JavaConversions.*;

@Configuration
@ComponentScan
public class RootConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login/").setViewName("accounts/login");
        registry.addViewController("/admin/").setViewName("admin/admin");
        registry.addViewController("/user/").setViewName("user/user");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasenames(
                "classpath:i18n/accounts",
                "classpath:i18n/admin",
                "classpath:i18n/error",
                "classpath:i18n/joust");
        return ms;
    }

    @Bean
    public Tournament tournament() {
        List<Team> teams = new ArrayList<Team>();
        Team t1, t2;

        teams.add(t1 = new Team("15-0000", "TGM"));
        teams.add(t2 = new Team("15-0001", "TGM"));
        teams.add(new Team("15-0002", "TGM"));
        teams.add(new Team("15-0003", "TGM"));
        teams.add(new Team("15-0004", "TGM"));
        teams.add(new Team("15-0005", "TGM"));
        teams.add(new Team("15-0006", "TGM"));
        teams.add(new Team("15-0007", "TGM"));
        teams.add(new Team("15-0008", "TGM"));
        teams.add(new Team("15-0009", "TGM"));
        teams.add(new Team("15-0010", "TGM"));
        teams.add(new Team("15-0011", "TGM"));
        teams.add(new Team("15-0012", "TGM"));
        teams.add(new Team("15-0013", "TGM"));
        teams.add(new Team("15-0014", "TGM"));
        teams.add(new Team("15-0015", "TGM"));
        teams.add(new Team("15-0016", "TGM"));

        Tournament t = new Tournament(collectionAsScalaIterable(teams).toList());

        return t;
    }
}