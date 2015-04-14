/**
 * Cached.scala
 *
 * Created on 14.04.2015
 */

package joust

/**
 * <p>
 * {@code Cached}
 * </p>
 *
 * @version V0.0 14.04.2015
 * @author SillyFreak
 */
class Cached[A](a: => A) {
  private[this] var _value: Option[A] = None

  def clear() = _value = None
  def value = _value match {
    case value @ Some(_) =>
      value
    case None => try {
      _value = Some(a)
      _value
    } catch {
      case ex: IllegalStateException => None
    }
  }
}
