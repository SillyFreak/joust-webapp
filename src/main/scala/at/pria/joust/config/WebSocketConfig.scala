/**
 * WebSocketConfig.scala
 *
 * Created on 26.04.2015
 */

package at.pria.joust.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry

/**
 * <p>
 * {@code WebSocketConfig}
 * </p>
 *
 * @version V0.0 26.04.2015
 * @author SillyFreak
 */
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
  override def configureMessageBroker(config: MessageBrokerRegistry) = {
    config.enableSimpleBroker("/topic")
    config.setApplicationDestinationPrefixes("/app")
  }

  override def registerStompEndpoints(registry: StompEndpointRegistry) = {
    registry.addEndpoint("/test").withSockJS()
  }
}
