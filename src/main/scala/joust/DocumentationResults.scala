/**
 * DocumentationResults.scala
 *
 * Created on 16.04.2015
 */
package joust

case class PeriodDocResult(val team: Team, val p1Score: Int, val p2Score: Int, val p3Score: Int)
case class OnsiteDocResult(val team: Team, val onsiteScore: Int)

class DocumentationResults(t: Tournament) {
  object PeriodDoc {
    private[this] val _results = collection.mutable.Map[Team, PeriodDocResult]()
    def result(team: Team, p1: Int, p2: Int, p3: Int) = {
      //_ranking.clear()
      _results(team) = PeriodDocResult(team, p1, p2, p3)
    }
    def result(team: Team) = _results.get(team)

    def clear() = {
      //_ranking.clear()
      _results.clear()
    }
  }
  object OnsiteDoc {
    private[this] val _results = collection.mutable.Map[Team, OnsiteDocResult]()
    def result(team: Team, score: Int) = {
      //_ranking.clear()
      _results(team) = OnsiteDocResult(team, score)
    }
    def result(team: Team) = _results.get(team)

    def clear() = {
      //_ranking.clear()
      _results.clear()
    }
  }

  //the list of teams, ordered by score
  //List[(team, p1rank, p2rank, p3rank, p4rank, rank, score)]
  private[this] val _ranking = new Cached({
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

        (team, p1Rank, p2Rank, p3Rank, p4Rank, rank, score)
    }

    scores.sortBy { case (_, _, _, _, _, _, score) => -score }
  })
  def ranking = _ranking.value.get
}
