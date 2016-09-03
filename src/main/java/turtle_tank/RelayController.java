package turtle_tank;

import org.springframework.stereotype.Controller;
import org.springframework.util.comparator.BooleanComparator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RelayController {
    // variables to account for Donnie's sanity
    private static volatile boolean mainLightTimeout, uvbLightTimeout, heatLightTimeout,bubblesTimeout;

    // inner class for timeout
    private class Timeout implements Runnable {
        private volatile String a;

        public Timeout(String a){
            this.a = a;
        }

        @Override
        public void run() {
            switch(a) {
                case "mainLightTimeout" : RelayController.mainLightTimeout = true;
                    break;
                case "uvbLightTimeout" : RelayController.uvbLightTimeout = true;
                    break;
                case "heatLightTimeout" : RelayController.heatLightTimeout = true;
                    break;
                case "bubblesTimeout" : RelayController.bubblesTimeout = true;
                    break;
            }

            try {
                Thread.sleep(25000); // timeout for 25 secs
            } catch (InterruptedException e) {
                System.err.println("Timeout Thread Interrupted");
            }

            switch(a) {
                case "mainLightTimeout" : RelayController.mainLightTimeout = false;
                    break;
                case "uvbLightTimeout" : RelayController.uvbLightTimeout = false;
                    break;
                case "heatLightTimeout" : RelayController.heatLightTimeout = false;
                    break;
                case "bubblesTimeout" : RelayController.bubblesTimeout = false;
                    break;
            }
        }
    }

    @RequestMapping(value = "/relayToggle", method = RequestMethod.GET)
    @ResponseBody
    synchronized public String relayToggle(@RequestParam(value="name", required = true) String name, @RequestParam(value="password", required = false) String password) {
        String status;
        boolean godMode = false;                                                                           // boolean added for godMode

        if(password.equals(GodModeController.getThePassword())) godMode = true;                            // check to see if passwords match, and enable godMode

        if(SunRSDB.getNighttimeBool() && !godMode) status = "NightTime";                                   // check if nighttime, if so status = nighttime
        else {
            switch (name) {
                case "mainLight":
                    if (mainLightTimeout && !godMode) status = "Timeout";                                  // check to see if timeout active
                    else {                                                                                 // if timeout NOT active
                        GPIO.mainLight.toggle();                                                           // toggle switch
                        status = (GPIO.mainLight.isHigh()) ? "Off" : "On";                                 // assign status value based off of toggle status
                        if(!godMode) {                                                                     // if not god mode, you must have limits!
                            Timeout mlClass = new Timeout("mainLightTimeout");                             // create instance of inner class and call start()
                            (new Thread(mlClass)).start();                                                 // activate timeout for however long above
                        }
                    }
                    break;
                case "uvbLight":
                    if (uvbLightTimeout && !godMode) status = "Timeout";
                    else {
                        GPIO.uvbLight.toggle();
                        status = (GPIO.uvbLight.isHigh()) ? "Off" : "On";
                        if(!godMode) {
                            Timeout uvblClass = new Timeout("uvbLightTimeout");
                            (new Thread(uvblClass)).start();
                        }
                    }
                    break;
                case "heatLight":
                    if (heatLightTimeout && !godMode) status = "Timeout";
                    else {
                        GPIO.heatLight.toggle();
                        status = (GPIO.heatLight.isHigh()) ? "Off" : "On";
                        if(!godMode) {
                            Timeout hlClass = new Timeout("heatLightTimeout");
                            (new Thread(hlClass)).start();
                        }
                    }
                    break;
                case "bubbles":
                    if (bubblesTimeout && !godMode) status = "Timeout";
                    else {
                        GPIO.bubbles.toggle();
                        status = (GPIO.bubbles.isHigh()) ? "Off" : "On";
                        if(!godMode) {
                            Timeout bClass = new Timeout("bubblesTimeout");
                            (new Thread(bClass)).start();
                        }
                    }
                    break;
                default:
                    status = "Fail";
            }
        }
        return status;
    }

    @RequestMapping(value = "/relayStatus", method = RequestMethod.GET)
    @ResponseBody
    synchronized public String relayStatus(@RequestParam(value="name", required = true) String name) {
        String status;

        switch(name) {
            case "mainLight":
                status = (GPIO.mainLight.isHigh()) ? "Off" : "On";
                break;
            case "uvbLight":
                status = (GPIO.uvbLight.isHigh()) ? "Off" : "On";
                break;
            case "heatLight":
                status = (GPIO.heatLight.isHigh()) ? "Off" : "On";
                break;
            case "bubbles":
                status = (GPIO.bubbles.isHigh()) ? "Off" : "On";
                break;
            case "waterHeat ":
                status = (GPIO.waterHeat.isHigh()) ? "Off" : "On";
                break;
//            case "feeder ":
//                status = (GPIO.feeder.isHigh()) ? "Off" : "On";
//                break;
//            case "waterSwitch":
//                status = (GPIO.waterSwitch.isHigh()) ? "Off" : "On";
//                break;
            default: status = "Fail";
        }
        return status;
    }
}