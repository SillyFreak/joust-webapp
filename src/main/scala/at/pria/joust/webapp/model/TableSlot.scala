/**
 * TableSlot.scala
 *
 * Created on 22.04.2015
 */

package at.pria.joust.webapp.model

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
  @BeanProperty var id: Long = 0l

  @ManyToOne
  @BeanProperty var table: Table = null

  def participants: List[Team]
  @Transient
  def getParticipants() = participants: java.util.List[Team]
}

trait TableSlotRepository extends CrudRepository[TableSlot, java.lang.Long]

@Entity
class PracticeSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var team: Team = null

  def participants: List[Team] = List(team)
}

@Entity
class SeedingSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var game: SeedingGame = null

  def participants: List[Team] = List(game.team)
}

@Entity
class BracketSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var game: BracketGame = null

  def participants: List[Team] = List(???, ???)
}

@Entity
class AllianceSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var game: AllianceGame = null

  def participants: List[Team] = List(game.aTeam, game.bTeam)
}
