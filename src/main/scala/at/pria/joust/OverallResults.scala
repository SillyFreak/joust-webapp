/**
 * OverallResults.scala
 *
 * Created on 20.04.2015
 */

package at.pria.joust

/**
 * <p>
 * {@code OverallResults}
 * </p>
 *
 * @version V0.0 20.04.2015
 * @author SillyFreak
 */
class OverallResults(t: Tournament) {
  //the list of teams, ordered by score
  //List[(team, seeding score/rank, DE score/rank, doc score/rank, overall score/rank)]
  private[this] val _ranking = new Cached({
    val seeding = Map(t.seedingResults.ranking.map { case (team, _, _, rank, score) => (team, (score, rank)) }: _*)
    val bracket = Map(t.bracketResults.ranking.map { case (team, rank, score) => (team, (score, rank)) }: _*)
    val doc = Map(t.documentationResults.ranking.map { case (team, _, _, _, _, rank, score) => (team, (score, rank)) }: _*)

    val scores = t.teams.map { team =>
      val (sScore, sRank) = seeding(team)
      val (bScore, bRank) = bracket(team)
      val (dScore, dRank) = doc(team)

      (team, sScore, sRank, bScore, bRank, dScore, dRank,
        sScore + bScore + dScore, sRank + bRank + dRank)
    }

    scores.sortBy { case (_, _, _, _, _, _, _, score, _) => -score }
  })

  def ranking = _ranking.value.get

  //Java interop
  private[this] val _jRanking = new Cached({
    val result = new java.util.LinkedHashMap[Team, Double]()
    for ((team, _, _, _, _, _, _, score, _) <- ranking)
      result.put(team, score)
    result: java.util.Map[Team, Double]
  })
  def getRanking() = _jRanking.value.get
}
