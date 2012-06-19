package cua.li.ti.scene.layout

import java.{ util => ju }
import javafx.{ scene => jfxs }
import jfxs.{ layout => jfxsl }
import javafx.geometry.HPos
import javafx.geometry.VPos
import scalafx.scene.Node
import scalafx.scene.effect.PerspectiveTransform
import scalafx.scene.layout.HBox
import scalafx.scene.transform.Translate
import scalafx.beans.property.DoubleProperty
import scalafx.beans.property.IntegerProperty
import scalafx.beans.property.ObjectProperty
import scalafx.beans.binding.NumberBinding

/**
 * @author A@cua.li
 */
class DockPane(override val delegate :DockPane.ExtendedHBox) extends HBox(delegate) {
    def this(preferredHeight :DoubleProperty, preferredWidth :DoubleProperty) = this(new DockPane.ExtendedHBox(preferredHeight, preferredWidth))
    def center = delegate.center.value
    def center_=(v :Int) {
      delegate.center.value = v
    }
    mouseTransparent = true
}

object DockPane {
	val SQRT_2 = Math.sqrt(2)
	val HALF_SQRT_2 = SQRT_2/2
	val QUARTER_SQRT_2 = SQRT_2/4
	val LATERAL = 3    // take care of adding or removing perspectives and translations accordingly
    val SHRINKS = Seq(HALF_SQRT_2, QUARTER_SQRT_2, QUARTER_SQRT_2)
	class ExtendedHBox(val preferredHeight :DoubleProperty, val preferredWidth :DoubleProperty) extends jfxsl.HBox {
	    val nodePrefWidth = new DoubleProperty
	    /*
	    val pxl4 = (preferredWidth - nodePrefWidth * (1 + SQRT_2 + HALF_SQRT_2)) / 2
	    val pxl3 = (preferredWidth - nodePrefWidth * (1 + SQRT_2 + QUARTER_SQRT_2)) / 2
	    val pxl2 = (preferredWidth - nodePrefWidth * (1 + SQRT_2)) / 2
	    val pxl1 = (preferredWidth - nodePrefWidth) / 2
	    val pxr1 = (preferredWidth + nodePrefWidth) / 2
	    val pxr2 = (preferredWidth + nodePrefWidth * (1 + SQRT_2)) / 2
	    val pxr3 = (preferredWidth + nodePrefWidth * (1 + SQRT_2 + QUARTER_SQRT_2)) / 2
	    val pxr4 = (preferredWidth + nodePrefWidth * (1 + SQRT_2 + HALF_SQRT_2)) / 2
	    val pyt4 = preferredHeight * (1 - QUARTER_SQRT_2) / 2
	    val pyt3 = preferredHeight * (1 - 0.5) / 2
	    val pyt2 = preferredHeight * (1 - HALF_SQRT_2) / 2
	    val pyb2 = preferredHeight * (1 + HALF_SQRT_2) / 2
	    val pyb3 = preferredHeight * (1 + 0.5) / 2
	    val pyb4 = preferredHeight * (1 + QUARTER_SQRT_2) / 2
	    */
	    var perspectives :Seq[PerspectiveTransform] = Seq(
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
	    
	    var compensation = SQRT_2 - 3
	    var translations :Seq[Translate] = Seq(
	        new Translate { x <== nodePrefWidth * compensation / 2 }
	    )
	    private[this] def addTransform(xShrink :Double, yMinShrink :Double, yMaxShrink :Double, xShift :Double) = {
          val rightTranslate = new Translate { x <== nodePrefWidth * (compensation - xShift) }
          val leftTranslate = new Translate { x <== nodePrefWidth * (compensation + xShift) }
	      translations = leftTranslate +: translations :+ rightTranslate
          val leftPerspective = new PerspectiveTransform {
                ulx <== nodePrefWidth * (1 - xShrink)
                uly <== preferredHeight * (1 - yMaxShrink) / 2
                urx <== nodePrefWidth
                ury <== preferredHeight * (1 - yMinShrink) / 2
                lrx <== nodePrefWidth
                lry <== preferredHeight * (1 + yMinShrink) / 2
                llx <== nodePrefWidth * (1 - xShrink)
                lly <== preferredHeight * (1 + yMaxShrink) / 2
          }
          val rightPerspective = new PerspectiveTransform {
                urx <== nodePrefWidth * xShrink
                ury <== preferredHeight * (1 - yMaxShrink) / 2
                ulx = 0
                uly <== preferredHeight * (1 - yMinShrink) / 2
                llx = 0
                lly <== preferredHeight * (1 + yMinShrink) / 2
                lrx <== nodePrefWidth * xShrink
                lry <== preferredHeight * (1 + yMaxShrink) / 2
          }
	      perspectives = leftPerspective +: perspectives :+ rightPerspective
	    }
        addTransform(SHRINKS(0), 1, HALF_SQRT_2, 0)
        addTransform(SHRINKS(1), HALF_SQRT_2, 0.5, 1 - HALF_SQRT_2)
        addTransform(SHRINKS(2), 0.5, QUARTER_SQRT_2, 2 - HALF_SQRT_2 - QUARTER_SQRT_2)
	
	    val sides = new IntegerProperty { value = LATERAL }
	    sides onChange { 
	        (_, previousSides, newSides) => {
		        if (0 > newSides.intValue) sides.value = 0
		        if (LATERAL < newSides.intValue) sides.value = previousSides
		        compensation = 0
		        for(side <- 0 until sides.value) {
		          compensation += SHRINKS(side) - 1
		        }
		    }
	    }
	    var managedContent :ju.List[jfxs.Node] = _
        getChildren.addListener(
          new javafx.collections.ListChangeListener[jfxs.Node]() {
            override def onChanged(change :javafx.collections.ListChangeListener.Change[_ <:jfxs.Node]) {
              managedContent = getManagedChildren.asInstanceOf[ju.List[jfxs.Node]]
              referenceNode = if (0 == managedContent.size) null else managedContent.get(center.value)
              nodePrefWidth.value = if (null == referenceNode) 0 else referenceNode.prefWidth(preferredHeight.value)
              visibleDirty = true
            }
          }
        )
	    private[layout] val center = new IntegerProperty
	    center.value = 0 
	    center onChange {
            (_, previousCenter, newCenter) => {
                val sizeOfContent = managedContent.size - 1;
                if ((0 <= sizeOfContent) && (sizeOfContent < newCenter.intValue)) { center.value = sizeOfContent }
                if (0 > center.intValue) { center.value = 0 }
                visibleDirty = true
                requestLayout()
            }
	    }
	    var referenceNode :jfxs.Node = _
	    var visibleDirty = true
	    var visibleContent :Seq[Int] = Seq(-1, -2, -1, -2, -1, -2, -1)
	
	    private[this] def updateVisible() = {
            managedContent = getManagedChildren().asInstanceOf[ju.List[jfxs.Node]]
            val sizeOfContent = managedContent.size
            visibleContent = for (i <- (center.value - LATERAL) to (center.value + LATERAL)) 
                yield { if (i < sizeOfContent) { i } else { -1 } }
            val iterator = managedContent.iterator
            while (iterator.hasNext) {
                val node :jfxs.Node = iterator.next
                node.setCache(false)
                node.setEffect(null)
                node.getTransforms.clear
                node.setVisible(false)
            }
            visibleDirty = false
	    }
	
	    override def layoutChildren() = {
	        if (visibleDirty) {
	            updateVisible
	        }
	        for (position <- (LATERAL + sides.value) to LATERAL by -1) {
	            prepareNode(position)
	        }
	        for (position <- (LATERAL - sides.value) to LATERAL) {
	            prepareNode(position)
	        }
	        super.layoutChildren()
	    }
	    
	    private[this] def prepareNode(position :Int) = {
	        var index = visibleContent(position)
	        if (0 <= index) {
		        val node :jfxs.Node = managedContent.get(index)
		        node.setVisible(true)
		        node.setCache(true)
		        node.setEffect(perspectives.apply(position))
		        node.getTransforms().add(translations.apply(position))
	        }
	    }
	    
	    override def computePrefWidth(height :Double) :Double = {
	      var width = 0.5
	      for (side <- 0 until sides.value) {
	        width += SHRINKS(side)
	      }
	      2 * width * nodePrefWidth.value
	    }
        override def computeMinWidth(height :Double) :Double = computePrefWidth(height)
        override def computeMaxWidth(height :Double) :Double = computePrefWidth(height)
	    
	    def focus(node :Node) = {
	        val index = indexOf(node)
	        if (0 <= index) {
	            center.value = index
	        }
	    }
	
	    def indexOf(node :Node) :Int = {
	        managedContent = getManagedChildren.asInstanceOf[ju.List[jfxs.Node]]
	        for (position <- (LATERAL + sides.value) to (LATERAL - sides.value) by -1) {
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
	        var bounds = this.boundsInLocalProperty.get;
	        if ((localPoint.getX < bounds.getWidth) && (localPoint.getY < bounds.getHeight)) {
	            // FIXME calculate the effective index of the managed content at the given point
	            var index :Int = -1
	            for (position <- (LATERAL + sides.value) to (LATERAL - sides.value) by -1 ) {
	                index = visibleContent(position)
	                if (0 <= index) {
		                var node = managedContent.get(index)
		                if (node.boundsInParentProperty.get.contains(localPoint)) {
		                    return index
		                }
	                }
	            }
	            -1
	        } else {
	            -1  // should reject only when out of the layoutInfo bounds
	        }
	    }
	}
}