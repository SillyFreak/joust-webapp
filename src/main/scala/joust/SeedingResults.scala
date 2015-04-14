/**
 * SeedingResults.scala
 *
 * Created on 14.04.2015
 */

package joust

case class SeedingRoundResult(val id: Int, val score: Int)

class SeedingResults(t: Tournament) {
  private[this] val results = collection.mutable.Map[SeedingRound, SeedingRoundResult]()
  def result(sr: SeedingRound, score: Int) = {
    _ranking.clear()
    results(sr) = SeedingRoundResult(sr.id, score)
  }
  def result(sr: SeedingRound) = results.get(sr)

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

  private[this] var _ranking = new Cached({
    var finished = true

    val avgs = t.teams.map { team =>
      team -> (avg(team) match {
        case Right(score) => score
        case Left(score)  => finished = false; score
      })
    }

    val places = avgs.map {
      case (team, teamAvg) => (team, avgs.count { case (_, avg) => avg > teamAvg })
    }

    places.sortBy { case (_, place) => place }
  })

  //the list of teams, ordered by average score
  def ranking = _ranking.value.get

  //the team with the specified rank, or ByeTeam if there are less teams
  def teamByRank(rank: Int): TeamLike =
    ranking.map { case (team, _) => team }.applyOrElse(rank, { case _ => ByeTeam }: PartialFunction[Int, TeamLike])
}
