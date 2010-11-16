package cua.li.ti;

import javafx.geometry.VPos;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.paint.Color;

import cua.li.ti.clocks.SpiralClock;
import cua.li.ti.scene.layout.Dock;
import cua.li.ti.scene.gadget.HexagonalColorPicker;
import cua.li.ti.scene.layout.ShiftingStack;
import javafx.scene.Scene;

/**
 * @author Alain
 */
public class Demo {
    var clock :SpiralClock;
    def dock = Dock {
        sides: 2
        layoutInfo: LayoutInfo {
            height: 400
            width: 1000
        }
        nodeVPos: VPos.CENTER
        content: [
            ShiftingStack {
                content: [
                    clock = SpiralClock {
                        size: 400
                    }
                ]
            }
            HexagonalColorPicker {
                centerX: 150
                centerY: 200
                radius: 220
                onClose: function(color:Color,changed:Boolean):Void {
                    if (changed) then clock.secondPaint = color
                }
            }
        ]
        center: 1
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:main
    public-read def scene: javafx.scene.Scene = javafx.scene.Scene {
        width: 480.0
        height: 320.0
        content: getDesignRootNodes ()
    }
    
    public-read def currentState: org.netbeans.javafx.design.DesignState = org.netbeans.javafx.design.DesignState {
    }
    
    public function getDesignRootNodes (): javafx.scene.Node[] {
        [ ]
    }
    
    public function getDesignScene (): javafx.scene.Scene {
        scene
    }
    // </editor-fold>//GEN-END:main

}

function run (): Void {
    var design = Demo {};

    javafx.stage.Stage {
        title: "Demo"
        scene: //design.getDesignScene ()
            Scene {
                height: 400
                width: 1000
                content: [
                    design.dock
                ]
            }
    }
}
