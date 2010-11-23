package cua.li.ti.scene.layout;

import javafx.scene.Node;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.transform.Translate;
import javafx.util.Math;

def SQRT_2 = Math.sqrt(2);
def HALF_SQRT_2 = SQRT_2/2;
def QUARTER_SQRT_2 = SQRT_2/4;
def LATERAL = 3;    // take care of adding or removing perspectives and translations accordingly

/**
 * @author A@cua.li
 */
public class Dock extends HBox {
    override var blocksMouse = true;
    def nodePrefWidth = bind getNodePrefWidth(referenceNode);
    def pxl4 = bind (preferredWidth - nodePrefWidth * (1 + SQRT_2 + HALF_SQRT_2)) / 2;
    def pxl3 = bind (preferredWidth - nodePrefWidth * (1 + SQRT_2 + QUARTER_SQRT_2)) / 2;
    def pxl2 = bind (preferredWidth - nodePrefWidth * (1 + SQRT_2)) / 2;
    def pxl1 = bind (preferredWidth - nodePrefWidth) / 2;
    def pxr1 = bind (preferredWidth + nodePrefWidth) / 2;
    def pxr2 = bind (preferredWidth + nodePrefWidth * (1 + SQRT_2)) / 2;
    def pxr3 = bind (preferredWidth + nodePrefWidth * (1 + SQRT_2 + QUARTER_SQRT_2)) / 2;
    def pxr4 = bind (preferredWidth + nodePrefWidth * (1 + SQRT_2 + HALF_SQRT_2)) / 2;
    def pyt4 = bind preferredHeight * (1 - QUARTER_SQRT_2) / 2;
    def pyt3 = bind preferredHeight * (1 - 0.5) / 2;
    def pyt2 = bind preferredHeight * (1 - HALF_SQRT_2) / 2;
    def pyb2 = bind preferredHeight * (1 + HALF_SQRT_2) / 2;
    def pyb3 = bind preferredHeight * (1 + 0.5) / 2;
    def pyb4 = bind preferredHeight * (1 + QUARTER_SQRT_2) / 2;
    // <editor-fold defaultstate="collapsed" desc="carefully ordered sequence of perspective transforms">
    def perspectives: PerspectiveTransform[] = [
        PerspectiveTransform {
            ulx: 0
            uly: bind pyt4
            urx: bind pxl3 - pxl4
            ury: bind pyt3
            lrx: bind pxl3 - pxl4
            lry: bind pyb3
            llx: 0
            lly: bind pyb4
        }
        PerspectiveTransform {
            ulx: 0
            uly: bind pyt3
            urx: bind pxl2 - pxl3
            ury: bind pyt2
            lrx: bind pxl2 - pxl3
            lry: bind pyb2
            llx: 0
            lly: bind pyb3
        }
        PerspectiveTransform {
            ulx: 0
            uly: bind pyt2
            urx: bind pxl1 - pxl2
            ury: 0
            lrx: bind pxl1 - pxl2
            lry: bind preferredHeight
            llx: 0
            lly: bind pyb2
        }
        PerspectiveTransform {
            ulx: 0
            uly: 0
            urx: bind pxr1 - pxl1
            ury: 0
            lrx: bind pxr1 - pxl1
            lry: bind preferredHeight
            llx: 0
            lly: bind preferredHeight
        }
        PerspectiveTransform {
            ulx: 0
            uly: 0
            urx: bind pxr2 - pxr1
            ury: bind pyt2
            lrx: bind pxr2 - pxr1
            lry: bind pyb2
            llx: 0
            lly: bind preferredHeight
        }
        PerspectiveTransform {
            ulx: 0
            uly: bind pyt2
            urx: bind pxr3 - pxr2
            ury: bind pyt3
            lrx: bind pxr3 - pxr2
            lry: bind pyb3
            llx: 0
            lly: bind pyb2
        }
        PerspectiveTransform {
            ulx: 0
            uly: bind pyt3
            urx: bind pxr4 - pxr3
            ury: bind pyt4
            lrx: bind pxr4 -pxr3
            lry: bind pyb4
            llx: 0
            lly: bind pyb3
        }
    ];
    // </editor-fold>
    def translations: Translate[] = [
        Translate { x: bind pxl4 }
        Translate { x: bind pxl3 }
        Translate { x: bind pxl2 }
        Translate { x: bind pxl1 }
        Translate { x: bind pxr1 }
        Translate { x: bind pxr2 }
        Translate { x: bind pxr3 }
    ];

    public-init var sides :Integer = LATERAL on replace previousSides {
        if (0 > sides) sides = 0;
        if (LATERAL < sides) sides = previousSides
    }
    public-init var preferredHeight :Number = if (null == this.layoutInfo)
                then super.getPrefHeight(width) - 1
                else (this.layoutInfo as LayoutInfo).height - 1;
    public-init var preferredWidth :Number = if (null == this.layoutInfo)
                then super.getPrefWidth(height) 
                else (this.layoutInfo as LayoutInfo).width;
    override var content on replace {
                def managedContent = getManaged(content);
                def referenceIndex = sizeof managedContent - 1;
                referenceNode = if (0 > referenceIndex) then null else managedContent[referenceIndex];
                visibleDirty = true;
                center = referenceIndex
            }
    public var center :Integer = 0 on replace {
                def sizeOfContent = sizeof getManaged(content) - 1;
                if ((0 <= sizeOfContent) and (sizeOfContent < center)) then center = sizeOfContent;
                if (0 > center) then center = 0;
                visibleDirty = true;
                requestLayout()
            }
    var referenceNode: Node;
    var visibleDirty = true;
    var visibleContent: Integer[] = [-1, -2, -1, -2, -1, -2, -1];

    function updateVisible(): Void {
        def managedContent = getManaged(content);
        def sizeOfContent = sizeof managedContent;
        visibleContent = for (i in [center - LATERAL .. center + LATERAL]) { if (i < sizeOfContent) then i else -1 };
        for (node in managedContent) {
            node.cache = false;
            node.effect = null;
            delete node.transforms;
            node.visible = false
        }
        visibleDirty = false
    }

    override function doLayout(): Void {
        if (visibleDirty) {
            updateVisible()
        }
        def managedContent = getManaged(content);
        for (position in [LATERAL + sides .. LATERAL step -1]) {
            layoutNode(managedContent, position)
        }
        for (position in [LATERAL - sides .. LATERAL]) {
            layoutNode(managedContent, position)
        }
    }
    
    function layoutNode(managedContent :Node[], position :Integer) {
        var index = visibleContent[position];
        if (0 > index) {
            return
        }
        def node = managedContent[index];
        node.visible = true;
        node.cache = true;
        node.effect = perspectives[position];
        layoutNode(node,  translations[position].x, -1, getNodePrefWidth(node), getNodePrefHeight(node), false)
    }
}
