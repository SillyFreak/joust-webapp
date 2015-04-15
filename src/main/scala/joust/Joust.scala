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
    val teams =
      List(
        ("0000", 9, 9, 1),
        ("0001", 9, 8, 1),
        ("0002", 9, 7, 1),
        ("0003", 9, 6, 1),
        ("0004", 9, 5, 1),
        ("0005", 9, 4, 1),
        ("0006", 9, 3, 1),
        ("0007", 9, 2, 1),
        ("0008", 9, 1, 1),
        ("0009", 8, 1, 1),
        ("000a", 7, 1, 1),
        ("000b", 6, 1, 1),
        ("000c", 5, 1, 1),
        ("000d", 4, 1, 1),
        ("000e", 3, 1, 1),
        ("000f", 2, 1, 1),
        ("0010", 1, 1, 1))
        .map { case (x, s1, s2, s3) => (Team(x, ""), s1, s2, s3) }

    val t = new Tournament(teams.map { case (x, _, _, _) => x })
    for ((team, s1, s2, s3) <- teams) {
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
    for (BracketMatch(id, a, b) <- t.bracketMatches) {
      println(s"${id.formatted("%02d")}:\t$a\t-  $b\t-- ${a.team.get.id} - ${b.team.get.id} -- ${t.bracketResults.winner(id).get.id}")
    }
  }
}
