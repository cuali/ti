package cua.li.ti.scene.layout

import javafx.collections.ListChangeListener
import javafx.beans.{ property => jfxbp }
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.{ scene => jfxs }
import jfxs.{ layout => jfxsl }

import scalafx.Includes._
import scalafx.beans.property.DoubleProperty
import scalafx.scene.Node
import scalafx.scene.layout.StackPane

/**
 * It does NOT honor the <code>nodeHPos</code> and <code>nodeVPos</code> attributes.
 * @author A@cua.li
 */

class FanStackPane(override val delegate :FanStackPane.ExtendedStackPane) extends StackPane(delegate) {
  def this(preferredHeight :DoubleProperty = DoubleProperty(100), preferredWidth :DoubleProperty = DoubleProperty(100))
  		= this(new FanStackPane.ExtendedStackPane(preferredHeight, preferredWidth))
}
object FanStackPane {
  private[layout] class ExtendedStackPane(preferredHeight :jfxbp.DoubleProperty, preferredWidth :jfxbp.DoubleProperty) extends jfxsl.StackPane {
    var manager :FanStackPane = _
    override def computePrefHeight(width :Double) = preferredHeight.get
    override def computePrefWidth(height :Double) = preferredWidth.get
    override def layoutChildren() = {
      val sizeOfContent = super.getManagedChildren.size
      if (2 < sizeOfContent) {
        val theta = math.toRadians(360/sizeOfContent)
        val halfPi = - math.Pi / 2
    	val radius = preferredHeight.get / 2
    	val managedContent = super.getManagedChildren.iterator
    	var nodeShift = sizeOfContent - 1
    	while (managedContent.hasNext) {
     	  val node = managedContent.next.asInstanceOf[jfxs.Node]
     	  val phi = halfPi + nodeShift * theta
     	  val posX = (radius - node.prefWidth(preferredHeight.get) / 2) * (1 + math.cos(phi))
     	  val posY = (radius - node.prefHeight(preferredWidth.get) / 2) * (1 + math.sin(phi))
     	  layoutInArea(node, posX, posY,
          node.prefWidth(preferredHeight.get), node.prefHeight(preferredWidth.get), 0, HPos.CENTER, VPos.CENTER )
          nodeShift -= 1
        }
      }
    }
  }
}
