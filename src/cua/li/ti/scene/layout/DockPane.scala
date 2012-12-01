package cua.li.ti.scene.layout

import java.lang.Math
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.{ scene => jfxs }
import jfxs.{ layout => jfxsl }
import scalafx.Includes._
import scalafx.beans.binding.NumberBinding
import scalafx.beans.property.DoubleProperty
import scalafx.beans.property.IntegerProperty
import scalafx.beans.property.ObjectProperty
import scalafx.scene.Node
import scalafx.scene.effect.DropShadow
import scalafx.scene.effect.Effect
import scalafx.scene.effect.PerspectiveTransform
import scalafx.scene.effect.Reflection
import scalafx.scene.layout.HBox
import scalafx.scene.transform.Translate

/**
 * @author A@cua.li
 */
class DockPane(override val delegate :DockPane.ExtendedHBox) extends HBox(delegate) {
  def this(preferredHeight :DoubleProperty, preferredWidth :DoubleProperty) 
      = this(new DockPane.ExtendedHBox(preferredHeight, preferredWidth))
  def sides = delegate.sides()
  def sides_=(v :Int) {
    delegate.sides() = v
  }
  def center = delegate.center()
  def center_=(v :Int) {
    delegate.center() = v
  }
  def focus(node :Node) = delegate.focus(node)
  def indexOf(node :Node) = delegate.indexOf(node)
  def indexOf(sceneX :Double, sceneY :Double) = delegate.indexOf(sceneX, sceneY)
}

object DockPane {
  val SQRT_2 = Math.sqrt(2)
  val HALF_SQRT_2 = SQRT_2 / 2
  val QUARTER_SQRT_2 = SQRT_2 / 4
  val LATERAL = 5 // take care of adding or removing perspectives and translations accordingly
  val SHRINKS = Seq(SQRT_2, QUARTER_SQRT_2, QUARTER_SQRT_2, 0.5)
  class ExtendedHBox(val preferredHeight :DoubleProperty, val preferredWidth :DoubleProperty) extends jfxsl.HBox {
    this.setPrefHeight(preferredHeight())
    this.setPrefWidth(preferredWidth())
    val nodePrefWidth = DoubleProperty(0)
    val nodePrefHeight = DoubleProperty(0)
    var perspectives :Seq[Effect] = Seq(
      new PerspectiveTransform {
        ulx = 0
        uly = 0
        urx <== nodePrefWidth
        ury = 0
        lrx <== nodePrefWidth
        lry <== preferredHeight
        llx = 0
        lly <== preferredHeight
      }
    )
    var translations :Seq[Translate] = Seq(
      new Translate { y = 2; x <== (preferredWidth - nodePrefWidth) / 2 }
    )
    private[this] def addTransform(xShrink :Double, yMinShrink :Double, yMaxShrink :Double, xShift :Double, depth :Int) = {
      val rightTranslate = new Translate { x <== ((preferredWidth + nodePrefWidth * xShift) / 2) + spacingProperty * depth * HALF_SQRT_2 }
      val leftTranslate = new Translate { x <== ((preferredWidth - nodePrefWidth * (2 * xShrink + xShift)) / 2) - spacingProperty * depth * HALF_SQRT_2 }
      translations = leftTranslate +: translations :+ rightTranslate
      val magic = 3 * (1 + depth)
      val leftPerspective = new PerspectiveTransform {
        ulx = 0
        uly <== preferredHeight * (1 - yMaxShrink) / 2
        urx <== nodePrefWidth * xShrink
        ury <== preferredHeight * (1 - yMinShrink) / 2
        lrx <== nodePrefWidth * xShrink
        lry <== preferredHeight * (1.6 + yMinShrink) / 2
        llx = 0
        lly <== preferredHeight * (1.6 + yMaxShrink) / 2
        input = new Reflection { fraction = 0.3; input = new DropShadow { offsetX = -magic; offsetY = 2; radius = magic } }
      }
      val rightPerspective = new PerspectiveTransform {
        urx <== nodePrefWidth * xShrink
        ury <== preferredHeight * (1 - yMaxShrink) / 2
        ulx = 0
        uly <== preferredHeight * (1 - yMinShrink) / 2
        llx = 0
        lly <== preferredHeight * (1.6 + yMinShrink) / 2
        lrx <== nodePrefWidth * xShrink
        lry <== preferredHeight * (1.6 + yMaxShrink) / 2
        input = new Reflection { fraction = 0.3; input = new DropShadow { offsetX = magic; offsetY = 2; radius = magic } }
      }
      perspectives = leftPerspective +: perspectives :+ rightPerspective
    }
    addTransform(SHRINKS(0) / 2, 1, HALF_SQRT_2, 1, 1)
    addTransform(SHRINKS(1) / 2, HALF_SQRT_2, 0.5, 1 + SHRINKS(0), 2)
    addTransform(SHRINKS(2) / 2, 0.5, QUARTER_SQRT_2, 1 + SHRINKS(0) + SHRINKS(1), 3)
    addTransform(SHRINKS(3) / 2, QUARTER_SQRT_2, 0.25, 1 + SHRINKS(0) + SHRINKS(1) + SHRINKS(2), 4)
    addTransform(0.25, 0.25, 0.25, 1 + SHRINKS(0) + SHRINKS(1) + SHRINKS(2) + SHRINKS(3), 5)

    val sides = IntegerProperty(LATERAL)
    sides onChange {
      (_, previousSides, newSides) =>
        {
          if (0 > newSides.intValue) sides() = 0
          if (LATERAL < newSides.intValue) sides() = previousSides.intValue
        }
    }
    getChildren.addListener(
      new javafx.collections.ListChangeListener[jfxs.Node]() {
        override def onChanged(change :javafx.collections.ListChangeListener.Change[_ <: jfxs.Node]) {
          checkCenter(center()) // so that it forces the value to be valid even after dropping some content
          val managedContent = getChildren
          referenceNode = if (0 == managedContent.size) null else managedContent.get(center())
          nodePrefWidth() = if (null == referenceNode) 0 else referenceNode.prefWidth(preferredHeight())
          nodePrefHeight() = if (null == referenceNode) 0 else referenceNode.prefHeight(preferredWidth())
          visibleDirty = true
        }
      }
    )
    private def checkCenter(newCenter :Int) {
      val sizeOfContent = getChildren.size - 1
      if ((0 <= sizeOfContent) && (sizeOfContent < newCenter)) { center() = sizeOfContent }
      if (0 > center()) { center() = 0 }
    }
    private[layout] val center = IntegerProperty(0)
    center onChange {
      (_, previousCenter, newCenter) =>
        {
          checkCenter(newCenter.intValue)
          visibleDirty = true
          requestLayout()
        }
    }
    var referenceNode :jfxs.Node = _
    var visibleDirty = true
    var visibleContent :Seq[Int] = Seq(-1, -2, -1, -2, -1, -2, -1)

    private[this] def updateVisible() = {
      val sizeOfContent = getChildren.size
      visibleContent = for (i <- (center() - LATERAL) to (center() + LATERAL)) yield { if (i < sizeOfContent) { i } else { -1 } }
      val iterator = getManagedChildren.iterator
      while (iterator.hasNext) {
        val node :jfxs.Node = iterator.next
        node.setCache(false)
        node.setEffect(null)
        node.setTranslateX(0)
        node.setVisible(false)
        node.setManaged(false)
        node.setOpacity(1)
      }
      visibleDirty = false
    }

    override def layoutChildren() = {
      if (visibleDirty) {
        updateVisible
      }
      for (position <- (LATERAL + sides()) until LATERAL by -1) {
        prepareNode(position)
      }
      for (position <- (LATERAL - sides()) to LATERAL) {
        prepareNode(position)
      }
    }

    private[this] def prepareNode(position :Int) = {
      var index = visibleContent(position)
      if (0 <= index) {
        val node :jfxs.Node = getChildren.get(index)
        node.setVisible(true)
        node.setCache(true)
        node.setEffect(perspectives(position))
        node.setManaged(true)
        node.setOpacity(1 - (Math.abs(LATERAL - position) / (2.0 + sides())))
        layoutInArea(node, translations(position).x(), translations(position).y(), 
            node.prefWidth(preferredHeight()), node.prefHeight(preferredWidth()), 0, HPos.CENTER, VPos.CENTER)
      }
    }

    override def computePrefWidth(height :Double) :Double = {
      var width = 0.5
      for (side <- 0 until sides()) {
        width += SHRINKS(side)
      }
      2 * width * nodePrefWidth()
    }
    override def computeMinWidth(height :Double) :Double = Math.min(computePrefWidth(height), getMinWidth)
    override def computeMaxWidth(height :Double) :Double = Math.max(computePrefWidth(height), getMaxWidth)

    def focus(node :Node) = {
      val index = indexOf(node)
      if (0 <= index) {
        center() = index
      }
    }

    def indexOf(node :Node) :Int = {
      val managedContent = getChildren
      for (position <- (LATERAL + sides()) to (LATERAL - sides()) by -1) {
        var index = visibleContent(position)
        if (0 <= index) {
          if (node.delegate == managedContent.get(index)) {
            return index
          }
        }
      }
      return -1
    }

    def indexOf(sceneX :Double, sceneY :Double) :Int = {
      val localPoint = sceneToLocal(sceneX, sceneY)
      val bounds = this.boundsInLocalProperty.get;
      if ((localPoint.getX < bounds.getWidth) && (localPoint.getY < bounds.getHeight)) {
        // FIXME calculate the effective index of the managed content at the given point
        val managedContent = getChildren
        var index :Int = -1
        for (position <- (LATERAL + sides()) to (LATERAL - sides()) by -1) {
          index = visibleContent(position)
          if (0 <= index) {
            var node = managedContent.get(index).asInstanceOf[jfxs.Node]
            if (node.boundsInParentProperty.get.contains(localPoint)) {
              return index
            }
          }
        }
        -1
      } else {
        -1 // should reject only when out of the layoutInfo bounds
      }
    }
  }
}
