/**
 * OverallResults.scala
 *
 * Created on 20.04.2015
 */

package at.pria.joust

import scala.beans.BeanProperty

case class OverallScore(
  @BeanProperty val team: Team,
  @BeanProperty val seedingScore: Double,
  @BeanProperty val seedingRank: Int,
  @BeanProperty val bracketScore: Double,
  @BeanProperty val bracketRank: Int,
  @BeanProperty val documentationScore: Double,
  @BeanProperty val documentationRank: Int,
  @BeanProperty val score: Double,
  @BeanProperty val rank: Int)

class OverallResults(t: Tournament) {
  private[joust] def clear() = _ranking.clear()

  //the list of teams, ordered by score
  //List[(team, seeding score/rank, DE score/rank, doc score/rank, overall score/rank)]
  private[this] val _ranking = new Cached({
    val seeding = Map(t.seedingResults.ranking.map { sc => (sc.team, (sc.score, sc.rank)) }: _*)
    val bracket = Map(t.bracketResults.ranking.map { sc => (sc.team, (sc.score, sc.rank)) }: _*)
    val doc = Map(t.documentationResults.ranking.map { sc => (sc.team, (sc.score, sc.rank)) }: _*)

    val scores = t.teams.map { team =>
      val (sScore, sRank) = seeding(team)
      val (bScore, bRank) = bracket(team)
      val (dScore, dRank) = doc(team)

      OverallScore(team, sScore, sRank, bScore, bRank, dScore, dRank,
        sScore + bScore + dScore, sRank + bRank + dRank)
    }

    val result = scores.sortBy { sc => sc.rank }

    //Java interop
    val jResult = new java.util.LinkedHashMap[Team, OverallScore]()
    for (sc <- result)
      jResult.put(sc.team, sc)

    (result, jResult: java.util.Map[Team, OverallScore])
  })

  def ranking = _ranking.value.get._1

  //Java interop
  def getRanking() = _ranking.value.get._2
}
