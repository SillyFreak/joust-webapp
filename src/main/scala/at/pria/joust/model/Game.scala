/**
 * Game.scala
 *
 * Created on 23.04.2015
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

/**
 * <p>
 * {@code Game}
 * </p>
 *
 * @version V0.0 23.04.2015
 * @author SillyFreak
 */
@Entity
abstract class Game {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var id: Long = _

  @ManyToOne
  @BeanProperty var tournament: Tournament = _
}

trait GameRepository extends CrudRepository[Game, jLong]

@Entity
class SeedingGame extends Game {
  @ManyToOne
  @BeanProperty var team: Team = _
  @BeanProperty var round: Int = _

  //result
  @BeanProperty var finished: Boolean = _
  @BeanProperty var score: Int = _
}

@Entity
class BracketGame extends Game {
  @BeanProperty var gameId: Int = _

  //result
  @BeanProperty var finished: Boolean = false
  @BeanProperty var winnerSideA: Boolean = _
}

@Entity
class AllianceGame extends Game {
  @ManyToOne
  @BeanProperty var aTeam: Team = _

  @ManyToOne
  @BeanProperty var bTeam: Team = _

  //result
  @BeanProperty var finished: Boolean = false
  @BeanProperty var score: Int = _
}

@Entity
class AerialGame extends Game {
  @ManyToOne
  @BeanProperty var aerialTeam: Team = _
  @BeanProperty var day: Int = _
  @BeanProperty var round: Int = _

  //result
  @BeanProperty var finished: Boolean = false
  @BeanProperty var score: Int = _
}
