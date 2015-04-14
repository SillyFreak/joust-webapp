/**
 * Game.scala
 *
 * Created on 12.04.2015
 */

package joust

case class SeedingRound(val id: Int, val round: Int, val team: Team)

case class BracketMatch(val id: Int, val aTeamSource: TeamSource, val bTeamSource: TeamSource)

sealed trait TeamSource {
  def team: Option[TeamLike]
}
case class SeedingRank(val rank: Int)(implicit t: Tournament) extends TeamSource {
  def team = t.seedingResults.seedingRank(rank)
}
case class BracketMatchWinner(val bracketMatch: Int)(implicit t: Tournament) extends TeamSource {
  def team = t.results.bracketMatchWinner(bracketMatch)
}
case class BracketMatchLoser(val bracketMatch: Int)(implicit t: Tournament) extends TeamSource {
  def team = t.results.bracketMatchLoser(bracketMatch)
}

case class AllianceMatch(val id: Int, val aTeam: Team, val bTeam: Team)
