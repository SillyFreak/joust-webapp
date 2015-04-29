/**
 * Team.scala
 *
 * Created on 22.04.2015
 */

package at.pria.joust.model

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.service.TournamentService.{ TournamentInfo => TInfo }

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.repository.CrudRepository

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
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

  @ManyToOne
  @BeanProperty var tournament: Tournament = _

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

  @OneToMany(mappedBy = "aerialTeam")
  @BeanProperty var aerialGames: juList[AerialGame] = new juArrayList[AerialGame]

  //inferred

  private[this] def seedingStats = {
    val _scores =
      for (game <- List(seedingGames: _*) if game.finished)
        yield game.score
    val scores = _scores.sorted

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

  def seedingRank = {
    val seedingAvg = this.seedingAvg
    tournament.teams.count { _.seedingAvg > seedingAvg }
  }
  @Transient def getSeedingRank() = seedingRank

  def seedingScore = {
    val count = tournament.teams.size
    val rank = seedingRank
    val avg = seedingAvg
    val seedingMax = tournament.seedingMax

    .75 * (count - rank) / count + .25 * (if (seedingMax == 0) 0 else avg / seedingMax)
  }
  @Transient def getSeedingScore() = seedingScore

  private def rawBracketScore(implicit tInfo: TInfo) = {
    var score = -1
    for (game <- tInfo.bracket.games)
      for (a <- game.aTeam(); b <- game.bTeam())
        if (a == this || b == this) score = game.round
    score
  }

  def bracketRank(implicit tInfo: TInfo) = {
    val bracketScore = this.rawBracketScore
    tournament.teams.count { _.rawBracketScore > bracketScore }
  }
  @Transient def getBracketRank(implicit tInfo: TInfo) = bracketRank

  def bracketScore(implicit tInfo: TInfo) = {
    val count = tournament.teams.size
    (count - bracketRank).toDouble / count
  }
  @Transient def getBracketScore(implicit tInfo: TInfo) = bracketScore

  def docScore =
    List(
      (0.30, 100, p1doc),
      (0.30, 100, p2doc),
      (0.10, 100, p3doc),
      (0.30, 100, onsite))
      .foldLeft(0d) { case (sum, (weight, max, score)) => sum + weight * score / max }
  @Transient def getDocScore() = docScore

  def docRank = {
    val docScore = this.docScore
    tournament.teams.count { _.docScore > docScore }
  }
  @Transient def getDocRank() = docRank

  def overallScore(implicit tInfo: TInfo) =
    seedingScore + bracketScore + docScore
  @Transient def getOverallScore(implicit tInfo: TInfo) = overallScore

  def overallRank(implicit tInfo: TInfo) = {
    val overallScore = this.overallScore
    tournament.teams.count { _.overallScore > overallScore }
  }
  @Transient def getOverallRank(implicit tInfo: TInfo) = overallRank

  private[this] def aerialStats =
    (for (games <- List(aerialGames: _*).groupBy(_.day).toList.sortBy(_._1).map(_._2)) yield {
      val _scores =
        for (game <- games if game.finished)
          yield game.score
      val scores = _scores.sorted

      if (scores.isEmpty) {
        (0, 0d)
      } else {
        val dropped = scores.takeRight(2)
        (dropped.last, dropped.sum.toDouble / dropped.size)
      }
    }).padTo(2, (0, 0d))

  def aerialMax = aerialStats.map(_._1)
  @Transient def getAerialMax() = aerialMax: juList[Int]

  def aerialAvg = aerialStats.map(_._2)
  @Transient def getAerialAvg() = aerialAvg: juList[Double]

  private def rawAerialScore = {
    val avgs = aerialAvg
    avgs.sum / avgs.size
  }

  def aerialRank = {
    val rawAerialScore = this.rawAerialScore
    tournament.teams.count { _.rawAerialScore > rawAerialScore }
  }
  @Transient def getAerialRank() = aerialRank

  def aerialScore = {
    val count = tournament.teams.size
    val rank = aerialRank

    (count - rank) / count
  }
  @Transient def getAerialScore() = aerialScore
}

trait TeamRepository extends CrudRepository[Team, jLong] {
  def findByTeamId(teamId: String): Tournament
  def findByName(name: String): Tournament
}
