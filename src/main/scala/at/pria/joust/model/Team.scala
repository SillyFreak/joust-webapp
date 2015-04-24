/**
 * Team.scala
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
  @BeanProperty var id: Long = 0l

  @NotEmpty
  @BeanProperty var teamId: String = null

  @NotEmpty
  @BeanProperty var name: String = null

  @BeanProperty var p1doc: Int = 0
  @BeanProperty var p2doc: Int = 0
  @BeanProperty var p3doc: Int = 0
  @BeanProperty var onsite: Int = 0
}

trait TeamRepository extends CrudRepository[Team, java.lang.Long]
