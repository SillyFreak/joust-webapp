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
    _seedingRanking = None
    seedingRoundResults(sr) = SeedingRoundResult(sr.id, score)
  }
  def seedingRoundResult(sr: SeedingRound) = seedingRoundResults.get(sr)

  def seedingScores(team: Team) =
    for {
      round <- 0 until t.numOfSeedingRounds
      result <- seedingRoundResult(t.seedingRoundsMap(team, round))
    } yield result.score

  def seedingMax(team: Team) = {
    val scores = seedingScores(team).sorted

    val finished = scores.size == t.numOfSeedingRounds
    val score = scores.last

    //the seeding score is final, regular result
    if (finished) Right(score)
    //the seeding score is preliminary, exceptional result
    else Left(score)
  }

  def seedingAvg(team: Team) = {
    val scores = seedingScores(team).sorted

    val finished = scores.size == t.numOfSeedingRounds
    val dropped = scores.takeRight(2)
    val score = dropped.sum.toDouble / dropped.size

    //the seeding score is final, regular result
    if (finished) Right(score)
    //the seeding score is preliminary, exceptional result
    else Left(score)
  }

  private[this] var _seedingRanking: Option[List[Team]] = None

  private[this] def buildSeedingRanking() = {
    var scores = collection.mutable.ListBuffer[(Team, Double)]()
    for (team <- t.teams) {
      seedingAvg(team) match {
        case Right(score) =>
          scores += (team -> score)
        case Left(_) =>
          //seeding is not finished
          throw new IllegalStateException()
      }
    }
    scores.toList
      .sortBy { case (team, score) => -score }
      .map { case (team, score) => team }
  }

  def seedingRanking: Option[List[Team]] =
    _seedingRanking match {
      case ranking @ Some(_) =>
        ranking
      case None => try {
        _seedingRanking = Some(buildSeedingRanking())
        _seedingRanking
      } catch {
        case ex: IllegalStateException => None
      }
    }

  def seedingRank(rank: Int): Option[TeamLike] =
    for {
      ranks <- seedingRanking
    } yield ranks.applyOrElse(rank, { case _ => ByeTeam }: PartialFunction[Int, TeamLike])

  private[this] val bracketMatchResults = collection.mutable.Map[BracketMatch, BracketMatchResult]()
  def bracketMatchResult(bm: BracketMatch, winnerSideA: Boolean) = bracketMatchResults(bm) = BracketMatchResult(bm.id, winnerSideA)
  def bracketMatchResult(bm: BracketMatch) = bracketMatchResults.get(bm)

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
