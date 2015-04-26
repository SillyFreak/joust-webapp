/**
 * NotFoundException.scala
 *
 * Created on 26.04.2015
 */

package at.pria.joust.web

import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.HttpStatus

/**
 * <p>
 * {@code NotFoundException}
 * </p>
 *
 * @version V0.0 26.04.2015
 * @author SillyFreak
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException extends RuntimeException
