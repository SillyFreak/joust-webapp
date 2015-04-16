/**
 * SeedingResults.scala
 *
 * Created on 14.04.2015
 */

package joust

case class SeedingRoundResult(val id: Int, val score: Int)

class SeedingResults(t: Tournament) {
  private[this] val _results = collection.mutable.Map[SeedingRound, SeedingRoundResult]()
  def result(sr: SeedingRound, score: Int) = {
    _ranking.clear()
    _results(sr) = SeedingRoundResult(sr.id, score)
  }
  def result(sr: SeedingRound) = _results.get(sr)

  def clear() = {
    _ranking.clear()
    _results.clear()
    t.bracketResults.clear()
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

        //TODO this double-calculation is clearly a bug in the original Joust

        // .5 * ( teamcount - seedrank +1)/teamcount + .5 *(seedavg/tmtseedmax) as seedscore
        val _score = .5 * (count - rank) / count + .5 * teamAvg / seedingMax
        // $seed = (3 / 4) * (($teamcount - $team -> seedrank + 1) / $teamcount) + (1 / 4) * ($team -> seedavg / $team -> tmtseedmax);
        val score = .75 * (count - rank) / count + .25 * teamAvg / seedingMax

        (team, teamMax, teamAvg, rank, score)
    }

    scores.sortBy { case (_, _, _, _, score) => -score }
  })
  def ranking = _ranking.value.get

  //the team with the specified rank, or ByeTeam if there are less teams
  def teamByRank(rank: Int): TeamLike =
    ranking.map { case (team, _, _, _, _) => team }.applyOrElse(rank, { case _ => ByeTeam }: PartialFunction[Int, TeamLike])
}
