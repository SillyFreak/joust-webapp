/**
 * InitService.scala
 *
 * Created on 24.04.2015
 */

package at.pria.joust.service

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

import at.pria.joust.model._

import scala.collection.JavaConversions._

/**
 * <p>
 * {@code InitService}
 * </p>
 *
 * @version V0.0 24.04.2015
 * @author SillyFreak
 */
@Service
class InitService {
  @Autowired
  private[this] var tournamentRepo: TournamentRepository = _
  @Autowired
  private[this] var teamRepo: TeamRepository = _
  @Autowired
  private[this] var gameRepo: GameRepository = _

  private[this] def mkTeam(teamId: String, name: String) = {
    val t = new Team
    t.teamId = teamId
    t.name = name
    teamRepo.save(t)
  }

  private[this] def mkTournament(name: String, teams: Team*) = {
    val tournament = new Tournament
    tournament.name = name
    tournament.teams.addAll(teams)
    tournamentRepo.save(tournament)
  }

  private[this] def mkSeeding(tournament: Tournament) = {
    for (round <- 0 until 3; team <- tournament.teams) {
      val game = new SeedingGame
      game.tournament = tournament
      game.team = team
      game.round = round
      gameRepo.save(game)
    }
  }

  private[this] def mkBracket(tournament: Tournament) = {
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

    sealed trait Round {
      val first: Int
      val count: Int
    }
    case class FirstRound(val count: Int) extends Round {
      val first = 0
    }
    case class MainRound(val ord: Int, val prev: Round, val first: Int, val count: Int) extends Round
    case class ConsolationRound1(val ord: Int, val odd: Boolean, val prev: Round, val first: Int, val count: Int) extends Round
    case class ConsolationRound2(val ord: Int, val odd: Boolean, val mPrev: Round, val cPrev: Round, val first: Int, val count: Int) extends Round
    case class FinalRound(val ord: Int, val mPrev: Round, val cPrev: Round, val first: Int) extends Round {
      val count = 3
    }

    val rounds = {
      val rounds = List.newBuilder[Round]
      var first = 0
      def add[R <: Round](r: R) = {
        rounds += r
        first += r.count
        r
      }

      var n = bracketSize / 2
      val firstRound = add(FirstRound(n))

      var mPrev: Round = firstRound
      var cPrev: Round = firstRound
      var odd = true
      var ord = 1
      do {
        n /= 2
        mPrev = add(MainRound(ord + 0, mPrev, first, n))
        cPrev = add(ConsolationRound1(ord + 1, odd, cPrev, first, n))
        cPrev = add(ConsolationRound2(ord + 2, odd, mPrev, cPrev, first, n))
        odd = !odd
        ord += 3
      } while (n > 1)
      add(FinalRound(ord, mPrev, cPrev, first))

      rounds.result()
    }

    val matches = {
      implicit val t: Tournament = Tournament.this
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
            for (((a, b), id) <- firstMatches(bracketSize).zipWithIndex) yield {
              BracketMatch(id, 0, SeedingRank(a), SeedingRank(b))
            }

          case MainRound(ord, prev, first, count) =>
            // a main round consists of the winners of the matches of the
            // previous main/first round

            for (i <- List(0 until count: _*)) yield {
              BracketMatch(first + i, ord,
                BracketMatchWinner(prev.first + 2 * i),
                BracketMatchWinner(prev.first + 2 * i + 1))
            }

          case ConsolationRound1(ord, odd, prev: FirstRound, first, count) =>
            // the first consolation round, type 1, consists of the losers of
            // the matches of the first round

            for (i <- List(0 until count: _*)) yield {
              BracketMatch(first + i, ord,
                BracketMatchLoser(prev.first + 2 * i),
                BracketMatchLoser(prev.first + 2 * i + 1))
            }

          case ConsolationRound1(ord, odd, prev: ConsolationRound2, first, count) =>
            // other consolation rounds, type 1, consist of the winners of
            // the matches of the previous consolation round

            for (i <- List(0 until count: _*)) yield {
              val a = BracketMatchWinner(prev.first + 2 * i)
              val b = BracketMatchWinner(prev.first + 2 * i + 1)
              BracketMatch(first + i, ord, a, b)
            }

          case ConsolationRound2(ord, odd, mPrev: MainRound, cPrev: ConsolationRound1, first, count) =>
            // consolation rounds, type 2, consist of the losers of the matches
            // of the previous main round, and the winners of the matches of
            // the previous consolation round

            for (i <- List(0 until count: _*)) yield {
              val a = BracketMatchWinner(cPrev.first + i)
              val b = BracketMatchLoser((mPrev.first + i) ^ (if (count > 1) 1 else 0))
              if (odd) BracketMatch(first + i, ord, a, b)
              else BracketMatch(first + i, ord, b, a)
            }

          case FinalRound(ord, mPrev, cPrev, first) =>
            // the final round consists of one or two final matches: if the
            // consolation winner wins the first final match, there is a second
            // final to determine the winner

            List(
              BracketMatch(first, ord,
                BracketMatchWinner(mPrev.first),
                BracketMatchWinner(cPrev.first)),
              BracketMatch(first + 1, ord + 1,
                FinalMatchWinner(first, mPrev.first),
                FinalMatchLoser(first, cPrev.first)),
              BracketMatch(first + 2, ord + 2,
                BracketWinner(first, first + 1, mPrev.first),
                ByeTeamSource))

          case x => throw new MatchError(x)
        }
      } yield game
    }
  }

  def apply(): Unit =
    if (tournamentRepo.count() == 0) {
      val botball =
        mkTournament("Botball",
          (for (i <- List(0 to 16: _*)) yield mkTeam("15-%04d".format(i), "TGM")): _*)
      mkSeeding(botball)
      mkBracket(botball)
    }
}
