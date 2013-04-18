package cua.li.ti.scene.layout

import javafx.util.Duration
import scalafx.Includes._
import scalafx.animation.Animation.Status
import scalafx.animation.Timeline
import scalafx.beans.property.DoubleProperty
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer.Add
import scalafx.collections.ObservableBuffer.Remove
import scalafx.geometry.BoundingBox
import scalafx.geometry.HPos
import scalafx.geometry.Pos
import scalafx.geometry.VPos
import scalafx.scene.Node
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.StackPane
import scalafx.scene.shape.Shape
import scalafx.geometry.Insets

/**
 * Layout with animation on mouse enter, placing the children along the border of enclosing ellipse.
 * It does NOT consider the individual node's alignment property, NOR the container's one.
 * @author A@cua.li
 */
class FanStackPane(val reference :ObjectProperty[javafx.geometry.Pos] = ObjectProperty(Pos.BOTTOM_LEFT),
    val parentPadding :ObjectProperty[javafx.geometry.Insets] = ObjectProperty(Insets(0,0,0,0)),
    val parentHeight :DoubleProperty = DoubleProperty(100),
    val parentWidth :DoubleProperty = DoubleProperty(100)) extends StackPane {
  val angle = DoubleProperty(-math.Pi / 4)
  val initialDelay = ObjectProperty[Duration](600 ms)
  val duration = ObjectProperty[Duration](3 s)
  val shapes = ObservableBuffer[FanStackPane.FloatingShape]()
  shapes onChange {
    (_, changes) => {
        if (0 < shapes.size) {
          theta() = math.toRadians(360 / shapes.size)
        }
        for (change <- changes) {
          change match {
            case Add(_, shapes :Seq[FanStackPane.FloatingShape]) => {
              for (shape <- shapes) {
                shape.padding <== parentPadding
                shape.parentWidth <== parentWidth
                shape.parentHeight <== parentHeight
                shape.reference <== reference
                shape.parentBounds <== layoutBounds
              }
            }
            case _ => {}
          }
        }
        reset
      }
  }
  minWidth <== parentWidth / 2
  minHeight <== parentHeight / 2
  def reset() = {
    for (shape <- shapes) {
      shape.phi() = angle()
    }
  }
  private val theta = DoubleProperty(0)
  private lazy val animation = new Timeline {
    delay <== initialDelay
    private val initialFrames = for (shape <- shapes) yield at(0 s) {
      shape.phi -> angle()
    }
    private val endFrames = for ((shape, index) <- shapes.zipWithIndex) yield at(duration()) {
      shape.phi -> (angle() + 2 * math.Pi - index * theta())
    }
    keyFrames = initialFrames ++ endFrames
  }
  onMouseExited = (_: MouseEvent) => {
    if (Status.RUNNING == animation.status()) {
      animation.stop
      reset
    }
  }
  onMouseEntered = (_: MouseEvent) => {
    if (Status.STOPPED == animation.status()) {
      toFront
      animation.playFromStart
    }
  }
}
object FanStackPane {
  trait FloatingShape extends Node {
    val padding = ObjectProperty(Insets(0,0,0,0))
    val reference = ObjectProperty(Pos.BOTTOM_LEFT)
    val parentBounds :ObjectProperty[javafx.geometry.Bounds] = ObjectProperty(new BoundingBox(0, 0, 0, 0))
    val parentWidth = DoubleProperty(0)
    val parentHeight = DoubleProperty(0)
    val phi = DoubleProperty(0)
    phi onChange { (_,_,_) => translateNode }
    parentBounds onChange { (_,_,_) => translateNode }
    private def translateNode() = {
      translateX() = computeHorizontalShift + ((parentWidth() - boundsInParent().width) / 2) * (math.cos(phi()))
      translateY() = computeVerticalShift + ((parentHeight() - boundsInParent().height) / 2) * (math.sin(phi()))
    }
    private def computeHorizontalShift() :Double = {
      reference().hpos match {
        case HPos.LEFT => (padding().left - padding().right + parentBounds().width) / 2
        case HPos.RIGHT => (padding().left - padding().right - parentBounds().width) / 2
        case HPos.CENTER => 0
      }
    }
    private def computeVerticalShift() :Double = {
      reference().vpos match {
        case VPos.TOP => (padding().top - padding().bottom + parentBounds().height) / 2
        case VPos.BOTTOM => (padding().top - padding().bottom - parentBounds().height) / 2
        case VPos.CENTER => 0
      }
    }
  }
}
