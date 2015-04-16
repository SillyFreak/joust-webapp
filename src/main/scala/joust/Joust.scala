/**
 * Joust.scala
 *
 * Created on 11.04.2015
 */

package joust

import scala.annotation.tailrec

/**
 * <p>
 * {@code Joust}
 * </p>
 *
 * @version V0.0 11.04.2015
 * @author SillyFreak
 */
object Joust {
  def main(args: Array[String]): Unit = {
    val seeding =
      List(
        ("0000", 9, 7, 0),
        ("0001", 9, 6, 0),
        ("0002", 9, 5, 0),
        ("0003", 9, 4, 0),
        ("0004", 9, 3, 0),
        ("0005", 9, 2, 0),
        ("0006", 9, 1, 0),
        ("0007", 9, 0, 0),
        ("0008", 8, 0, 0),
        ("0009", 7, 0, 0),
        ("000a", 6, 0, 0),
        ("000b", 5, 0, 0),
        ("000c", 4, 0, 0),
        ("000d", 3, 0, 0),
        ("000e", 2, 0, 0),
        ("000f", 1, 0, 0),
        ("0010", 0, 0, 0))
        .map { case (x, s1, s2, s3) => (Team(x, ""), s1, s2, s3) }
    val teams = seeding.map { case (x, _, _, _) => x }

    val t = new Tournament(teams)
    for ((team, s1, s2, s3) <- seeding) {
      t.seedingResults.result(t.seedingRoundsMap(team, 0), s1)
      t.seedingResults.result(t.seedingRoundsMap(team, 1), s2)
      t.seedingResults.result(t.seedingRoundsMap(team, 2), s3)
    }
    for (m <- t.bracketMatches) {
      val a = m.aTeamSource.team.get
      val b = m.bTeamSource.team.get
      val winnerSideA = (a, b) match {
        case (ByeTeam, ByeTeam)       => true
        case (ByeTeam, _)             => false
        case (_, ByeTeam)             => true
        case (Team(a, _), Team(b, _)) => Integer.parseInt(a, 0x10) < Integer.parseInt(b, 0x10)
      }
      t.bracketResults.result(m, winnerSideA)
    }

    for ((team, max, avg, rank, score) <- t.seedingResults.ranking) {
      val scores = t.seedingResults.scores(team)
      val (_, deRank, deScore) = t.bracketResults.ranking.find { case (t, _, _) => t == team }.get
      printf("%s | %2d %s %d %f - %f | %2d - %f%n", team.id, rank, scores, max, avg, score, deRank, deScore)
    }

    //for (BracketMatch(id, a, b) <- t.bracketMatches) {
    //  println(s"${id.formatted("%02d")}:\t$a\t-  $b\t-- ${a.team.get.id} - ${b.team.get.id} -- ${t.bracketResults.winner(id).get.id}")
    //}
  }
}
