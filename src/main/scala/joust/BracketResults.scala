/**
 * BracketResults.scala
 *
 * Created on 13.04.2015
 */

package joust

case class BracketMatchResult(val id: Int, val winnerSideA: Boolean)

class BracketResults(t: Tournament) {

  private[this] val results = collection.mutable.Map[BracketMatch, BracketMatchResult]()
  def result(bm: BracketMatch, winnerSideA: Boolean) = results(bm) = BracketMatchResult(bm.id, winnerSideA)
  def result(bm: BracketMatch) = results.get(bm)

  def score(team: Team): Either[Int, Int] = {
    var finished = true
    var score = -1
    for (game <- t.bracketMatches) {
      val result = this.result(game)
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

  private[this] var _ranking = new Cached({
    t.teams.sortBy {
      score(_) match {
        case Right(score) => -score
        case Left(score)  => -score
      }
    }
  })

  def ranking = _ranking.value.get

  def winner(id: Int): Option[TeamLike] =
    for {
      result <- this.result(t.bracketMatches(id))
      team <- {
        val game = t.bracketMatches(id)
        (if (result.winnerSideA) game.aTeamSource else game.bTeamSource).team
      }
    } yield team

  def loser(id: Int): Option[TeamLike] =
    for {
      result <- this.result(t.bracketMatches(id))
      team <- {
        val game = t.bracketMatches(id)
        (if (result.winnerSideA) game.bTeamSource else game.aTeamSource).team
      }
    } yield team
}
