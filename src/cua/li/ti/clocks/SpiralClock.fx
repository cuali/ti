package cua.li.ti.clocks;

import java.util.Calendar;
import java.util.Date;

import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

/**
 * Fancy clock based on sample code from
 * http://javafx.com/samples/AnalogClock/index.html
 * and a design from
 * http://www.stupid.com/fun/DALI.html
 * or
 * http://www.gadgetshop.com/Lifestyle_Gadgets/ViewAll/Salvador+Dali+Clock/EPN247825
 *
 * @author Bearez Alain
 */
public class SpiralClock extends CustomNode {
    public-init var size: Number = 300;
    public var hourPaint: Paint = Color.DARKBLUE;
    public var minutePaint: Paint = Color.DARKGREEN;
    public var secondPaint: Paint = Color.DARKRED;
    var seconds: Number = 20;
    var minutes: Number = 0;
    var hours: Number = 9;
    init {
        var timeline = Timeline {
            repeatCount: Timeline.INDEFINITE
            keyFrames : [
                KeyFrame {
                    time : 1s
                    action: function() {
                        actionOnTick();
                    }
                }
            ]
        }
        timeline.play();
    }
    function actionOnTick () {
        def calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // fix for mobile
        seconds = calendar.get(Calendar.SECOND);
        minutes = calendar.get(Calendar.MINUTE);
        hours = calendar.get(Calendar.HOUR);
    }
    override public function create(): Node {
        return Group {            
            transforms: [
                Transform.translate(size/2, size/2)
            ]
            content: [
                 ImageView {
                    translateX: -size/2
                    translateY: -size/2
                    image: Image { url : "{__DIR__}SpiralClockNoPointers.png" }
                    fitHeight: size
                    fitWidth: size
                }
                SpiralHand {
                    length: size * 0.3
                    paint: hourPaint
                    rotation: Rotate {
                        angle: bind (hours + minutes / 60) * 360/12 - 90
                    }
                }
                SpiralHand {
                    length: size * 0.4
                    paint: minutePaint
                    rotation: Rotate {
                        angle: bind minutes * 360/60 -90 
                    }
                }
                SpiralHand {
                    length: size / 2
                    paint: secondPaint
                    rotation: Rotate {
                        angle: bind seconds * 360/60 -90
                    }
                }
            ]
        }
    }
}

class SpiralHand extends CustomNode {
    public-init var length: Number;
    public-init var paint: Paint;
    public-init var rotation: Rotate;
    override public function create(): Node {
        return Path {
            transforms: bind rotation
            fill: bind paint
            stroke: null
            elements: [
                MoveTo {
                    x: bind length * -0.3 -8
                    y: bind length * -0.1
                }
                ArcTo {
                    x: bind length * -0.2
                    y: bind length * -0.2 -6
                    radiusX: bind length * 0.1 +8
                    radiusY: bind length * 0.1 +6
                    sweepFlag: true
                }
                ArcTo {
                    x: 0 +4
                    y: 0
                    radiusX: bind length * 0.2 +4
                    radiusY: bind length * 0.2 +6
                    sweepFlag: true
                }
                ArcTo {
                    x: bind length * 0.3
                    y: bind length * 0.3 -2
                    radiusX: bind length * 0.3 -4
                    radiusY: bind length * 0.3 -2
                }
                ArcTo {
                    x: bind length * 0.8
                    y: bind length * -0.2
                    radiusX: bind length * 0.5
                    radiusY: bind length * 0.5 -2
                }
                ArcTo {
                    x: bind length * 0.3
                    y: bind length * 0.3 +2
                    radiusX: bind length * 0.5
                    radiusY: bind length * 0.5 +2
                    sweepFlag: true
                }
                ArcTo {
                    x: 0 -4
                    y: 0
                    radiusX: bind length * 0.3 +4
                    radiusY: bind length * 0.3 +2
                    sweepFlag: true
                }
                ArcTo {
                    x: bind length * -0.2
                    y: bind length * -0.2 +6
                    radiusX: bind length * 0.2 -4
                    radiusY: bind length * 0.2 +6
                }
                ArcTo {
                    x: bind length * -0.3 +8
                    y: bind length * -0.1
                    radiusX: bind length * 0.1 +8
                    radiusY: bind length * -0.1 +6
                }
            ]
        }
    }
}
