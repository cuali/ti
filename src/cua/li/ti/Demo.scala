package cua.li.ti

import cua.li.ti.scene.gadget.HexagonalColorPicker

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
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
      onClose = (color: Color, hasChanged: Boolean) => {
        if (hasChanged) { stage.scene.value.fill = color }
      }
    }
  stage = new Stage {
    title = "Demo"
    scene = new Scene(2 * RADIUS, Math.sqrt(3) * RADIUS) {
      content = new StackPane {
        content = Seq(
          new Button {
            text = "Show Hexagonal Color Picker"
            onMouseClicked = (me: MouseEvent) => {
              PICKER.radius = RADIUS
              PICKER.original = Color.BLACK
              PICKER.managed = ! PICKER.managed.get
              PICKER.visible = ! PICKER.visible.get
            }
          },
          PICKER
          )
      }
      fill = Color.BLANCHEDALMOND
    }
  }
}
