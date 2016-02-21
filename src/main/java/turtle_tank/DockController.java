package turtle_tank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DockController {
    private static volatile boolean outOfWater;

    @RequestMapping(value = "/turtleStatus", method = RequestMethod.GET)
    @ResponseBody
    synchronized public static String turtleStatus() {
        if(outOfWater) {
            return "Out Of The Water";
        }
        else {
            return "In The Water";
        }
    }

    public static boolean isOutOfWater() {
        return outOfWater;
    }

    public static void setOutOfWater(boolean outOfWater) {
        DockController.outOfWater = outOfWater;

        /* troubleshooting
        if (DockController.outOfWater) System.out.println("out of water");
        else System.out.println("in the water"); */
    }
}