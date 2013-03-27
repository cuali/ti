package cua.li.ti

import cua.li.ti.scene.gadget.HexagonalColorPicker
import cua.li.ti.scene.layout.DockPane
import cua.li.ti.scene.layout.EllipticalStackPane
import cua.li.ti.scene.layout.FanStackPane
import cua.li.ti.scene.layout.ShiftingStackPane

import java.lang.Math
import scala.collection.mutable.Seq

import scalafx.Includes._
import scalafx.animation.Animation.Status
import scalafx.animation.Timeline
import scalafx.application.JFXApp
import scalafx.beans.property.DoubleProperty
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.effect.PerspectiveTransform
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox
import scalafx.scene.layout.StackPane
import scalafx.scene.layout.TilePane
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.shape.Rectangle
import scalafx.scene.shape.Shape
import scalafx.scene.transform.Translate
import scalafx.stage.Stage

/**
 * @author A@cua.li
 */
object Demo extends JFXApp {
  val RADIUS = 230
  val PICKER =
    new HexagonalColorPicker {
      centerX = RADIUS
      centerY = Math.sqrt(3) * RADIUS / 2
      onClose = (color :Color, hasChanged :Boolean) => {
        if (hasChanged) { stage.scene().fill = color }
      }
    }
  stage = new JFXApp.PrimaryStage {
    width = 800
    title = "Demo"
    scene = new Scene(3 * RADIUS, 800) {
      content = new VBox {
        val dockHeight = DoubleProperty(100)
        val dockWidth = DoubleProperty(800)
        val fanHeightWidth = DoubleProperty(400)
        val shift = DoubleProperty(5)
        content = Seq(
          new StackPane {
            content = Seq(
              new ShiftingStackPane(shift, shift) {
                content = Seq(
                  new Rectangle { width = 300; height = 100; fill = Color.DARKORANGE },
                  new Rectangle { width = 300; height = 100; fill = Color.DARKTURQUOISE },
                  new Rectangle { width = 300; height = 100; fill = Color.DARKOLIVEGREEN }
                )
                onMouseEntered = (me :MouseEvent) => { shift() = 20 }
                onMouseExited = (me :MouseEvent) => { shift() = 5 }
              },
              new Button {
                text = "Show Hexagonal Color Picker"
                onMouseClicked = (me :MouseEvent) => {
                  PICKER.radius = RADIUS
                  PICKER.original = Color.BLACK
                  PICKER.managed = !PICKER.managed()
                  PICKER.visible = !PICKER.visible()
                }
              },
              PICKER
            )
          },
          new HBox {
            content = Seq(
              new EllipticalStackPane(fanHeightWidth, fanHeightWidth) {
                content = Seq(
                  new Rectangle { width = 100; height = 50; fill = Color.DARKORANGE },
                  new Rectangle { width = 80; height = 75; fill = Color.DARKOLIVEGREEN },
                  new Rectangle { width = 120; height = 150; fill = Color.DARKTURQUOISE },
                  new Rectangle { width = 90; height = 120; fill = Color.DARKOLIVEGREEN },
                  new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE },
                  new Rectangle { width = 176; height = 106; fill = Color.DARKMAGENTA }
                )
              },
              new StackPane { 
                minWidth <== fanHeightWidth
                minHeight <== fanHeightWidth
                var shapes = for (i <- 1 to 9) yield new Rectangle with FloatingShape {
                  edge <== fanHeightWidth
                  width = 176; height = 106; fill = Color.DARKMAGENTA
                }
                content = shapes
                val originAngle = - math.Pi / 4
                val theta = math.toRadians(360 / content.size)
                val animation = new Timeline {
                  delay = 600 ms
                  var index = 0
                  keyFrames = for (shape <- shapes) yield at(3 s) {
                    index += 1
                    shape.phi() = originAngle
                    shape.phi -> (originAngle + 2 * math.Pi - index * theta)
                  }
                }
                onMouseExited = (_: MouseEvent) => {
                  animation.stop
                  for (shape <- shapes) {
                    shape.phi() = originAngle
                  }
                }
                onMouseEntered = (_: MouseEvent) => {
	              if (Status.STOPPED == animation.status()) {
	                animation.playFromStart
	              }
	            }
                trait FloatingShape extends Shape {
                  val edge = DoubleProperty(400)
                  val phi = DoubleProperty(0)
                  phi onChange {
                    translateX() = ((edge() - layoutBounds().width) / 2) * (math.cos(phi()))
                    translateY() = ((edge() - layoutBounds().height) / 2) * (math.sin(phi()))
                  }
                }
              }
            )
          },
          new TilePane {
            maxWidth <== fanHeightWidth
            maxHeight <== fanHeightWidth
            prefRows = 2
            prefColumns = 2
            content = Seq(
              new FanStackPane(ObjectProperty(Pos.TOP_LEFT), fanHeightWidth, fanHeightWidth) {
                minWidth <== fanHeightWidth / 2
                minHeight <== fanHeightWidth / 2
  				angle() = -3 * math.Pi / 4; duration() = 6 s; initialDelay() = 900 ms
                val circles = for (i <- 1 to 8) yield new Circle with FanStackPane.FloatingShape {
                  centerX = 20 * i; centerY = 20 * i; radius = 5 * i
                  fill = Color.DARKMAGENTA
                }
                shapes ++= circles
                content = shapes
              },
              new FanStackPane(ObjectProperty(Pos.TOP_RIGHT), fanHeightWidth, fanHeightWidth) {
                minWidth <== fanHeightWidth / 2
                minHeight <== fanHeightWidth / 2
  				angle() = -math.Pi / 4; duration() = 6 s; initialDelay() = 900 ms
                val circles = for (i <- 1 to 8) yield new Circle with FanStackPane.FloatingShape {
                  centerX = 20 * i; centerY = 20 * i; radius = 5 * i
                  fill = Color.DARKTURQUOISE
                }
                shapes ++= circles
                content = shapes
              },
              new FanStackPane(ObjectProperty(Pos.BOTTOM_LEFT), fanHeightWidth, fanHeightWidth) {
                minWidth <== fanHeightWidth / 2
                minHeight <== fanHeightWidth / 2
  				angle() = 3 * math.Pi / 4; duration() = 6 s; initialDelay() = 900 ms
                val circles = for (i <- 1 to 8) yield new Circle with FanStackPane.FloatingShape {
                  centerX = 20 * i; centerY = 20 * i; radius = 5 * i
                  fill = Color.DARKORANGE
                }
                shapes ++= circles
                content = shapes
              },
              new FanStackPane(ObjectProperty(Pos.BOTTOM_RIGHT), fanHeightWidth, fanHeightWidth) {
                minWidth <== fanHeightWidth / 2
                minHeight <== fanHeightWidth / 2
  				angle() = math.Pi / 4; duration() = 6 s; initialDelay() = 900 ms
                val circles = for (i <- 1 to 8) yield new Circle with FanStackPane.FloatingShape {
                  centerX = 20 * i; centerY = 20 * i; radius = 5 * i
                  fill = Color.DARKOLIVEGREEN
                }
                shapes ++= circles
                content = shapes
              }
            )
          },
          new DockPane(dockHeight, dockWidth) {
            content = Seq(
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE),
              newRectangle(Color.DARKTURQUOISE),
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE),
              newRectangle(Color.DARKTURQUOISE)
            )
            center = 0
          },
          new DockPane(dockHeight, dockWidth) {
            content = Seq(
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE),
              newRectangle(Color.DARKTURQUOISE),
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE),
              newRectangle(Color.DARKTURQUOISE)
            )
            center = 1
          },
          new DockPane(dockHeight, dockWidth) {
            content = Seq(
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE),
              newRectangle(Color.DARKTURQUOISE),
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE),
              newRectangle(Color.DARKTURQUOISE)
            )
            center = 2
          },
          new DockPane(dockHeight, dockWidth) {
            content = Seq(
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE),
              newRectangle(Color.DARKTURQUOISE),
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE),
              newRectangle(Color.DARKTURQUOISE)
            )
            center = 3
          },
          new DockPane(dockHeight, dockWidth) {
            content = Seq(
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE),
              newRectangle(Color.DARKTURQUOISE),
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE),
              newRectangle(Color.DARKTURQUOISE)
            )
            center = 4
          },
          new DockPane(dockHeight, dockWidth) {
            content = Seq(
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE),
              newRectangle(Color.DARKTURQUOISE),
              newRectangle(Color.DARKOLIVEGREEN),
              newRectangle(Color.DARKMAGENTA),
              newRectangle(Color.DARKORANGE)
    		)
            center = 5
          }
        )
      }
      fill = Color.BLANCHEDALMOND
    }
  }
  private def newRectangle(color :Color) :Rectangle = {
    new Rectangle { 
            val paint = ObjectProperty(color)
            width = 150; height = 100; fill <== paint
            onMouseClicked = (me :MouseEvent) => {paint() = paint().brighter}
    }
  }
  //com.javafx.experiments.scenicview.ScenicView.show(stage.scene())
}
