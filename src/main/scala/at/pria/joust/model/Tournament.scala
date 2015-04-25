/**
 * Tournament.scala
 *
 * Created on 22.04.2015
 */

package at.pria.joust.model

import scala.beans.BeanProperty

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.repository.CrudRepository

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.OneToMany

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
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var id: Long = _

  @NotEmpty
  @BeanProperty var name: String = _

  @OneToMany(mappedBy = "tournament")
  @BeanProperty var teams: juList[Team] = new juArrayList[Team]

  @OneToMany(mappedBy = "tournament")
  @BeanProperty var games: juList[Game] = new juArrayList[Game]
}

trait TournamentRepository extends CrudRepository[Tournament, jLong] {
  def findByName(name: String): Tournament
}
