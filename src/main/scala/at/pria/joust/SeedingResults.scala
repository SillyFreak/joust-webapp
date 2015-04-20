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
  @BeanProperty val s1: Int,
  @BeanProperty val s2: Int,
  @BeanProperty val s3: Int,
  @BeanProperty val max: Int,
  @BeanProperty val avg: Double,
  @BeanProperty val score: Double,
  @BeanProperty val rank: Int)

class SeedingResults(t: Tournament) {
  private[this] val _results = collection.mutable.Map[SeedingRound, SeedingRoundResult]()
  def result(sr: SeedingRound, score: Int) = {
    _ranking.clear()
    _results(sr) = SeedingRoundResult(sr.id, score)
  }
  def result(sr: SeedingRound) = _results.get(sr)

  //Java interop
  def setResult(sr: SeedingRound, score: Int) = result(sr, score)
  def getResult(sr: SeedingRound) = result(sr).getOrElse(null)

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
      result <- result(t.seedingRoundsMap(team, round))
    } yield result.score

  //the maximum score, Either final or preliminary
  def max(team: Team): Either[Int, Int] = {
    val scores = this.scores(team).sorted

    val finished = scores.size == t.numOfSeedingRounds
    val score = if (scores.isEmpty) 0 else scores.last

    //the seeding score is final, regular result
    if (finished) Right(score)
    //the seeding score is preliminary, exceptional result
    else Left(score)
  }

  //the average of the best two rounds, Either final or preliminary
  def avg(team: Team): Either[Double, Double] = {
    val scores = this.scores(team).sorted

    val finished = scores.size == t.numOfSeedingRounds
    val dropped = scores.takeRight(2)
    val score = if (dropped.isEmpty) 0 else dropped.sum.toDouble / dropped.size

    //the seeding score is final, regular result
    if (finished) Right(score)
    //the seeding score is preliminary, exceptional result
    else Left(score)
  }

  //the list of teams, ordered by score
  //List[(team, max, avg, rank, score)]
  private[this] val _ranking = new Cached({
    val count = t.teams.size.asInstanceOf[Double]

    val _scores = t.teams.map { team =>
      (max(team), avg(team)) match {
        case (Right(max), Right(avg)) => (team, max, avg)
        case (Left(max), Left(avg))   => (team, max, avg)
        case x                        => throw new MatchError(x)
      }
    }

    val seedingMax = _scores.map { case (_, max, _) => max }.max

    val scores = _scores.map {
      case (team, teamMax, teamAvg) =>
        val rank = _scores.count { case (_, _, avg) => avg > teamAvg }

        val score = .75 * (count - rank) / count + .25 * teamAvg / seedingMax

        SeedingScore(team, 0, 0, 0, teamMax, teamAvg, score, rank)
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
