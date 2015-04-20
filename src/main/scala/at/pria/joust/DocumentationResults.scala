/**
 * DocumentationResults.scala
 *
 * Created on 16.04.2015
 */

package at.pria.joust

import scala.beans.BeanProperty

case class PeriodDocResult(
  @BeanProperty val team: Team,
  @BeanProperty val p1Score: Int,
  @BeanProperty val p2Score: Int,
  @BeanProperty val p3Score: Int)
case class OnsiteDocResult(
  @BeanProperty val team: Team,
  @BeanProperty val onsiteScore: Int)

case class DocumentationScore(
  @BeanProperty val team: Team,
  @BeanProperty val p1Score: Int,
  @BeanProperty val p1Rank: Int,
  @BeanProperty val p2Score: Int,
  @BeanProperty val p2Rank: Int,
  @BeanProperty val p3Score: Int,
  @BeanProperty val p3Rank: Int,
  @BeanProperty val onsiteScore: Int,
  @BeanProperty val onsiteRank: Int,
  @BeanProperty val score: Double,
  @BeanProperty val rank: Int)

class DocumentationResults(t: Tournament) {
  @BeanProperty object PeriodDoc {
    private[this] val _results = collection.mutable.Map[Team, PeriodDocResult]()
    def result(team: Team, p1: Int, p2: Int, p3: Int) = {
      _ranking.clear()
      _results(team) = PeriodDocResult(team, p1, p2, p3)
    }
    def result(team: Team) = _results.get(team)

    //Java interop
    def setResult(team: Team, p1: Int, p2: Int, p3: Int) = result(team, p1, p2, p3)
    def getResult(team: Team) = result(team).getOrElse(null)

    def clear() = {
      _ranking.clear()
      _results.clear()
      t.overallResults.clear()
    }
  }

  @BeanProperty object OnsiteDoc {
    private[this] val _results = collection.mutable.Map[Team, OnsiteDocResult]()
    def result(team: Team, score: Int) = {
      _ranking.clear()
      _results(team) = OnsiteDocResult(team, score)
    }
    def result(team: Team) = _results.get(team)

    //Java interop
    def setResult(team: Team, score: Int) = result(team, score)
    def getResult(team: Team) = result(team).getOrElse(null)

    def clear() = {
      _ranking.clear()
      _results.clear()
      t.overallResults.clear()
    }
  }

  //the list of teams, ordered by score
  //List[(team, p1rank, p2rank, p3rank, p4rank, rank, score)]
  private[this] val _ranking: Cached[(List[DocumentationScore], java.util.Map[Team, DocumentationScore])] = new Cached({
    val count = t.teams.size.asInstanceOf[Double]

    val _scores = t.teams.map { team =>
      val (p1, p2, p3) = PeriodDoc.result(team) match {
        case Some(PeriodDocResult(_, p1, p2, p3)) => (p1, p2, p3)
        case None                                 => (0, 0, 0)
      }
      val p4 = OnsiteDoc.result(team) match {
        case Some(OnsiteDocResult(_, score)) => score
        case None                            => 0
      }

      val score =
        List(
          (0.30, 300, p1),
          (0.30, 300, p2),
          (0.10, 200, p3),
          (0.30, 100, p4))
          .foldLeft(0d) { case (sum, (weight, max, score)) => sum + weight * score / max }

      (team, p1, p2, p3, p4, score)
    }

    val scores = _scores.map {
      case (team, p1, p2, p3, p4, score) =>
        val p1Rank = _scores.count { case (_, p, _, _, _, _) => p > p1 }
        val p2Rank = _scores.count { case (_, _, p, _, _, _) => p > p2 }
        val p3Rank = _scores.count { case (_, _, _, p, _, _) => p > p3 }
        val p4Rank = _scores.count { case (_, _, _, _, p, _) => p > p4 }

        val rank = _scores.count { case (_, _, _, _, _, sc) => sc > score }

        DocumentationScore(team, p1, p1Rank, p2, p2Rank, p3, p3Rank, p4, p4Rank, score, rank)
    }

    val result = scores.sortBy { sc => sc.rank }

    //Java interop
    val jResult = new java.util.LinkedHashMap[Team, DocumentationScore]()
    for (sc <- result)
      jResult.put(sc.team, sc)

    (result, jResult: java.util.Map[Team, DocumentationScore])
  })
  def ranking = _ranking.value.get._1

  //Java interop
  def getRanking() = _ranking.value.get._2
}
