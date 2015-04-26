/**
 * Team.scala
 *
 * Created on 22.04.2015
 */

package at.pria.joust.model

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

import at.pria.joust.service.BracketStructure

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

  private def rawBracketScore(bracket: BracketStructure) = {
    var score = -1
    for (game <- bracket.games)
      for (a <- game.aTeam(); b <- game.bTeam())
        if (a == this || b == this) score = game.round
    score
  }

  def bracketRank(bracket: BracketStructure) = {
    val bracketScore = this.rawBracketScore(bracket)
    tournament.teams.count { _.rawBracketScore(bracket) > bracketScore }
  }
  @Transient def getBracketRank(bracket: BracketStructure) = bracketRank(bracket)

  def bracketScore(bracket: BracketStructure) = {
    val count = tournament.teams.size
    (count - bracketRank(bracket)).toDouble / count
  }
  @Transient def getBracketScore(bracket: BracketStructure) = bracketScore(bracket)

  def docScore =
    List(
      (0.30, 300, p1doc),
      (0.30, 300, p2doc),
      (0.10, 200, p3doc),
      (0.30, 100, onsite))
      .foldLeft(0d) { case (sum, (weight, max, score)) => sum + weight * score / max }
  @Transient def getDocScore() = docScore

  def docRank = {
    val docScore = this.docScore
    tournament.teams.count { _.docScore > docScore }
  }
  @Transient def getDocRank() = docRank

  def overallScore(bracket: BracketStructure) =
    seedingScore + bracketScore(bracket) + docScore
  @Transient def getOverallScore(bracket: BracketStructure) = overallScore(bracket)

  def overallRank(bracket: BracketStructure) = {
    val overallScore = this.overallScore(bracket)
    tournament.teams.count { _.overallScore(bracket) > overallScore }
  }
  @Transient def getOverallRank(bracket: BracketStructure) = overallRank(bracket)
}

trait TeamRepository extends CrudRepository[Team, jLong] {
  def findByTeamId(teamId: String): Tournament
  def findByName(name: String): Tournament
}
