package at.pria.joust.config

import at.pria.joust._

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Configuration
@ComponentScan
class RootConfiguration extends WebMvcConfigurerAdapter {
  override def addViewControllers(registry: ViewControllerRegistry) = {
    registry.addViewController("/login/").setViewName("accounts/login")
    registry.addViewController("/admin/").setViewName("admin/admin")
    registry.addViewController("/user/").setViewName("user/user")
    registry.setOrder(Ordered.HIGHEST_PRECEDENCE)
  }

  @Bean def springSecurityDialect() =
    new org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect()

  @Bean def layoutDialect() =
    new nz.net.ultraq.thymeleaf.LayoutDialect()

  @Bean def messageSource() = {
    val ms = new org.springframework.context.support.ReloadableResourceBundleMessageSource()
    ms.setBasenames(
      "classpath:i18n/error",
      "classpath:i18n/joust")
    ms
  }

  @Bean def tournament() = {
    val teams =
      for (i <- List(0 to 16: _*))
        yield Team("15-%04d".format(i), "TGM")
    new Tournament(teams)
  }
}
