/**
 * BracketResults.scala
 *
 * Created on 13.04.2015
 */

package joust

case class BracketMatchResult(val id: Int, val winnerSideA: Boolean)

class BracketResults(t: Tournament) {
  private[this] val _results = collection.mutable.Map[BracketMatch, BracketMatchResult]()
  def result(bm: BracketMatch, winnerSideA: Boolean) = _results(bm) = {
    _ranking.clear()
    BracketMatchResult(bm.id, winnerSideA)
  }
  def result(bm: BracketMatch) = _results.get(bm)

  def clear() = {
    _ranking.clear()
    _results.clear()
  }

  def score(team: Team): Either[Int, Int] = {
    var finished = true
    var score = -1
    for (game <- t.bracketMatches) {
      val result = this.result(game)
      finished &= result.nonEmpty
      for (a <- game.aTeamSource.team; b <- game.bTeamSource.team)
        //games are in increasing ID order -> last match ID is max
        if (a == team || b == team) score = game.ord
    }

    //the DE score is final, regular result
    if (finished) Right(score)
    //the DE score is preliminary, exceptional result
    else Left(score)
  }

  //the list of teams, ordered by rank
  //List[(team, rank, score)]
  private[this] val _ranking = new Cached({
    val count = t.teams.size.asInstanceOf[Double]

    val _scores = t.teams.map { team =>
      score(team) match {
        case Right(score) => (team, score)
        case Left(score)  => (team, score)
      }
    }

    val scores = _scores.map {
      case (team, teamScore) =>
        val rank = _scores.count { case (_, score) => score > teamScore }
        val score = (count - rank) / count

        (team, rank, score)
    }

    scores.sortBy { case (_, rank, _) => rank }
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
