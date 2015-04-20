/**
 * Game.scala
 *
 * Created on 12.04.2015
 */

package at.pria.joust

import scala.beans.BeanProperty

case class SeedingRound(
  @BeanProperty val id: Int,
  @BeanProperty val round: Int,
  @BeanProperty val team: Team)

case class BracketMatch(
    @BeanProperty val id: Int,
    @BeanProperty val ord: Int,
    val aTeamSource: TeamSource,
    val bTeamSource: TeamSource) {
  //Java interop
  def getATeam() = aTeamSource.team.getOrElse(null)
  def getBTeam() = bTeamSource.team.getOrElse(null)
}

sealed trait TeamSource {
  def team: Option[TeamLike]
}
case class SeedingRank(val rank: Int)(implicit t: Tournament) extends TeamSource {
  def team = Some(t.seedingResults.teamByRank(rank))
}
case class BracketMatchWinner(val bracketMatch: Int)(implicit t: Tournament) extends TeamSource {
  def team = t.bracketResults.winner(bracketMatch)
}
case class BracketMatchLoser(val bracketMatch: Int)(implicit t: Tournament) extends TeamSource {
  def team = t.bracketResults.loser(bracketMatch)
}

//special team sources for the final matches
case class FinalMatchWinner(val finalMatch: Int, val main: Int)(implicit t: Tournament) extends TeamSource {
  def team =
    for (finalWinner <- t.bracketResults.winner(finalMatch); mainWinner <- t.bracketResults.winner(main))
      yield if (finalWinner == mainWinner) ByeTeam else finalWinner
}
case class FinalMatchLoser(val finalMatch: Int, val consolation: Int)(implicit t: Tournament) extends TeamSource {
  def team =
    for (finalLoser <- t.bracketResults.loser(finalMatch); consolationWinner <- t.bracketResults.winner(consolation))
      yield if (finalLoser == consolationWinner) ByeTeam else finalLoser
}

case class BracketWinner(val final1: Int, val final2: Int)(implicit t: Tournament) extends TeamSource {
  def team =
    for (final1Winner <- t.bracketResults.winner(final1); final2Winner <- t.bracketResults.winner(final2))
      yield if (final2Winner == ByeTeam) final1Winner else final2Winner
}

case object ByeTeamSource extends TeamSource {
  def team = Some(ByeTeam)
}

case class AllianceMatch(
  @BeanProperty val id: Int,
  @BeanProperty val aTeam: Team,
  @BeanProperty val bTeam: Team)
