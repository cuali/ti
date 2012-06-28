package cua.li.ti.scene.shape

import scalafx.Includes._
import scalafx.beans.property.IntegerProperty
import scalafx.beans.property.DoubleProperty
import scalafx.scene.shape.Polygon

/**
 * A simple regular polygon can be defined by:
 * <ol>
 * <li>its center</li>
 * <li>its number of sides</li>
 * <li>its radius</li>
 * <li>its inclination angle</li>
 * </ol>
 * and a little bit of maths to compute the coordinates of each of its vertices.
 *
 * @author A@cua.li
 */
class RegularPolygon(var inicialAngle :Double = -90, var inicialSides :Int = 6)
  extends Polygon(new javafx.scene.shape.Polygon) {
  var centerX :Double = 0
  var centerY :Double = 0
  private[this] val radiusProperty = DoubleProperty(30)
  def radius :Double = radiusProperty()
  def radius_=(value :Double) {
    radiusProperty() = value
    computePoints
  }
  private[this] val angleProperty = DoubleProperty(inicialAngle)
  def angle :Double = angleProperty()
  def angle_=(value :Double) {
    angleProperty() = value
    alpha = Math.toRadians(value)
    computePoints
  }
  private[this] val sidesProperty = IntegerProperty(inicialSides)
  def sides :Int = sidesProperty()
  def sides_=(value :Int) {
    sidesProperty() = value
    theta = Math.toRadians(360.0 / value)
    computePoints
  }
  private[this] var theta :Double = 120
  private[this] var alpha :Double = 0
  private[this] def computePoints {
    points.clear
    for (side <- 1 to sides) {
      points.add(centerX + (Math.cos(alpha + theta * side) * radius))
      points.add(centerY + (Math.sin(alpha + theta * side) * radius))
    }
  }
  angle = inicialAngle
  sides = inicialSides
}
