/**
 * BracketStructure.scala
 *
 * Created on 25.04.2015
 */

package at.pria.joust.service

import at.pria.joust.model._

/**
 * <p>
 * {@code BracketStructure}
 * </p>
 *
 * @version V0.0 25.04.2015
 * @author SillyFreak
 */
class BracketStructure(val tournament: Tournament) {
  // the bracket size n is the lowest power of two that fits all teams, i.e.
  // 2^(ceil(log2(n)))
  val bracketSize = Integer.highestOneBit(tournament.teams.size - 1) * 2

  // for a bracket size of n, there are 2*n-1 matches.
  // The first round has n/2 main matches
  // The second round has n/4 main matches, n/4 consolation matches between
  // losers of the first round, and n/4 consolation matches between losers
  // of the first and second rounds, total 3*n/4 matches
  // Subsequent rounds have half that many matches, until there are only 3
  // (=3*n/n) matches left
  // Then there are one or two final matches: if the consolation winner wins
  // the first final match, there is a second final to determine the winner

  sealed trait BracketRound {
    val first: Int
    val count: Int
  }
  case class FirstRound(val count: Int) extends BracketRound {
    val first = 0
  }
  case class MainRound(val round: Int, val prev: BracketRound, val first: Int, val count: Int) extends BracketRound
  case class ConsolationRound1(val round: Int, val odd: Boolean, val prev: BracketRound, val first: Int, val count: Int) extends BracketRound
  case class ConsolationRound2(val round: Int, val odd: Boolean, val mPrev: BracketRound, val cPrev: BracketRound, val first: Int, val count: Int) extends BracketRound
  case class FinalRound(val round: Int, val mPrev: BracketRound, val cPrev: BracketRound, val first: Int) extends BracketRound {
    val count = 3
  }

  val rounds = {
    val rounds = List.newBuilder[BracketRound]
    var first = 0
    def add[R <: BracketRound](r: R) = {
      rounds += r
      first += r.count
      r
    }

    var n = bracketSize / 2
    val firstRound = add(FirstRound(n))

    var mPrev: BracketRound = firstRound
    var cPrev: BracketRound = firstRound
    var odd = true
    var round = 1
    do {
      n /= 2
      mPrev = add(MainRound(round + 0, mPrev, first, n))
      cPrev = add(ConsolationRound1(round + 1, odd, cPrev, first, n))
      cPrev = add(ConsolationRound2(round + 2, odd, mPrev, cPrev, first, n))
      odd = !odd
      round += 3
    } while (n > 1)
    add(FinalRound(round, mPrev, cPrev, first))

    rounds.result()
  }

  sealed trait TeamSource
  //no opponent
  case object ByeTeamSource extends TeamSource
  case class SeedingRankSource(rank: Int) extends TeamSource
  case class BracketWinnerSource(game: Int) extends TeamSource
  case class BracketLoserSource(game: Int) extends TeamSource
  //for the second final match:
  //if the main bracket winner won the first final, a bye, otherwise like a bracket source
  case class FinalWinnerSource(game: Int, main: Int) extends TeamSource
  case class FinalLoserSource(game: Int, consolation: Int) extends TeamSource
  //pseudo source that displays the bracket winner
  case class BracketWinner(val final1: Int, val final2: Int, val main: Int) extends TeamSource

  case class BracketGame(
    id: Int,
    round: Int,
    aTeam: TeamSource,
    bTeam: TeamSource)

  val games = {
    for {
      round <- rounds
      game <- round match {
        case FirstRound(count) =>
          // Create the first matches with teams assigned from seeding.
          // Teams paired up are determined recursively. Starting with a
          // single match (0, 1), this is expanded to (0, _), (_, 1) filling
          // in 2 and 3 such that highest is paired with lowest, or equally
          // so that the sum of two paired teams' ranks is (# teams - 1):
          // (0, 3), (2, 1).
          // Next step is (0, _), (_, 3), (2, _), (_, 1) ->
          // (0, 7), (4, 3), (2, 5), (6, 1) and so on.

          //TODO not tail recursive
          def firstMatches(teams: Int): List[(Int, Int)] = {
            if (teams == 2) List((0, 1))
            else {
              val sum = teams - 1
              firstMatches(teams / 2)
                .flatMap { case (a, b) => List((a, sum - a), (sum - b, b)) }
            }
          }
          val teamCount = tournament.teams.size
          for (((a, b), id) <- firstMatches(bracketSize).zipWithIndex) yield {
            BracketGame(id, 0,
              if (a < teamCount) SeedingRankSource(a) else ByeTeamSource,
              if (b < teamCount) SeedingRankSource(b) else ByeTeamSource)
          }

        case MainRound(round, prev, first, count) =>
          // a main round consists of the winners of the matches of the
          // previous main/first round

          for (i <- List(0 until count: _*)) yield {
            BracketGame(first + i, round,
              BracketWinnerSource(prev.first + 2 * i),
              BracketWinnerSource(prev.first + 2 * i + 1))
          }

        case ConsolationRound1(round, odd, prev: FirstRound, first, count) =>
          // the first consolation round, type 1, consists of the losers of
          // the matches of the first round

          for (i <- List(0 until count: _*)) yield {
            BracketGame(first + i, round,
              BracketLoserSource(prev.first + 2 * i),
              BracketLoserSource(prev.first + 2 * i + 1))
          }

        case ConsolationRound1(round, odd, prev: ConsolationRound2, first, count) =>
          // other consolation rounds, type 1, consist of the winners of
          // the matches of the previous consolation round

          for (i <- List(0 until count: _*)) yield {
            BracketGame(first + i, round,
              BracketWinnerSource(prev.first + 2 * i),
              BracketWinnerSource(prev.first + 2 * i + 1))
          }

        case ConsolationRound2(round, odd, mPrev: MainRound, cPrev: ConsolationRound1, first, count) =>
          // consolation rounds, type 2, consist of the losers of the matches
          // of the previous main round, and the winners of the matches of
          // the previous consolation round

          for (i <- List(0 until count: _*)) yield {
            val aSource = BracketWinnerSource(cPrev.first + i)
            val bSource = BracketLoserSource((mPrev.first + i) ^ (if (count > 1) 1 else 0))
            if (odd) BracketGame(first + i, round, aSource, bSource)
            else BracketGame(first + i, round, bSource, aSource)
          }

        case FinalRound(ord, mPrev, cPrev, first) =>
          // the final round consists of one or two final matches: if the
          // consolation winner wins the first final match, there is a second
          // final to determine the winner

          List(
            BracketGame(first, ord,
              BracketWinnerSource(mPrev.first),
              BracketWinnerSource(cPrev.first)),
            BracketGame(first + 1, ord + 1,
              FinalWinnerSource(first, mPrev.first),
              FinalLoserSource(first, cPrev.first)),
            BracketGame(first + 2, ord + 2,
              BracketWinner(first, first + 1, mPrev.first),
              ByeTeamSource))

        case x => throw new MatchError(x)
      }
    } yield game
  }
}
