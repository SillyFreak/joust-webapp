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
  private[this] val seedingRoundResults = collection.mutable.Map[Int, SeedingRoundResult]()
  def seedingRoundResult_=(id: Int, score: Int) = seedingRoundResults(id) = SeedingRoundResult(id, score)
  def seedingRoundResult(id: Int) = seedingRoundResults.get(id)

  private[this] val bracketMatchResults = collection.mutable.Map[Int, BracketMatchResult]()
  def bracketMatchResult_=(id: Int, winnerSideA: Boolean) = bracketMatchResults(id) = BracketMatchResult(id, winnerSideA)
  def bracketMatchResult(id: Int) = bracketMatchResults.get(id)

  def seedingRanking: Option[List[Team]] = {
    val scores = collection.mutable.Map[Team, Int](t.teams.map { t => (t, 0) }: _*)
    for (SeedingRound(id, _, team) <- t.seedingRounds) {
      seedingRoundResult(id) match {
        case None =>
          //seeding is not finished
          return None
        case Some(SeedingRoundResult(_, score)) =>
          scores(team) += score
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
      result <- bracketMatchResult(id)
      team <- {
        val game = t.bracketMatches(id)
        (if (result.winnerSideA) game.aTeamSource else game.bTeamSource).team
      }
    } yield team
  def bracketMatchLoser(id: Int): Option[TeamLike] =
    for {
      result <- bracketMatchResult(id)
      team <- {
        val game = t.bracketMatches(id)
        (if (result.winnerSideA) game.bTeamSource else game.aTeamSource).team
      }
    } yield team
}
