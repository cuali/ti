package cua.li.ti

import cua.li.ti.scene.gadget.HexagonalColorPicker
import cua.li.ti.scene.layout.DockPane
import cua.li.ti.scene.layout.ShiftingStackPane
import javafx.scene.{ transform => jfxst }
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.stage.Stage
import scalafx.scene.layout.HBox
import scalafx.scene.layout.VBox
import scalafx.scene.effect.PerspectiveTransform
import scalafx.scene.transform.Translate
import scalafx.beans.property.DoubleProperty

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
    scene = new Scene(2 * RADIUS, Math.sqrt(3) * RADIUS) {
      content = new VBox {
        val dockHeight = DoubleProperty(100)
        val dockWidth = DoubleProperty(400)
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
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE }
            )
            center = 0
          },
          new DockPane(dockHeight, dockWidth) {
            content = Seq(
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE }
            )
            center = 1
          },
          new DockPane(dockHeight, dockWidth) {
            content = Seq(
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE }
            )
            center = 2
          },
          new DockPane(dockHeight, dockWidth) {
            content = Seq(
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE }
            )
            center = 3
          },
          new DockPane(dockHeight, dockWidth) {
            content = Seq(
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE }
            )
            center = 4
          },
          new DockPane(dockHeight, dockWidth) {
            content = Seq(
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKOLIVEGREEN },
              new Rectangle { width = 150; height = 100; fill = Color.DARKMAGENTA },
              new Rectangle { width = 150; height = 100; fill = Color.DARKORANGE },
              new Rectangle { width = 150; height = 100; fill = Color.DARKTURQUOISE }
            )
            center = 5
          }
        )
      }
      fill = Color.BLANCHEDALMOND
    }
  }
}
