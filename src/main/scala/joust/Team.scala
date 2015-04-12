/**
 * Team.scala
 *
 * Created on 12.04.2015
 */

package joust

/**
 * <p>
 * {@code Team}
 * </p>
 *
 * @version V0.0 12.04.2015
 * @author SillyFreak
 */
sealed trait TeamLike {
  val id: String
  val name: String
}

case class Team(val id: String, val name: String) extends TeamLike
case object ByeTeam extends TeamLike {
  val id = "____"
  val name = "bye"
}
