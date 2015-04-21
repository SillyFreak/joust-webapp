/**
 * SeedingResults.scala
 *
 * Created on 14.04.2015
 */

package at.pria.joust

import scala.beans.BeanProperty

case class SeedingRoundResult(
  @BeanProperty val id: Int,
  @BeanProperty val score: Int)

case class SeedingScore(
    @BeanProperty val team: Team,
    val s1: Option[Int],
    val s2: Option[Int],
    val s3: Option[Int],
    @BeanProperty val max: Int,
    @BeanProperty val avg: Double,
    @BeanProperty val score: Double,
    @BeanProperty val rank: Int) {
  def getS1() = s1.getOrElse(-1)
  def getS2() = s2.getOrElse(-1)
  def getS3() = s3.getOrElse(-1)
}

class SeedingResults(t: Tournament) {
  private[this] val _results = collection.mutable.Map[SeedingRound, SeedingRoundResult]()
  def result(team: Team, round: Int, score: Int) = {
    val sr = t.seedingRoundsMap(team, round)
    _ranking.clear()
    _results(sr) = SeedingRoundResult(sr.id, score)
  }
  def result(team: Team, round: Int) = {
    val sr = t.seedingRoundsMap(team, round)
    _results.get(sr)
  }

  //Java interop
  def setResult(team: Team, round: Int, score: Int) = result(team, round, score)
  def getResult(team: Team, round: Int) = result(team, round).getOrElse(null)

  def clear() = {
    _ranking.clear()
    _results.clear()
    t.bracketResults.clear()
    t.overallResults.clear()
  }

  //a list of scores of all seeding rounds the team has already had
  def scores(team: Team) =
    for {
      round <- 0 until t.numOfSeedingRounds
    } yield result(team, round)

  //the list of teams, ordered by score
  //List[(team, max, avg, rank, score)]
  private[this] val _ranking: Cached[(List[SeedingScore], java.util.Map[Team, SeedingScore])] = new Cached({
    val count = t.teams.size.asInstanceOf[Double]

    val _scores = t.teams.map { team =>
      val allScores = this.scores(team).map(_.map(_.score))
      val scores = allScores.collect { case Some(x) => x }.sorted

      val (max, avg) =
        if (scores.isEmpty) (0, 0d)
        else {
          val dropped = scores.takeRight(2)
          (dropped.last, dropped.sum.toDouble / dropped.size)
        }

      (team, allScores(0), allScores(1), allScores(2), max, avg)
    }

    val seedingMax = _scores.map { case (_, _, _, _, max, _) => max }.max

    val scores = _scores.map {
      case (team, s1, s2, s3, teamMax, teamAvg) =>
        val rank = _scores.count { case (_, _, _, _, _, avg) => avg > teamAvg }

        val score = .75 * (count - rank) / count + .25 * teamAvg / seedingMax

        SeedingScore(team, s1, s2, s3, teamMax, teamAvg, score, rank)
    }

    val result = scores.sortBy { sc => sc.rank }

    //Java interop
    val jResult = new java.util.LinkedHashMap[Team, SeedingScore]()
    for (sc <- result)
      jResult.put(sc.team, sc)

    (result, jResult: java.util.Map[Team, SeedingScore])
  })
  def ranking = _ranking.value.get._1

  //Java interop
  def getRanking() = _ranking.value.get._2

  //the team with the specified rank, or ByeTeam if there are less teams
  def teamByRank(rank: Int): TeamLike =
    ranking.map { sc => sc.team }.applyOrElse(rank, { case _ => ByeTeam }: PartialFunction[Int, TeamLike])
}
