package turtle_tank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FeederController {

    // variables to ensure no over feeding is able to occur
    private static volatile int count = 0;
    private static volatile int overFeedingCount = 0;

    // inner class for timeout
    private class Timeout implements Runnable {
        @Override
        public void run() {
            try {
                FeederController.count = 1;
                Thread.sleep(3600000); // timeout for 1 hr
            } catch (InterruptedException e) {
                System.err.println("Feeder Thread Interrupted");
            }
            FeederController.count = 0;
        }
    }

    // lets add some humor into the mix
    private volatile String[] overFeeding = {"Whoa, Whoa, Buddy. Let's just chill.",
                            "Awe, Donnie just isn't feeling the grub right now.",
                            "Do you really want to harm Donnie?",
                            "Enough, just let the turtle catch his breath.",
                            "Come back another time, he can only eat so much.",
                            "This is getting out of hand...",
                            "Every time you click, it will cost you a PayPal quarter.",
                            "Feeder is disabled for now try again in an hour"};

    @RequestMapping(value = "/feederToggle", method = RequestMethod.GET)
    @ResponseBody
    synchronized public String feederToggle() {
        String status;

        if(count > 0) status = (overFeedingCount == 7) ? overFeeding[overFeedingCount] : overFeeding[overFeedingCount++];
        else {
            Timeout fClass = new Timeout();
            (new Thread(fClass)).start();
            GPIO.motor.setStepsPerRevolution(2038);
            GPIO.motor.setStepSequence(GPIO.sequence);
            GPIO.motor.setStepInterval(1);
            GPIO.motor.rotate(-2);
            status = "Feeding";
        }
        return status;
    }

    @RequestMapping(value = "/feederStatus", method = RequestMethod.GET)
    @ResponseBody
    synchronized public String feederStatus() {
        String status;
        if(count > 0) status = "Disabled";
        else status = "Feed";

        return status;
    }
}