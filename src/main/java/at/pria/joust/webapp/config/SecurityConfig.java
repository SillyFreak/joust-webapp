package at.pria.joust.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@Configuration
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/admin/**").hasIpAddress("localhost")
                .anyRequest().permitAll()
                .and()
                .formLogin().loginPage("/login/").permitAll()
                .and()
                .logout().logoutUrl("/logout/").logoutSuccessUrl("/").permitAll();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JdbcUserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager mgr = new JdbcUserDetailsManager();
        mgr.setDataSource(dataSource);
        return mgr;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, JdbcUserDetailsManager userDetailsManager, DataSource dataSource, PasswordEncoder enc) throws Exception {
        auth.userDetailsService(userDetailsManager);
        JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> conf =
                new JdbcUserDetailsManagerConfigurer<>(userDetailsManager);
        auth.apply(conf);
        conf.dataSource(dataSource).withDefaultSchema().passwordEncoder(enc)
                .withUser("user").password(enc.encode("password")).roles("USER").and()
                .withUser("admin").password(enc.encode("password")).roles("USER", "ADMIN");
    }
}
