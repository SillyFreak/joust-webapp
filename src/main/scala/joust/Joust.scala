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
        "0000", "0001", "0002", "0003",
        "0004", "0005", "0006", "0007",
        "0008", "0009", "000a", "000b",
        "000c", "000d", "000e", "000f", "0010")
        .map { x => Team(x, "") }

    val t = new Tournament(teams)
    for (s <- t.seedingRoundsList) {
      t.seedingResults.seedingRoundResult(s, 16 - s.id % 17)
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
      t.results.bracketMatchResult(m, winnerSideA)
    }
    for (BracketMatch(id, a, b) <- t.bracketMatches) {
      println(s"${id.formatted("%02d")}:\t$a\t-  $b\t-- ${a.team.get.id} - ${b.team.get.id} -- ${t.results.bracketMatchWinner(id).get.id}")
    }
  }
}
