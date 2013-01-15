package cua.li.ti

import cua.li.ti.scene.gadget.HexagonalColorPicker
import cua.li.ti.scene.layout.DockPane
import cua.li.ti.scene.layout.ShiftingStackPane

import java.lang.Math

import javafx.scene.{ paint => jfxsp }

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.DoubleProperty
import scalafx.beans.property.ObjectProperty
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.effect.PerspectiveTransform
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox
import scalafx.scene.layout.StackPane
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
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
  stage = new Stage {
    title = "Demo"
    scene = new Scene(3 * RADIUS, 720) {
      content = new VBox {
        val dockHeight = DoubleProperty(100)
        val dockWidth = DoubleProperty(600)
        content = Seq(
          new StackPane {
            content = Seq(
              new ShiftingStackPane(5, 5) {
                content = Seq(
                  new Rectangle { width = 300; height = 100; fill = Color.DARKORANGE },
                  new Rectangle { width = 300; height = 100; fill = Color.DARKTURQUOISE },
                  new Rectangle { width = 300; height = 100; fill = Color.DARKOLIVEGREEN }
                )
              },
              new Button {
                text = "Show Hexagonal Color Picker"
                onMouseClicked = (me :MouseEvent) => {
                  PICKER.radius = RADIUS
                  PICKER.original = Color.BLACK
                  PICKER.managed = !PICKER.managed.get
                  PICKER.visible = !PICKER.visible.get
                }
              },
              PICKER
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
            content add newRectangle(Color.DARKOLIVEGREEN)
            content add newRectangle(Color.DARKMAGENTA)
            content add newRectangle(Color.DARKORANGE)
            content add newRectangle(Color.DARKTURQUOISE)
            content add newRectangle(Color.DARKOLIVEGREEN)
            content add newRectangle(Color.DARKMAGENTA)
            content add newRectangle(Color.DARKORANGE)
            content add newRectangle(Color.DARKTURQUOISE)
            center = 5
          }
        )
      }
      fill = Color.BLANCHEDALMOND
    }
  }
  private def newRectangle(color :Color) :Rectangle = {
    new Rectangle { 
            val paint = ObjectProperty[jfxsp.Color](color)
            width = 150; height = 100; fill <== paint
            onMouseClicked = (me :MouseEvent) => {paint() = paint().brighter}
    }
  }
}
