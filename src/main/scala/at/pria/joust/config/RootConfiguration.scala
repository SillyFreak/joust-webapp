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
    registry.addViewController("/notification/").setViewName("joust/notification")
    registry.setOrder(Ordered.HIGHEST_PRECEDENCE)
  }

  @Bean def springSecurityDialect() =
    new org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect()

  @Bean def layoutDialect() =
    new nz.net.ultraq.thymeleaf.LayoutDialect()

  @Bean def messageSource() = {
    val ms = new org.springframework.context.support.ReloadableResourceBundleMessageSource()
    ms.setBasenames(
      "classpath:i18n/admin",
      "classpath:i18n/error",
      "classpath:i18n/joust")
    ms
  }
}
