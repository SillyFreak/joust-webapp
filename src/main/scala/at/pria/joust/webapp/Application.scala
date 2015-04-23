package at.pria.joust.webapp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.web.SpringBootServletInitializer

@SpringBootApplication
class Application extends SpringBootServletInitializer {
  override def configure(application: SpringApplicationBuilder): SpringApplicationBuilder =
    application.sources(classOf[Application])
}

object Application {
  def main(args: Array[String]): Unit =
    SpringApplication.run(classOf[Application], args: _*)
}
