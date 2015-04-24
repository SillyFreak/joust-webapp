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
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var id: Long = _

  @ManyToOne
  @BeanProperty var table: Table = _

  def participants: List[Team]
  @Transient
  def getParticipants() = participants: juList[Team]
}

trait TableSlotRepository extends CrudRepository[TableSlot, jLong]

@Entity
class PracticeSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var team: Team = _

  def participants: List[Team] = List(team)
}

@Entity
class SeedingSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var game: SeedingGame = _

  def participants: List[Team] = List(game.team)
}

@Entity
class BracketSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var game: BracketGame = _

  def participants: List[Team] = List(???, ???)
}

@Entity
class AllianceSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var game: AllianceGame = _

  def participants: List[Team] = List(game.aTeam, game.bTeam)
}
