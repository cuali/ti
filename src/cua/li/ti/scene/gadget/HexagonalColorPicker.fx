package cua.li.ti.scene.gadget;

import cua.li.ti.scene.shape.RegularPolygon;

import java.lang.IllegalArgumentException;

import javafx.scene.CustomNode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.util.Math;

def SQRT_3 = Math.sqrt(3);
def HALF_SQRT_3 = SQRT_3 / 2;
class ColorCell {
    var shift :String;  // ftyhbv
    var color :String;
    var legend :String = 'W';  // 'B' or 'W'
}

/**
 * Based on original idea by Bob Stein (http://www.VisiBone.com)
 * @author A@cua.li
 */
public class HexagonalColorPicker extends CustomNode {
    /**
    * The onClose function attribute is executed when the
    * the center or the border is pressed, passing the chosen color
    * or original color, depending upon which was pressed.
    */
    public var onClose: function(color:Color,changed:Boolean):Void;
    public-read var chosen :Paint;
    public var original = Color.TRANSPARENT;
    public-init var centerX :Number = 50;
    public-init var centerY :Number = 120;
    public-init var radius :Number = 100;
    def cellRadius = bind radius * 3 / (26*SQRT_3);
    override var managed = true;
    override var visible = false on replace {
        if (visible) {
            managed = false;
            chosen = original;
            this.toFront()
        } else {
            managed = true
        }
    };
    postinit { draw() }
    var centralLabel :String;
    var centralPaint :Paint;
    var centralHexagon :RegularPolygon;
    var currentHexagon :RegularPolygon;
    function draw() :Void {
        def colorName = Text {
            font: Font { name: "Consolas Bold" size: cellRadius * 0.707 }
            wrappingWidth: cellRadius * 3.3
            translateX: cellRadius * 3.3 / -2
            visible: false
        };
        this.children = [
            RegularPolygon {
                centerX: centerX
                centerY: centerY
                radius: radius
                angle: 0
                strokeWidth: bind cellRadius
                strokeLineJoin: StrokeLineJoin.ROUND
                stroke: Color.BLACK
                fill: Color.TRANSPARENT
                blocksMouse: true
                onMouseClicked: function (me :MouseEvent) :Void {
                    colorName.visible = false;
                    this.managed = false;
                    this.visible = false;
                    onClose(original, false)
                }
            }
            colorName
        ];
        var relPosX = 0.0;
        var relPosY = 0.0;
        insert
            currentHexagon = centralHexagon = RegularPolygon {
                centerX: centerX + relPosX
                centerY: centerY + relPosY
                radius: cellRadius
                strokeWidth: 0
                stroke: Color.TRANSPARENT
                fill: bind chosen with inverse
                blocksMouse: true
                onMouseEntered: function (me :MouseEvent) :Void {
                    centralHexagon.toFront();
                    colorName.content = centralLabel;
                    colorName.fill = centralPaint;
                    colorName.x = centralHexagon.centerX;
                    colorName.y = centralHexagon.centerY;
                    colorName.visible = true;
                    colorName.toFront();
                    centralHexagon.transforms = Transform.scale(3, 3, centralHexagon.centerX, centralHexagon.centerY)
                }
                onMouseExited: function (me :MouseEvent) :Void {
                    colorName.visible = false;
                    delete centralHexagon.transforms
                }
                onMouseClicked: function (me :MouseEvent) :Void {
                    centralLabel = '';
                    colorName.visible = false;
                    this.visible = false;
                    onClose(chosen as Color, not chosen.equals(original))
                }
            }
        into this.children;
        for (cell in CELLS) {
            def index = "ftyhbv".indexOf(cell.shift.toLowerCase());
            if (0 > index) throw new IllegalArgumentException("Unknown position shift {cell.shift}");
            relPosX += horizontalShift[index];
            relPosY += verticalShift[index];
            def cellColor = "#{cell.color}";
            insert
                RegularPolygon {
                    centerX: centerX + relPosX
                    centerY: centerY + relPosY
                    radius: cellRadius
                    strokeWidth: 0
                    stroke: Color.TRANSPARENT
                    fill: Color.web(cellColor)
                    blocksMouse: true
                    onMouseEntered: function (me :MouseEvent) :Void {
                        delete currentHexagon.transforms;
                        def node = me.source as RegularPolygon;
                        node.toFront();
                        colorName.content = cellColor;
                        colorName.fill = if ('W' == cell.legend) then Color.WHITE else Color.BLACK;
                        colorName.x = node.centerX;
                        colorName.y = node.centerY;
                        colorName.visible = true;
                        colorName.toFront();
                        currentHexagon = node;
                        node.transforms = Transform.scale(2, 2, node.centerX, node.centerY)
                    }
                    onMouseExited: function (me :MouseEvent) :Void {
                        colorName.visible = false;
                        delete me.source.transforms
                    }
                    onMouseClicked: function (me :MouseEvent) :Void {
                        centralLabel = cellColor;
                        centralPaint = colorName.fill;
                        centralHexagon.fill = (me.source as RegularPolygon).fill
                    }
                }
            into this.children
        }
    }
    def horizontalShift = bind [
        -cellRadius * SQRT_3/*f*/,
        -cellRadius * HALF_SQRT_3/*t*/,
        cellRadius * HALF_SQRT_3/*y*/,
        cellRadius * SQRT_3/*h*/,
        cellRadius * HALF_SQRT_3/*b*/,
        -cellRadius * HALF_SQRT_3]/*v*/;
    def verticalShift = bind [0, -cellRadius*3/2, -cellRadius*3/2, 0, cellRadius*3/2, cellRadius*3/2];
}
//<editor-fold defaultstate="collapsed" desc="carefully ordered sequence of 255 color cells">
def CELLS: ColorCell[] = [
    ColorCell {shift: 'h' color: "000000"}
    ColorCell {shift: 'v' color: "333333"}
    ColorCell {shift: 'f' color: "666666"}
    ColorCell {shift: 't' color: "999999" legend: 'B'}
    ColorCell {shift: 'y' color: "cccccc" legend: 'B'}
    ColorCell {shift: 'h' color: "ffffff" legend: 'B'}
    ColorCell {shift: 'h' color: "ffccff" legend: 'B'}
    ColorCell {shift: 'b' color: "cc99ff" legend: 'B'}
    ColorCell {shift: 'v' color: "ccccff" legend: 'B'}
    ColorCell {shift: 'v' color: "99ccff" legend: 'B'}
    ColorCell {shift: 'f' color: "ccffff" legend: 'B'}
    ColorCell {shift: 'f' color: "99ffcc" legend: 'B'}
    ColorCell {shift: 't' color: "ccffcc" legend: 'B'}
    ColorCell {shift: 't' color: "ccff99" legend: 'B'}
    ColorCell {shift: 'y' color: "ffffcc" legend: 'B'}
    ColorCell {shift: 'y' color: "ffcc99" legend: 'B'}
    ColorCell {shift: 'h' color: "ffcccc" legend: 'B'}
    ColorCell {shift: 'h' color: "ff99cc" legend: 'B'}
    ColorCell {shift: 'h' color: "cc99cc" legend: 'B'}
    ColorCell {shift: 'b' color: "ff99ff" legend: 'B'}
    ColorCell {shift: 'b' color: "9966cc"}
    ColorCell {shift: 'v' color: "9999cc" legend: 'B'}
    ColorCell {shift: 'v' color: "9999ff" legend: 'B'}
    ColorCell {shift: 'v' color: "6699cc"}
    ColorCell {shift: 'f' color: "99cccc" legend: 'B'}
    ColorCell {shift: 'f' color: "99ffff" legend: 'B'}
    ColorCell {shift: 'f' color: "66cc99" legend: 'B'}
    ColorCell {shift: 't' color: "99cc99" legend: 'B'}
    ColorCell {shift: 't' color: "99ff99" legend: 'B'}
    ColorCell {shift: 't' color: "99cc66" legend: 'B'}
    ColorCell {shift: 'y' color: "cccc99" legend: 'B'}
    ColorCell {shift: 'y' color: "ffff99" legend: 'B'}
    ColorCell {shift: 'y' color: "cc9966" legend: 'B'}
    ColorCell {shift: 'h' color: "cc9999" legend: 'B'}
    ColorCell {shift: 'h' color: "ff9999" legend: 'B'}
    ColorCell {shift: 'h' color: "cc6699"}
    ColorCell {shift: 'h' color: "996699"}
    ColorCell {shift: 'b' color: "cc66cc"}
    ColorCell {shift: 'b' color: "ff66ff" legend: 'B'}
    ColorCell {shift: 'b' color: "663399"}
    ColorCell {shift: 'v' color: "666699"}
    ColorCell {shift: 'v' color: "6666cc"}
    ColorCell {shift: 'v' color: "6666ff"}
    ColorCell {shift: 'v' color: "336699"}
    ColorCell {shift: 'f' color: "669999"}
    ColorCell {shift: 'f' color: "66cccc" legend: 'B'}
    ColorCell {shift: 'f' color: "66ffff" legend: 'B'}
    ColorCell {shift: 'f' color: "339966"}
    ColorCell {shift: 't' color: "669966"}
    ColorCell {shift: 't' color: "66cc66" legend: 'B'}
    ColorCell {shift: 't' color: "66ff66" legend: 'B'}
    ColorCell {shift: 't' color: "669933"}
    ColorCell {shift: 'y' color: "999966"}
    ColorCell {shift: 'y' color: "cccc66" legend: 'B'}
    ColorCell {shift: 'y' color: "ffff66" legend: 'B'}
    ColorCell {shift: 'y' color: "996633"}
    ColorCell {shift: 'h' color: "996666"}
    ColorCell {shift: 'h' color: "cc6666"}
    ColorCell {shift: 'h' color: "ff6666"}
    ColorCell {shift: 'h' color: "993366"}
    ColorCell {shift: 'h' color: "663366"}
    ColorCell {shift: 'b' color: "993399"}
    ColorCell {shift: 'b' color: "cc33cc"}
    ColorCell {shift: 'b' color: "ff33ff"}
    ColorCell {shift: 'b' color: "330066"}
    ColorCell {shift: 'v' color: "333366"}
    ColorCell {shift: 'v' color: "333399"}
    ColorCell {shift: 'v' color: "3333cc"}
    ColorCell {shift: 'v' color: "3333ff"}
    ColorCell {shift: 'v' color: "003366"}
    ColorCell {shift: 'f' color: "336666"}
    ColorCell {shift: 'f' color: "339999"}
    ColorCell {shift: 'f' color: "33cccc" legend: 'B'}
    ColorCell {shift: 'f' color: "33ffff" legend: 'B'}
    ColorCell {shift: 'f' color: "006633"}
    ColorCell {shift: 't' color: "336633"}
    ColorCell {shift: 't' color: "339933"}
    ColorCell {shift: 't' color: "33cc33"}
    ColorCell {shift: 't' color: "33ff33" legend: 'B'}
    ColorCell {shift: 't' color: "336600"}
    ColorCell {shift: 'y' color: "666633"}
    ColorCell {shift: 'y' color: "999933"}
    ColorCell {shift: 'y' color: "cccc33" legend: 'B'}
    ColorCell {shift: 'y' color: "ffff33" legend: 'B'}
    ColorCell {shift: 'y' color: "663300"}
    ColorCell {shift: 'h' color: "663333"}
    ColorCell {shift: 'h' color: "993333"}
    ColorCell {shift: 'h' color: "cc3333"}
    ColorCell {shift: 'h' color: "ff3333"}
    ColorCell {shift: 'h' color: "660033"}
    ColorCell {shift: 'h' color: "330033"}
    ColorCell {shift: 'b' color: "660066"}
    ColorCell {shift: 'b' color: "990099"}
    ColorCell {shift: 'b' color: "cc00cc"}
    ColorCell {shift: 'b' color: "ff00ff"}
    ColorCell {shift: 'b' color: "6600cc"}
    ColorCell {shift: 'v' color: "000033"}
    ColorCell {shift: 'v' color: "000066"}
    ColorCell {shift: 'v' color: "000099"}
    ColorCell {shift: 'v' color: "0000cc"}
    ColorCell {shift: 'v' color: "0000ff"}
    ColorCell {shift: 'v' color: "0066cc"}
    ColorCell {shift: 'f' color: "003333"}
    ColorCell {shift: 'f' color: "006666"}
    ColorCell {shift: 'f' color: "009999"}
    ColorCell {shift: 'f' color: "00cccc"}
    ColorCell {shift: 'f' color: "00ffff" legend: 'B'}
    ColorCell {shift: 'f' color: "00cc66"}
    ColorCell {shift: 't' color: "003300"}
    ColorCell {shift: 't' color: "006600"}
    ColorCell {shift: 't' color: "009900"}
    ColorCell {shift: 't' color: "00cc00"}
    ColorCell {shift: 't' color: "00ff00" legend: 'B'}
    ColorCell {shift: 't' color: "66cc00"}
    ColorCell {shift: 'y' color: "333300"}
    ColorCell {shift: 'y' color: "666600"}
    ColorCell {shift: 'y' color: "999900"}
    ColorCell {shift: 'y' color: "cccc00" legend: 'B'}
    ColorCell {shift: 'y' color: "ffff00" legend: 'B'}
    ColorCell {shift: 'y' color: "cc6600"}
    ColorCell {shift: 'h' color: "330000"}
    ColorCell {shift: 'h' color: "660000"}
    ColorCell {shift: 'h' color: "990000"}
    ColorCell {shift: 'h' color: "cc0000"}
    ColorCell {shift: 'h' color: "ff0000"}
    ColorCell {shift: 'h' color: "cc0066"}
    ColorCell {shift: 'h' color: "ff66cc" legend: 'B'}
    ColorCell {shift: 'b' color: "cc3399"}
    ColorCell {shift: 'b' color: "990066"}
    ColorCell {shift: 'b' color: "660099"}
    ColorCell {shift: 'b' color: "9933cc"}
    ColorCell {shift: 'b' color: "cc66ff" legend: 'B'}
    ColorCell {shift: 'b' color: "9933ff"}
    ColorCell {shift: 'v' color: "9966ff"}
    ColorCell {shift: 'v' color: "6633cc"}
    ColorCell {shift: 'v' color: "330099"}
    ColorCell {shift: 'v' color: "003399"}
    ColorCell {shift: 'v' color: "3366cc"}
    ColorCell {shift: 'v' color: "6699ff" legend: 'B'}
    ColorCell {shift: 'v' color: "3399ff"}
    ColorCell {shift: 'f' color: "66ccff" legend: 'B'}
    ColorCell {shift: 'f' color: "3399cc"}
    ColorCell {shift: 'f' color: "006699"}
    ColorCell {shift: 'f' color: "009966"}
    ColorCell {shift: 'f' color: "33cc99"}
    ColorCell {shift: 'f' color: "66ffcc" legend: 'B'}
    ColorCell {shift: 'f' color: "33ff99" legend: 'B'}
    ColorCell {shift: 't' color: "66ff99" legend: 'B'}
    ColorCell {shift: 't' color: "33cc66"}
    ColorCell {shift: 't' color: "009933"}
    ColorCell {shift: 't' color: "339900"}
    ColorCell {shift: 't' color: "66cc33" legend: 'B'}
    ColorCell {shift: 't' color: "99ff66" legend: 'B'}
    ColorCell {shift: 't' color: "99ff33" legend: 'B'}
    ColorCell {shift: 'y' color: "ccff66" legend: 'B'}
    ColorCell {shift: 'y' color: "99cc33" legend: 'B'}
    ColorCell {shift: 'y' color: "669900"}
    ColorCell {shift: 'y' color: "996600"}
    ColorCell {shift: 'y' color: "cc9933" legend: 'B'}
    ColorCell {shift: 'y' color: "ffcc66" legend: 'B'}
    ColorCell {shift: 'y' color: "ff9933" legend: 'B'}
    ColorCell {shift: 'h' color: "ff9966" legend: 'B'}
    ColorCell {shift: 'h' color: "cc6633"}
    ColorCell {shift: 'h' color: "993300"}
    ColorCell {shift: 'h' color: "990033"}
    ColorCell {shift: 'h' color: "cc3366"}
    ColorCell {shift: 'h' color: "ff6699" legend: 'B'}
    ColorCell {shift: 'h' color: "ff3399"}
    ColorCell {shift: 'h' color: "ff00cc"}
    ColorCell {shift: 'b' color: "ff33cc"}
    ColorCell {shift: 'b' color: "cc0099"}
    ColorCell {shift: 'b' color: "9900cc"}
    ColorCell {shift: 'b' color: "cc33ff"}
    ColorCell {shift: 'b' color: "cc00ff"}
    ColorCell {shift: 'b' color: "9900ff"}
    ColorCell {shift: 'b' color: "6600ff"}
    ColorCell {shift: 'v' color: "3300ff"}
    ColorCell {shift: 'v' color: "6633ff"}
    ColorCell {shift: 'v' color: "3300cc"}
    ColorCell {shift: 'v' color: "0033cc"}
    ColorCell {shift: 'v' color: "3366ff"}
    ColorCell {shift: 'v' color: "0033ff"}
    ColorCell {shift: 'v' color: "0066ff"}
    ColorCell {shift: 'v' color: "0099ff"}
    ColorCell {shift: 'f' color: "00ccff" legend: 'B'}
    ColorCell {shift: 'f' color: "33ccff" legend: 'B'}
    ColorCell {shift: 'f' color: "0099cc"}
    ColorCell {shift: 'f' color: "00cc99"}
    ColorCell {shift: 'f' color: "33ffcc" legend: 'B'}
    ColorCell {shift: 'f' color: "00ffcc" legend: 'B'}
    ColorCell {shift: 'f' color: "00ff99" legend: 'B'}
    ColorCell {shift: 'f' color: "00ff66" legend: 'B'}
    ColorCell {shift: 't' color: "00ff33" legend: 'B'}
    ColorCell {shift: 't' color: "33ff66" legend: 'B'}
    ColorCell {shift: 't' color: "00cc33"}
    ColorCell {shift: 't' color: "33cc00"}
    ColorCell {shift: 't' color: "66ff33" legend: 'B'}
    ColorCell {shift: 't' color: "33ff00" legend: 'B'}
    ColorCell {shift: 't' color: "66ff00" legend: 'B'}
    ColorCell {shift: 't' color: "99ff00" legend: 'B'}
    ColorCell {shift: 'y' color: "ccff00" legend: 'B'}
    ColorCell {shift: 'y' color: "ccff33" legend: 'B'}
    ColorCell {shift: 'y' color: "99cc00" legend: 'B'}
    ColorCell {shift: 'y' color: "cc9900" legend: 'B'}
    ColorCell {shift: 'y' color: "ffcc33" legend: 'B'}
    ColorCell {shift: 'y' color: "ffcc00" legend: 'B'}
    ColorCell {shift: 'y' color: "ff9900" legend: 'B'}
    ColorCell {shift: 'y' color: "ff6600"}
    ColorCell {shift: 'h' color: "ff3300"}
    ColorCell {shift: 'h' color: "ff6633"}
    ColorCell {shift: 'h' color: "cc3300"}
    ColorCell {shift: 'h' color: "cc0033"}
    ColorCell {shift: 'h' color: "ff3366"}
    ColorCell {shift: 'h' color: "ff0033"}
    ColorCell {shift: 'h' color: "ff0066"}
    ColorCell {shift: 'h' color: "ff0099"}
];
//</editor-fold>