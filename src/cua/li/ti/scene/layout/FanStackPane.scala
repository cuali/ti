package cua.li.ti.scene.layout

import javafx.util.Duration

import scalafx.Includes._
import scalafx.animation.Animation.Status
import scalafx.animation.Timeline
import scalafx.beans.property.DoubleProperty
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer.Add
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.StackPane
import scalafx.scene.shape.Shape

/**
 * Layout with animation on mouse enter, placing the children along the border of enclosing ellipse.
 * It does NOT consider the individual node's alignment property, NOR the container's one.
 * @author A@cua.li
 */
class FanStackPane(val preferredHeight :DoubleProperty = DoubleProperty(100),
val preferredWidth :DoubleProperty = DoubleProperty(100)) extends StackPane {
  minWidth <== preferredWidth
  minHeight <== preferredHeight
  val angle = DoubleProperty(-math.Pi / 4)
  val initialDelay = ObjectProperty[Duration](600 ms)
  val duration = ObjectProperty[Duration](3 s)
  val shapes = ObservableBuffer[FanStackPane.FloatingShape]()
  shapes onChange {
    (_, changes) => {
    	theta() = math.toRadians(360 / shapes.size)
        for (change <- changes) {
          change match {
            case Add(_, shapes :Seq[FanStackPane.FloatingShape]) => {
              for (shape <- shapes) {
                shape.parentWidth <== preferredWidth
                shape.parentHeight <== preferredHeight
              }
              reset
            }
          }
        }
      }
  }
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
      animation.playFromStart
    }
  }
}
object FanStackPane {
  trait FloatingShape extends Shape {
    val parentWidth = DoubleProperty(0)
    val parentHeight = DoubleProperty(0)
    val phi = DoubleProperty(0)
    phi onChange {
      (_,_,_) => {
        translateX() = ((parentWidth() - layoutBounds().width) / 2) * (math.cos(phi()))
        translateY() = ((parentHeight() - layoutBounds().height) / 2) * (math.sin(phi()))
      }
    }
  }
}
