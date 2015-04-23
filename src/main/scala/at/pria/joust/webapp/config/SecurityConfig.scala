package at.pria.joust.webapp.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity

@Configuration
@EnableWebMvcSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {
  @throws[Exception]
  protected override def configure(http: HttpSecurity): Unit = {
    http
      .authorizeRequests()
      .antMatchers("/admin/**").hasIpAddress("localhost")
      .anyRequest().permitAll()
  }
}
