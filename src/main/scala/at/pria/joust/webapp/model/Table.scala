/**
 * Table.scala
 *
 * Created on 22.04.2015
 */

package at.pria.joust.webapp.model

import scala.beans.BeanProperty

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.repository.CrudRepository

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

/**
 * <p>
 * {@code Table}
 * </p>
 *
 * @version V0.0 22.04.2015
 * @author SillyFreak
 */
@Entity
class Table {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var id: Long = 0l

  @NotEmpty
  @BeanProperty var description: String = null
}

trait TableRepository extends CrudRepository[Table, java.lang.Long]
