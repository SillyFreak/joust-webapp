/**
 * GameResult.scala
 *
 * Created on 12.04.2015
 */

package at.pria.joust

import scala.beans.BeanProperty

case class AllianceMatchResult(
  @BeanProperty val id: Int,
  @BeanProperty val score: Int)
