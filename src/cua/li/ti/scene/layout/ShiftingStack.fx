package cua.li.ti.scene.layout;

import javafx.scene.layout.Stack;

/**
 * In its current implementation this class assumes all content nodes are the same size.
 * It does NOT honor the <code>nodeHPos</code> and <code>nodeVPos</code> attributes.
 * @author A@cua.li
 */

public class ShiftingStack extends Stack {
    public-init var shiftX: Number = 10;
    public-init var shiftY: Number = 10;

    var superHeight: Number;
    var superWidth: Number;
    var preferredWidth: Number;
    var preferredHeight: Number;
    var preferredDirty = true;

    override var content on replace {
      preferredDirty = true
    }
    
    override function getPrefHeight(width:Number):Number {
        if (preferredDirty) {
            calculatePrefSize()
        }
        preferredHeight
    }
    
    override function getPrefWidth(height:Number):Number {
        if (preferredDirty) {
            calculatePrefSize()
        }
        preferredWidth
    }
    
    function calculatePrefSize():Void {
        superHeight = super.getPrefHeight(width);
        superWidth = super.getPrefWidth(height);
        def sizeOfContent = (sizeof getManaged(content)) - 1;
        preferredHeight = superHeight + shiftY * sizeOfContent;
        preferredWidth = superWidth + shiftX * sizeOfContent;
        preferredDirty = false
    }
  
    override function doLayout():Void {
        if (preferredDirty) {
            calculatePrefSize()
        }
        def managedContent = getManaged(content);
        def sizeOfContent = sizeof managedContent - 1;
        for (node in managedContent) {
            def nodeShift = sizeOfContent - indexof node;
            layoutNode(node, shiftX * nodeShift, shiftY * nodeShift, superWidth, superHeight)
        }
    }
}
