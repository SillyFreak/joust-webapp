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
  def seedingRoundResult(sr: SeedingRound, score: Int) = seedingRoundResults(sr) = SeedingRoundResult(sr.id, score)
  def seedingRoundResult(sr: SeedingRound) = seedingRoundResults.get(sr)

  private[this] val bracketMatchResults = collection.mutable.Map[BracketMatch, BracketMatchResult]()
  def bracketMatchResult(bm: BracketMatch, winnerSideA: Boolean) = bracketMatchResults(bm) = BracketMatchResult(bm.id, winnerSideA)
  def bracketMatchResult(bm: BracketMatch) = bracketMatchResults.get(bm)

  def seedingRanking: Option[List[Team]] = {
    var scores = collection.mutable.ListBuffer[(Team, Int)]()
    for (team <- t.teams) {
      var teamScore = 0
      for (round <- 0 until t.numOfSeedingRounds) {
        seedingRoundResult(t.seedingRoundsMap(team, round)) match {
          case None =>
            //seeding is not finished
            return None
          case Some(SeedingRoundResult(_, score)) =>
            teamScore += score
        }
        scores += (team -> teamScore)
      }
    }
    Some(scores.toList.sortBy { case (team, score) => -score }.map { case (team, score) => team })
  }

  def seedingRank(rank: Int): Option[TeamLike] =
    for {
      ranks <- seedingRanking
    } yield ranks.applyOrElse(rank, { case _ => ByeTeam }: PartialFunction[Int, TeamLike])
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
