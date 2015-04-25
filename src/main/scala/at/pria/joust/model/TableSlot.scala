/**
 * TableSlot.scala
 *
 * Created on 22.04.2015
 */

package at.pria.joust.model

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.repository.CrudRepository

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.ManyToOne
import javax.persistence.Transient

import java.lang.{ Long => jLong }
import java.util.{ List => juList, ArrayList => juArrayList }

/**
 * <p>
 * {@code TableSlot}
 * </p>
 *
 * @version V0.0 22.04.2015
 * @author SillyFreak
 */
@Entity
abstract class TableSlot {
  import TableSlot._

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var id: Long = _

  @ManyToOne
  @BeanProperty var table: Table = _

  @BeanProperty var state: Int = UPCOMING

  def participants: List[Team]
  @Transient
  def getParticipants() = participants: juList[Team]

  def description: String
  @Transient
  def getDescription() = description
}

object TableSlot {
  final val UPCOMING = 0
  final val CALLED = 1
  final val CURRENT = 2
  final val FINISHED = 3
}

trait TableSlotRepository extends CrudRepository[TableSlot, jLong]

@Entity
class PracticeSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var team: Team = _

  def participants: List[Team] = List(team)
  def description = s"Open practice for team ${team.teamId} ${team.name}"
}

@Entity
class SeedingSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var game: SeedingGame = _

  def description = s"Seeding round ${game.round + 1} for team ${game.team.teamId} ${game.team.name}"
  def participants: List[Team] = List(game.team)
}

@Entity
class BracketSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var game: BracketGame = _

  def description = s"Double elimination match #${game.gameId + 1}"
  def participants: List[Team] = List(???, ???)
}

@Entity
class AllianceSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var game: AllianceGame = _

  def description = s"Alliance game ${game.aTeam.teamId} ${game.aTeam.name} & ${game.bTeam.teamId} ${game.bTeam.name}"
  def participants: List[Team] = List(game.aTeam, game.bTeam)
}
