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
  @BeanProperty var id: Long = 0l

  @NotEmpty
  @BeanProperty var name: String = null

  @OneToMany
  @BeanProperty var teams: java.util.List[Team] = new java.util.ArrayList[Team]

  @OneToMany(mappedBy = "tournament")
  @BeanProperty var games: java.util.List[Game] = new java.util.ArrayList[Game]
}

trait TournamentRepository extends CrudRepository[Tournament, java.lang.Long]
