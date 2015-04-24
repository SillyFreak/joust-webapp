/**
 * Team.scala
 *
 * Created on 12.04.2015
 */

package at.pria.joust

import scala.beans.BeanProperty

/**
 * <p>
 * {@code Team}
 * </p>
 *
 * @version V0.0 12.04.2015
 * @author SillyFreak
 */
sealed trait TeamLike {
  @BeanProperty val id: String
  @BeanProperty val name: String
}

case class Team(
  @BeanProperty val id: String,
  @BeanProperty val name: String) extends TeamLike
case object ByeTeam extends TeamLike {
  @BeanProperty val id = "____"
  @BeanProperty val name = "bye"
}
