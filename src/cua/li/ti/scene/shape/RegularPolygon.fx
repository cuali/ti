package cua.li.ti.scene.shape;

import javafx.scene.shape.Polygon;
import javafx.util.Math;

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
public class RegularPolygon extends Polygon {
    public var centerX :Number;
    public var centerY :Number;
    public var radius :Number = 30;
    public var angle :Number = -90;
    public var sides :Number =  6;
    def theta = bind Math.toRadians(360.0/sides);
    def alpha = bind Math.toRadians(angle);
    override var points = bind for (side in [1 .. sides]) {
        [
            centerX + (Math.cos(alpha + theta*side) * radius),
            centerY + (Math.sin(alpha + theta*side) * radius)
        ]
    };
}
