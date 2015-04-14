/**
 * TournamentResults.scala
 *
 * Created on 13.04.2015
 */
package joust

/**
 * <p>
 * {@code TournamentResults}
 * </p>
 *
 * @version V0.0 13.04.2015
 * @author SillyFreak
 */
class TournamentResults(t: Tournament) {
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

  private[this] val bracketMatchResults = collection.mutable.Map[BracketMatch, BracketMatchResult]()
  def bracketMatchResult(bm: BracketMatch, winnerSideA: Boolean) = bracketMatchResults(bm) = BracketMatchResult(bm.id, winnerSideA)
  def bracketMatchResult(bm: BracketMatch) = bracketMatchResults.get(bm)

  def deScore(team: Team): Either[Int, Int] = {
    var finished = true
    var score = -1
    for (game <- t.bracketMatches) {
      val result = bracketMatchResult(game)
      finished &= result.nonEmpty
      for (a <- game.aTeamSource.team; b <- game.bTeamSource.team)
        //games are in increasing ID order -> last match ID is max
        if (a == team || b == team) score = game.id
    }

    //the DE score is final, regular result
    if (finished) Right(score)
    //the DE score is preliminary, exceptional result
    else Left(score)
  }

  private[this] var _deRanking = new Cached({
    t.teams.sortBy {
      deScore(_) match {
        case Right(score) => -score
        //DE is not finished
        case Left(_)      => throw new IllegalStateException()
      }
    }
  })

  def deRanking: Option[List[Team]] = _deRanking.value

  def bracketMatchWinner(id: Int): Option[TeamLike] =
    for {
      result <- bracketMatchResult(t.bracketMatches(id))
      team <- {
        val game = t.bracketMatches(id)
        (if (result.winnerSideA) game.aTeamSource else game.bTeamSource).team
      }
    } yield team

  def bracketMatchLoser(id: Int): Option[TeamLike] =
    for {
      result <- bracketMatchResult(t.bracketMatches(id))
      team <- {
        val game = t.bracketMatches(id)
        (if (result.winnerSideA) game.bTeamSource else game.aTeamSource).team
      }
    } yield team
}
