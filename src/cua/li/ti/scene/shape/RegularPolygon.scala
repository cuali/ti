package cua.li.ti.scene.shape

import scalafx.Includes._
import scalafx.beans.property.IntegerProperty
import scalafx.beans.property.DoubleProperty
import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.beans.property.ReadOnlyIntegerProperty
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
class RegularPolygon(inicialAngle :Double = -90, inicialSides :Int = 6)
        extends Polygon(new javafx.scene.shape.Polygon) {
	var centerX :Double = 0
	var centerY :Double = 0
	private[this] val radiusProperty = new DoubleProperty(Double.box(30), "radius")
	def radius :ReadOnlyDoubleProperty = radiusProperty
	def radius_=(value :Double) {
	    radiusProperty.value = value
	    computePoints
	}
	private [this] val angleProperty = new DoubleProperty(Double.box(inicialAngle), "angle")
	def angle :ReadOnlyDoubleProperty = angleProperty
	def angle_=(value :Double) {
	    angleProperty.value = value
	    alpha = Math.toRadians(value)
	    computePoints
	}
	private[this] val sidesProperty = new IntegerProperty(Int.box(inicialSides), "sides")
	def sides :ReadOnlyIntegerProperty = sidesProperty
	def sides_=(value :Int) {
	    sidesProperty.value = value
  		theta = Math.toRadians(360.0/value)
        computePoints
	}
	private[this] var theta :Double = 120
	private[this] var alpha :Double = 0
	private[this] def computePoints {
	    points.clear()
	    for (side <- 1 to sidesProperty.value) {
	        points.add(centerX + (Math.cos(alpha + theta*side) * radiusProperty.value))
	        points.add(centerY + (Math.sin(alpha + theta*side) * radiusProperty.value))
	    }
	}
	angle = inicialAngle
	sides = inicialSides
}
