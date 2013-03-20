package cua.li.ti.scene.gadget

import cua.li.ti.scene.shape.RegularPolygon

import java.lang.IllegalArgumentException
import java.lang.Math

import scalafx.Includes._
import scalafx.beans.property.BooleanProperty
import scalafx.beans.property.DoubleProperty
import scalafx.beans.property.ObjectProperty
import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.beans.property.StringProperty
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.scene.paint.Paint
import scalafx.scene.shape.StrokeLineJoin
import scalafx.scene.text.Font
import scalafx.scene.text.Text
import scalafx.scene.transform.Transform

/**
 * Based on original layout by Bob Stein, Stein@VisiBone.com.
 * @author A@cua.li
 * @see <a href="http://www.VisiBone.com/color/">VisiBone Web Design Color References</a>
 */
class HexagonalColorPicker extends Pane {
  /**
   * The onClose function attribute is executed when the
   * the center or the border is pressed, passing the chosen color
   * or original color, depending upon which was pressed.
   */
  var onClose = (color :Color, hasChanged :Boolean) => {}
  /** The chosen color when calling the <code>onClose</code> function. */
  private var chosen :Color = Color.TRANSPARENT
  /** The original color has to be set <b>before</b> setting the <code>visible</code> attribute to <code>true</code>. */
  var original = Color.TRANSPARENT
  var centerX :Double = 50
  var centerY :Double = 120
  /** The radius may be changed after initialization at the cost of a global recomputation of polygons' vertices. */
  private[this] val radiusProperty = DoubleProperty(100)
  def radius :Double = radiusProperty()
  def radius_=(value :Double) {
    radiusProperty() = value
    cellRadius = value * HexagonalColorPicker.SQRT_3 / 26 // 27 would leave a border equivalent to one complete cell
    colorName = new Text("") {
      font = new Font("Consolas Bold", cellRadius * 0.707)
      wrappingWidth = cellRadius * 3.3
      translateX = cellRadius * 3.3 / -2
      mouseTransparent = true
      visible = false
    }
    horizontalShift = Seq(
      /*f*/ -cellRadius * HexagonalColorPicker.SQRT_3,
      /*t*/ -cellRadius * HexagonalColorPicker.HALF_SQRT_3,
      /*y*/ cellRadius * HexagonalColorPicker.HALF_SQRT_3,
      /*h*/ cellRadius * HexagonalColorPicker.SQRT_3,
      /*b*/ cellRadius * HexagonalColorPicker.HALF_SQRT_3,
      /*v*/ -cellRadius * HexagonalColorPicker.HALF_SQRT_3
    )
    verticalShift = Seq(
      /*f*/ 0,
      /*t*/ -cellRadius * HexagonalColorPicker.HALF_3,
      /*y*/ -cellRadius * HexagonalColorPicker.HALF_3,
      /*h*/ 0,
      /*b*/ cellRadius * HexagonalColorPicker.HALF_3,
      /*v*/ cellRadius * HexagonalColorPicker.HALF_3
    )
    layoutChildren
  }
  /** There are 8 complete cells on each side of the center cell, plus a "sufficient" border. */
  private[this] var cellRadius :Double = 6.66
  private[this] var horizontalShift :Seq[Double] = _
  private[this] var verticalShift :Seq[Double] = _
  private[this] var colorName :Text = _
  /**
   * The application has to set the <code>visible</code> attribute to <code>true</true>
   * <b>after</b> setting the <code>original</code> color.
   */
  visible onChange {
    (_, oldVisible, newVisible) =>
      if (newVisible) {
        this.managed = true
        chosen = original
        this.toFront()
      } else {
        this.managed = false
      }
  }
  visible = false
  onMouseClicked = (me :MouseEvent) => { visible = false }
  private[this] var centralLabel :String = ""
  private[this] var centralPaint :Paint = _
  private[this] var centralHexagon :RegularPolygon = new RegularPolygon
  private[this] var currentHexagon :RegularPolygon = new RegularPolygon
  def computePrefWidth = 2 * radiusProperty()
  def computePrefHeight = HexagonalColorPicker.SQRT_3 * radiusProperty()
  /**
   * Defines the nodes in the <code>children</code> sequence.
   * The current strategy for creating the nodes does not allow for translating after initialization.
   */
  def layoutChildren {
    content = Seq(
      new RegularPolygon {
        centerX = HexagonalColorPicker.this.centerX
        centerY = HexagonalColorPicker.this.centerY
        radius = HexagonalColorPicker.this.radius
        angle = 0
        strokeWidth = cellRadius
        strokeLineJoin = StrokeLineJoin.ROUND
        stroke = Color.BLACK
        fill = Color.TRANSPARENT
        mouseTransparent = false
        onMouseClicked = (me :MouseEvent) => {
          colorName.visible = false
          HexagonalColorPicker.this.managed = false
          HexagonalColorPicker.this.visible = false
          onClose(original, false)
          me.consume
        }
      },
      colorName
    )
    var relPosX = 0.0
    var relPosY = 0.0
    centralHexagon = new RegularPolygon {
      centerX = HexagonalColorPicker.this.centerX + relPosX
      centerY = HexagonalColorPicker.this.centerY + relPosY
      radius = cellRadius
      strokeWidth = 0
      stroke = Color.TRANSPARENT
      mouseTransparent = false
    }
    centralHexagon.onMouseEntered = (me :MouseEvent) => {
      centralHexagon.toFront()
      centralHexagon.transforms += Transform.scale(3, 3, centralHexagon.centerX, centralHexagon.centerY)
      colorName.text = centralLabel
      colorName.fill = centralPaint
      colorName.x = centralHexagon.centerX
      colorName.y = centralHexagon.centerY
      colorName.visible = true
      colorName.toFront()
      me.consume
    }
    centralHexagon.onMouseExited = (me :MouseEvent) => {
      colorName.visible = false
      centralHexagon.transforms.clear
      me.consume
    }
    centralHexagon.onMouseClicked = (me :MouseEvent) => {
      centralLabel = ""
      colorName.visible = false
      HexagonalColorPicker.this.visible = false
      onClose(chosen, !original.equals(chosen))
      me.consume
    }
    currentHexagon = centralHexagon
    content += currentHexagon
    for (cell <- HexagonalColorPicker.CELLS) {
      val index = "ftyhbv".indexOf(cell.shift.toLower)
      if (0 > index) throw new IllegalArgumentException("Unknown position shift " + cell.shift)
      relPosX += horizontalShift(index)
      relPosY += verticalShift(index)
      val hexagon = new RegularPolygon {
        centerX = HexagonalColorPicker.this.centerX + relPosX
        centerY = HexagonalColorPicker.this.centerY + relPosY
        radius = cellRadius
        strokeWidth = 0
        stroke = Color.TRANSPARENT
        val color = Color.web(cell.color)
        fill = color
        mouseTransparent = false
      }
      hexagon.onMouseEntered = (me :MouseEvent) => {
        currentHexagon.transforms.clear
        hexagon.toFront()
        hexagon.transforms += Transform.scale(2, 2, hexagon.centerX, hexagon.centerY)
        currentHexagon = hexagon
        colorName.text = cell.color
        colorName.fill = if ('W' equals cell.legend) { Color.WHITE } else { Color.BLACK }
        colorName.x = hexagon.centerX
        colorName.y = hexagon.centerY
        colorName.visible = true
        colorName.toFront()
        me.consume
      }
      hexagon.onMouseExited = (me :MouseEvent) => {
        colorName.visible = false
        hexagon.transforms.clear
        me.consume
      }
      hexagon.onMouseClicked = (me :MouseEvent) => {
        centralLabel = cell.color
        centralPaint = colorName.fill()
        centralHexagon.fill() = hexagon.color
        chosen = hexagon.color
        me.consume
      }
      content += hexagon
    }
  }
}

object HexagonalColorPicker {
  /**
   * Data Transfer Object describing an hexagonal cell in the resulting hexagonal layout.
   */
  class ColorCell {
    /** The direction this cell stands, relative to the previous one. */
    var shift :Char = 'g' // ftyhbv
    /** The color of the cell, expressed as a Web-color code. */
    var color :String = "#000000"
    /** Whether the color code should be written <code>W</code>hite or <code>B</code>lack. */
    var legend :Char = 'W' // 'B' or 'W'
  }

  // working with hexagons mainly involves 3/2, sqrt(3) and sqrt(3)/2
  val SQRT_3 = Math.sqrt(3)
  val HALF_SQRT_3 = SQRT_3 / 2
  val HALF_3 = 1.5 // 3.0 / 2.0

  // <editor-fold defaultstate="collapsed" desc="carefully ordered sequence of 216 color cells">
  val CELLS :Seq[ColorCell] = Seq(
    new ColorCell { shift = 'h'; color = "#000000" },
    new ColorCell { shift = 'v'; color = "#333333" },
    new ColorCell { shift = 'f'; color = "#666666" },
    new ColorCell { shift = 't'; color = "#999999"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#cccccc"; legend = 'B' },
    new ColorCell { shift = 'h'; color = "#ffffff"; legend = 'B' },
    new ColorCell { shift = 'h'; color = "#ffccff"; legend = 'B' },
    new ColorCell { shift = 'b'; color = "#cc99ff"; legend = 'B' },
    new ColorCell { shift = 'v'; color = "#ccccff"; legend = 'B' },
    new ColorCell { shift = 'v'; color = "#99ccff"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#ccffff"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#99ffcc"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#ccffcc"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#ccff99"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ffffcc"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ffcc99"; legend = 'B' },
    new ColorCell { shift = 'h'; color = "#ffcccc"; legend = 'B' },
    new ColorCell { shift = 'h'; color = "#ff99cc"; legend = 'B' },
    new ColorCell { shift = 'h'; color = "#cc99cc"; legend = 'B' },
    new ColorCell { shift = 'b'; color = "#ff99ff"; legend = 'B' },
    new ColorCell { shift = 'b'; color = "#9966cc" },
    new ColorCell { shift = 'v'; color = "#9999cc"; legend = 'B' },
    new ColorCell { shift = 'v'; color = "#9999ff"; legend = 'B' },
    new ColorCell { shift = 'v'; color = "#6699cc" },
    new ColorCell { shift = 'f'; color = "#99cccc"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#99ffff"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#66cc99"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#99cc99"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#99ff99"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#99cc66"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#cccc99"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ffff99"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#cc9966"; legend = 'B' },
    new ColorCell { shift = 'h'; color = "#cc9999"; legend = 'B' },
    new ColorCell { shift = 'h'; color = "#ff9999"; legend = 'B' },
    new ColorCell { shift = 'h'; color = "#cc6699" },
    new ColorCell { shift = 'h'; color = "#996699" },
    new ColorCell { shift = 'b'; color = "#cc66cc" },
    new ColorCell { shift = 'b'; color = "#ff66ff"; legend = 'B' },
    new ColorCell { shift = 'b'; color = "#663399" },
    new ColorCell { shift = 'v'; color = "#666699" },
    new ColorCell { shift = 'v'; color = "#6666cc" },
    new ColorCell { shift = 'v'; color = "#6666ff" },
    new ColorCell { shift = 'v'; color = "#336699" },
    new ColorCell { shift = 'f'; color = "#669999" },
    new ColorCell { shift = 'f'; color = "#66cccc"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#66ffff"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#339966" },
    new ColorCell { shift = 't'; color = "#669966" },
    new ColorCell { shift = 't'; color = "#66cc66"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#66ff66"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#669933" },
    new ColorCell { shift = 'y'; color = "#999966" },
    new ColorCell { shift = 'y'; color = "#cccc66"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ffff66"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#996633" },
    new ColorCell { shift = 'h'; color = "#996666" },
    new ColorCell { shift = 'h'; color = "#cc6666" },
    new ColorCell { shift = 'h'; color = "#ff6666" },
    new ColorCell { shift = 'h'; color = "#993366" },
    new ColorCell { shift = 'h'; color = "#663366" },
    new ColorCell { shift = 'b'; color = "#993399" },
    new ColorCell { shift = 'b'; color = "#cc33cc" },
    new ColorCell { shift = 'b'; color = "#ff33ff" },
    new ColorCell { shift = 'b'; color = "#330066" },
    new ColorCell { shift = 'v'; color = "#333366" },
    new ColorCell { shift = 'v'; color = "#333399" },
    new ColorCell { shift = 'v'; color = "#3333cc" },
    new ColorCell { shift = 'v'; color = "#3333ff" },
    new ColorCell { shift = 'v'; color = "#003366" },
    new ColorCell { shift = 'f'; color = "#336666" },
    new ColorCell { shift = 'f'; color = "#339999" },
    new ColorCell { shift = 'f'; color = "#33cccc"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#33ffff"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#006633" },
    new ColorCell { shift = 't'; color = "#336633" },
    new ColorCell { shift = 't'; color = "#339933" },
    new ColorCell { shift = 't'; color = "#33cc33" },
    new ColorCell { shift = 't'; color = "#33ff33"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#336600" },
    new ColorCell { shift = 'y'; color = "#666633" },
    new ColorCell { shift = 'y'; color = "#999933" },
    new ColorCell { shift = 'y'; color = "#cccc33"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ffff33"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#663300" },
    new ColorCell { shift = 'h'; color = "#663333" },
    new ColorCell { shift = 'h'; color = "#993333" },
    new ColorCell { shift = 'h'; color = "#cc3333" },
    new ColorCell { shift = 'h'; color = "#ff3333" },
    new ColorCell { shift = 'h'; color = "#660033" },
    new ColorCell { shift = 'h'; color = "#330033" },
    new ColorCell { shift = 'b'; color = "#660066" },
    new ColorCell { shift = 'b'; color = "#990099" },
    new ColorCell { shift = 'b'; color = "#cc00cc" },
    new ColorCell { shift = 'b'; color = "#ff00ff" },
    new ColorCell { shift = 'b'; color = "#6600cc" },
    new ColorCell { shift = 'v'; color = "#000033" },
    new ColorCell { shift = 'v'; color = "#000066" },
    new ColorCell { shift = 'v'; color = "#000099" },
    new ColorCell { shift = 'v'; color = "#0000cc" },
    new ColorCell { shift = 'v'; color = "#0000ff" },
    new ColorCell { shift = 'v'; color = "#0066cc" },
    new ColorCell { shift = 'f'; color = "#003333" },
    new ColorCell { shift = 'f'; color = "#006666" },
    new ColorCell { shift = 'f'; color = "#009999" },
    new ColorCell { shift = 'f'; color = "#00cccc" },
    new ColorCell { shift = 'f'; color = "#00ffff"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#00cc66" },
    new ColorCell { shift = 't'; color = "#003300" },
    new ColorCell { shift = 't'; color = "#006600" },
    new ColorCell { shift = 't'; color = "#009900" },
    new ColorCell { shift = 't'; color = "#00cc00" },
    new ColorCell { shift = 't'; color = "#00ff00"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#66cc00" },
    new ColorCell { shift = 'y'; color = "#333300" },
    new ColorCell { shift = 'y'; color = "#666600" },
    new ColorCell { shift = 'y'; color = "#999900" },
    new ColorCell { shift = 'y'; color = "#cccc00"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ffff00"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#cc6600" },
    new ColorCell { shift = 'h'; color = "#330000" },
    new ColorCell { shift = 'h'; color = "#660000" },
    new ColorCell { shift = 'h'; color = "#990000" },
    new ColorCell { shift = 'h'; color = "#cc0000" },
    new ColorCell { shift = 'h'; color = "#ff0000" },
    new ColorCell { shift = 'h'; color = "#cc0066" },
    new ColorCell { shift = 'h'; color = "#ff66cc"; legend = 'B' },
    new ColorCell { shift = 'b'; color = "#cc3399" },
    new ColorCell { shift = 'b'; color = "#990066" },
    new ColorCell { shift = 'b'; color = "#660099" },
    new ColorCell { shift = 'b'; color = "#9933cc" },
    new ColorCell { shift = 'b'; color = "#cc66ff"; legend = 'B' },
    new ColorCell { shift = 'b'; color = "#9933ff" },
    new ColorCell { shift = 'v'; color = "#9966ff" },
    new ColorCell { shift = 'v'; color = "#6633cc" },
    new ColorCell { shift = 'v'; color = "#330099" },
    new ColorCell { shift = 'v'; color = "#003399" },
    new ColorCell { shift = 'v'; color = "#3366cc" },
    new ColorCell { shift = 'v'; color = "#6699ff"; legend = 'B' },
    new ColorCell { shift = 'v'; color = "#3399ff" },
    new ColorCell { shift = 'f'; color = "#66ccff"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#3399cc" },
    new ColorCell { shift = 'f'; color = "#006699" },
    new ColorCell { shift = 'f'; color = "#009966" },
    new ColorCell { shift = 'f'; color = "#33cc99" },
    new ColorCell { shift = 'f'; color = "#66ffcc"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#33ff99"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#66ff99"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#33cc66" },
    new ColorCell { shift = 't'; color = "#009933" },
    new ColorCell { shift = 't'; color = "#339900" },
    new ColorCell { shift = 't'; color = "#66cc33"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#99ff66"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#99ff33"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ccff66"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#99cc33"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#669900" },
    new ColorCell { shift = 'y'; color = "#996600" },
    new ColorCell { shift = 'y'; color = "#cc9933"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ffcc66"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ff9933"; legend = 'B' },
    new ColorCell { shift = 'h'; color = "#ff9966"; legend = 'B' },
    new ColorCell { shift = 'h'; color = "#cc6633" },
    new ColorCell { shift = 'h'; color = "#993300" },
    new ColorCell { shift = 'h'; color = "#990033" },
    new ColorCell { shift = 'h'; color = "#cc3366" },
    new ColorCell { shift = 'h'; color = "#ff6699"; legend = 'B' },
    new ColorCell { shift = 'h'; color = "#ff3399" },
    new ColorCell { shift = 'h'; color = "#ff00cc" },
    new ColorCell { shift = 'b'; color = "#ff33cc" },
    new ColorCell { shift = 'b'; color = "#cc0099" },
    new ColorCell { shift = 'b'; color = "#9900cc" },
    new ColorCell { shift = 'b'; color = "#cc33ff" },
    new ColorCell { shift = 'b'; color = "#cc00ff" },
    new ColorCell { shift = 'b'; color = "#9900ff" },
    new ColorCell { shift = 'b'; color = "#6600ff" },
    new ColorCell { shift = 'v'; color = "#3300ff" },
    new ColorCell { shift = 'v'; color = "#6633ff" },
    new ColorCell { shift = 'v'; color = "#3300cc" },
    new ColorCell { shift = 'v'; color = "#0033cc" },
    new ColorCell { shift = 'v'; color = "#3366ff" },
    new ColorCell { shift = 'v'; color = "#0033ff" },
    new ColorCell { shift = 'v'; color = "#0066ff" },
    new ColorCell { shift = 'v'; color = "#0099ff" },
    new ColorCell { shift = 'f'; color = "#00ccff"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#33ccff"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#0099cc" },
    new ColorCell { shift = 'f'; color = "#00cc99" },
    new ColorCell { shift = 'f'; color = "#33ffcc"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#00ffcc"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#00ff99"; legend = 'B' },
    new ColorCell { shift = 'f'; color = "#00ff66"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#00ff33"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#33ff66"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#00cc33" },
    new ColorCell { shift = 't'; color = "#33cc00" },
    new ColorCell { shift = 't'; color = "#66ff33"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#33ff00"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#66ff00"; legend = 'B' },
    new ColorCell { shift = 't'; color = "#99ff00"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ccff00"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ccff33"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#99cc00"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#cc9900"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ffcc33"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ffcc00"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ff9900"; legend = 'B' },
    new ColorCell { shift = 'y'; color = "#ff6600" },
    new ColorCell { shift = 'h'; color = "#ff3300" },
    new ColorCell { shift = 'h'; color = "#ff6633" },
    new ColorCell { shift = 'h'; color = "#cc3300" },
    new ColorCell { shift = 'h'; color = "#cc0033" },
    new ColorCell { shift = 'h'; color = "#ff3366" },
    new ColorCell { shift = 'h'; color = "#ff0033" },
    new ColorCell { shift = 'h'; color = "#ff0066" },
    new ColorCell { shift = 'h'; color = "#ff0099" }
  )
  // </editor-fold>
}
