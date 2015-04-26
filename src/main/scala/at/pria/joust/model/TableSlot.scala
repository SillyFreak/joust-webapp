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
  import TableSlot._

  type TInfo = at.pria.joust.service.TournamentService#TournamentInfo

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var id: Long = _

  @ManyToOne
  @BeanProperty var table: Table = _

  @BeanProperty var state: Int = UPCOMING

  def tournament: Tournament
  @Transient
  def getTournament() = tournament

  def participants(implicit tInfo: TInfo): List[Team]
  @Transient
  def getParticipants(implicit tInfo: TInfo) = participants: juList[Team]

  def description(implicit tInfo: TInfo): String
  @Transient
  def getDescription(implicit tInfo: TInfo) = description
}

object TableSlot {
  final val UPCOMING = 0
  final val CALLED = 1
  final val CURRENT = 2
  final val FINISHED = 3
}

trait TableSlotRepository extends CrudRepository[TableSlot, jLong]

@Entity
class PracticeSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var team: Team = _

  override def tournament = team.tournament

  override def description(implicit tInfo: TInfo) = {
    val List(team) = participants
    s"Open practice for team ${team.teamId} ${team.name}"
  }
  override def participants(implicit tInfo: TInfo): List[Team] = List(team)
}

@Entity
class SeedingSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var sGame: SeedingGame = _

  override def tournament = sGame.tournament

  override def description(implicit tInfo: TInfo) = {
    val List(team) = participants
    s"Seeding round ${sGame.round + 1} for team ${team.teamId} ${team.name}"
  }
  override def participants(implicit tInfo: TInfo): List[Team] = List(sGame.team)
}

@Entity
class BracketSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var bGame: BracketGame = _

  override def tournament = bGame.tournament

  override def description(implicit tInfo: TInfo) = {
    val List(aTeam, bTeam) = participants
    s"Double elimination match #${bGame.gameId + 1} between ${aTeam.teamId} ${aTeam.name} & ${bTeam.teamId} ${bTeam.name}"
  }
  override def participants(implicit tInfo: TInfo): List[Team] = {
    val aTeam = tInfo.bracket.games(bGame.gameId).aTeam().get
    val bTeam = tInfo.bracket.games(bGame.gameId).bTeam().get
    List(aTeam, bTeam)
  }
}

@Entity
class AllianceSlot extends TableSlot {
  @ManyToOne
  @BeanProperty var aGame: AllianceGame = _

  override def tournament = aGame.tournament

  override def description(implicit tInfo: TInfo) = {
    val List(aTeam, bTeam) = participants
    s"Alliance game ${aTeam.teamId} ${aTeam.name} & ${bTeam.teamId} ${bTeam.name}"
  }
  override def participants(implicit tInfo: TInfo): List[Team] = List(aGame.aTeam, aGame.bTeam)
}
