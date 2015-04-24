/**
 * Team.scala
 *
 * Created on 22.04.2015
 */

package at.pria.joust.model

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.repository.CrudRepository

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Transient

import java.lang.{ Long => jLong }
import java.util.{ List => juList, ArrayList => juArrayList }

/**
 * <p>
 * {@code Team}
 * </p>
 *
 * @version V0.0 22.04.2015
 * @author SillyFreak
 */
@Entity
class Team {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var id: Long = _

  @NotEmpty
  @BeanProperty var teamId: String = _

  @NotEmpty
  @BeanProperty var name: String = _

  @BeanProperty var p1doc: Int = _
  @BeanProperty var p2doc: Int = _
  @BeanProperty var p3doc: Int = _
  @BeanProperty var onsite: Int = _

  @OneToMany(mappedBy = "team")
  @BeanProperty var seedingGames: juList[SeedingGame] = new juArrayList[SeedingGame]

  //inferred

  private[this] def seedingStats = {
    val allScores =
      for (game <- List(seedingGames: _*))
        yield if (game.finished) Some(game.score) else None
    val scores = allScores.collect { case Some(x) => x }.sorted

    if (scores.isEmpty) {
      (0, 0d)
    } else {
      val dropped = scores.takeRight(2)
      (dropped.last, dropped.sum.toDouble / dropped.size)
    }
  }

  def seedingMax = seedingStats._1
  @Transient def getSeedingMax() = seedingMax

  def seedingAvg = seedingStats._2
  @Transient def getSeedingAvg() = seedingAvg
}

trait TeamRepository extends CrudRepository[Team, jLong] {
  def findByTeamId(teamId: String): Tournament
  def findByName(name: String): Tournament
}
