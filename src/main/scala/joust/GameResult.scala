/**
 * GameResult.scala
 *
 * Created on 12.04.2015
 */

package joust

case class BracketMatchResult(val id: Int, val winnerSideA: Boolean)

case class AllianceMatchResult(val id: Int, val score: Int)
