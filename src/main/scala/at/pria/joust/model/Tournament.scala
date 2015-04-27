/**
 * Tournament.scala
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
import javax.persistence.OneToMany
import javax.persistence.Transient

import java.lang.{ Long => jLong }
import java.util.{ List => juList, ArrayList => juArrayList }

/**
 * <p>
 * {@code Tournament}
 * </p>
 *
 * @version V0.0 22.04.2015
 * @author SillyFreak
 */
@Entity
class Tournament {
  import Tournament._

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var id: Long = _

  @NotEmpty
  @BeanProperty var name: String = _

  @BeanProperty var mode: Int = _

  @OneToMany(mappedBy = "tournament")
  @BeanProperty var teams: juList[Team] = new juArrayList[Team]

  @OneToMany(mappedBy = "tournament")
  @BeanProperty var games: juList[Game] = new juArrayList[Game]

  //inferred

  @Transient def isAerialMode = mode == AERIAL
  @Transient def isBotballMode = mode == BOTBALL

  def seedingMax = {
    val scores = games.collect { case g: SeedingGame if g.finished => g.score }
    if (scores.isEmpty) 0 else scores.max
  }
}

object Tournament {
  final val BOTBALL = 0
  final val AERIAL = 1
}

trait TournamentRepository extends CrudRepository[Tournament, jLong] {
  def findByName(name: String): Tournament
}
