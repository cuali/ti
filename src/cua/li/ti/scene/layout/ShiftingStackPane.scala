package cua.li.ti.scene.layout

import javafx.collections.ListChangeListener
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.{ scene => jfxs }
import jfxs.{ layout => jfxsl }

import scalafx.Includes._
import scalafx.scene.Node
import scalafx.scene.layout.StackPane

/**
 * In its current implementation this class assumes all content nodes are the same size.
 * It does NOT honor the <code>nodeHPos</code> and <code>nodeVPos</code> attributes.
 * @author A@cua.li
 */

class ShiftingStackPane(override val delegate :jfxsl.StackPane) extends StackPane(delegate) {
  def this(shiftX :Double = 10, shiftY :Double = 10) = this(new ShiftingStackPane.ExtendedStackPane(shiftX, shiftY))
}
object ShiftingStackPane {
  private[layout] class ExtendedStackPane(shiftX :Double, shiftY :Double) extends jfxsl.StackPane {
    private[this] var preferredHeight :Double = 0
    private[this] var dirtyPrefHeight = true
    override def computePrefHeight(width :Double) = {
      if (dirtyPrefHeight) {
        dirtyPrefHeight = false
        val sizeOfContent = (super.getManagedChildren().size) - 1
        preferredHeight = super.computePrefHeight(width) + shiftY * sizeOfContent
      }
      preferredHeight
    }
    private[this] var preferredWidth :Double = 0
    private[this] var dirtyPrefWidth = true
    override def computePrefWidth(height :Double) = {
      if (dirtyPrefWidth) {
        dirtyPrefWidth = false
        val sizeOfContent = (super.getManagedChildren().size) - 1
        preferredWidth = super.computePrefWidth(height) + shiftX * sizeOfContent
      }
      preferredWidth
    }
    override def layoutChildren() = {
      val sizeOfContent = super.getManagedChildren().size
      if (0 < sizeOfContent) {
        val managedContent = super.getManagedChildren().iterator()
        var nodeShift = sizeOfContent - 1
        while (managedContent.hasNext) {
          layoutInArea(managedContent.next, shiftX * nodeShift, shiftY * nodeShift,
            preferredWidth, preferredHeight, 0, HPos.LEFT, VPos.TOP)
          nodeShift -= 1
        }
      }
    }
    private[this] val contentChangeListener = new ListChangeListener[jfxs.Node] {
      override def onChanged(change :ListChangeListener.Change[_ <: jfxs.Node]) = {
        dirtyPrefHeight = true
        dirtyPrefWidth = true
      }
    }
    getChildren.addListener(contentChangeListener)
  }
}
