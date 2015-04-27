package at.pria.joust.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity

@Configuration
@EnableWebMvcSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {
  @throws[Exception]
  protected override def configure(http: HttpSecurity): Unit = {
    http
      .headers().frameOptions().disable()
      .authorizeRequests()
      .antMatchers("/admin/**").hasRole("ADMIN")
      .anyRequest().permitAll()
      .and()
      .formLogin().loginPage("/login/").permitAll()
      .and()
      .logout().logoutUrl("/logout/").logoutSuccessUrl("/").permitAll()
  }

  @Autowired
  def configureGlobal(auth: AuthenticationManagerBuilder) = {
    auth.inMemoryAuthentication()
      .withUser("admin").password("ecer2015!?").roles("USER", "ADMIN")
  }
}
