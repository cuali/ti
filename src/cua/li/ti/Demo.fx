package cua.li.ti;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Math;

import cua.li.ti.clocks.SpiralClock;
import cua.li.ti.scene.gadget.HexagonalColorPicker;
import cua.li.ti.scene.layout.Dock;
import cua.li.ti.scene.layout.ShiftingStack;

/**
 * @author A@cua.li
 */
public class Demo {
    def RADIUS = 230;
    def CLOCK = SpiralClock {
        size: 300
        translateX: ( Math.sqrt(2) * Math.sqrt(3) * RADIUS - 300 ) / 4
        translateY: 50
    };
    def dock = Dock {
        sides: 1
        layoutInfo: LayoutInfo {
            height: 400
            width: 1200
        }
        content: [
            ShiftingStack {
                content: [
                    Rectangle {
                        height: 400
                        width: Math.sqrt(3) * RADIUS
                        strokeWidth: 3
                        stroke: Color.DARKRED
                        fill: Color.TRANSPARENT
                    }
                    CLOCK
                ]
            }
            HexagonalColorPicker {
                centerX: RADIUS
                centerY: Math.sqrt(3) * RADIUS / 2
                radius: RADIUS
                onClose: function(color :Color,hasChanged :Boolean) :Void {
                    if (hasChanged) then CLOCK.selectedPaint = color
                }
            }
        ]
        center: 1
    }
}

function run () :Void {
    Stage {
        title: "Demo"
        scene: Scene {
            height: 400
            width: 1200
            content: [ Demo {}.dock ]
        }
    }
}
