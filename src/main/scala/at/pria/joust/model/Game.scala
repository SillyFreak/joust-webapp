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
  @BeanProperty var id: Long = 0l

  @ManyToOne
  @BeanProperty var tournament: Tournament = null
}

trait GameRepository extends CrudRepository[Game, java.lang.Long]

@Entity
class SeedingGame extends Game {
  @ManyToOne
  @BeanProperty var team: Team = null
  @BeanProperty var round: Int = -1

  //result
  @BeanProperty var finished: Boolean = false
  @BeanProperty var score: Int = -1
}

@Entity
class BracketGame extends Game {
  @BeanProperty var gameId: Int = -1

  //result
  @BeanProperty var finished: Boolean = false
  @BeanProperty var winnerSideA: Boolean = false
}

@Entity
class AllianceGame extends Game {
  @ManyToOne
  @BeanProperty var aTeam: Team = null

  @ManyToOne
  @BeanProperty var bTeam: Team = null

  //result
  @BeanProperty var finished: Boolean = false
  @BeanProperty var score: Int = -1
}
