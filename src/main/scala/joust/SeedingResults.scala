/**
 * SeedingResults.scala
 *
 * Created on 14.04.2015
 */

package joust

case class SeedingRoundResult(val id: Int, val score: Int)

class SeedingResults(t: Tournament) {
  private[this] val seedingRoundResults = collection.mutable.Map[SeedingRound, SeedingRoundResult]()
  def seedingRoundResult(sr: SeedingRound, score: Int) = {
    _seedingRanking.clear()
    seedingRoundResults(sr) = SeedingRoundResult(sr.id, score)
  }
  def seedingRoundResult(sr: SeedingRound) = seedingRoundResults.get(sr)

  def seedingScores(team: Team) =
    for {
      round <- 0 until t.numOfSeedingRounds
      result <- seedingRoundResult(t.seedingRoundsMap(team, round))
    } yield result.score

  def seedingMax(team: Team): Either[Int, Int] = {
    val scores = seedingScores(team).sorted

    val finished = scores.size == t.numOfSeedingRounds
    val score = scores.last

    //the seeding score is final, regular result
    if (finished) Right(score)
    //the seeding score is preliminary, exceptional result
    else Left(score)
  }

  def seedingAvg(team: Team): Either[Double, Double] = {
    val scores = seedingScores(team).sorted

    val finished = scores.size == t.numOfSeedingRounds
    val dropped = scores.takeRight(2)
    val score = dropped.sum.toDouble / dropped.size

    //the seeding score is final, regular result
    if (finished) Right(score)
    //the seeding score is preliminary, exceptional result
    else Left(score)
  }

  private[this] var _seedingRanking = new Cached({
    t.teams.sortBy {
      seedingAvg(_) match {
        case Right(score) => -score
        //seeding is not finished
        case Left(_)      => throw new IllegalStateException()
      }
    }
  })

  def seedingRanking: Option[List[Team]] = _seedingRanking.value

  def seedingRank(rank: Int): Option[TeamLike] =
    for {
      ranks <- seedingRanking
    } yield ranks.applyOrElse(rank, { case _ => ByeTeam }: PartialFunction[Int, TeamLike])
}
