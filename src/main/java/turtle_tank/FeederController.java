package turtle_tank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FeederController {

    // variables to ensure no over feeding is able to occur
    private volatile int count = 0;
    private Thread t = new Thread(new Thread(){
        public void run() {
            try {
                Thread.sleep(18000000); // timeout for 30 mins
                count = 0;
                overFeedingCount = 0;
            } catch (InterruptedException e) {
                System.err.println("Feeder Thread Interrupted");
            }
        }
    });

    // lets add some humor into the mix
    private String[] overFeeding = {"Whoa, Whoa, Buddy. Let's just chill.",
                            "Awe, Donnie just isn't feeling the grub right now.",
                            "Do you really want to harm Donnie?",
                            "Enough, just let the turtle catch his breath.",
                            "Come back another time, he can only eat so much.",
                            "This is getting out of hand...",
                            "Every time you click, it will cost you a PayPal quarter.",
                            "Feeder is Disabled for now"};

    private int overFeedingCount;

    @RequestMapping(value = "/feederToggle", method = RequestMethod.GET)
    @ResponseBody
    public String feederToggle() {
        String status;

        if(count > 0) {
            status = (overFeedingCount == 7) ? overFeeding[overFeedingCount] : overFeeding[overFeedingCount++];
        }
        else {
            ++count;
            GPIO.motor.setStepsPerRevolution(2038);
            GPIO.motor.setStepSequence(GPIO.sequence);
            GPIO.motor.setStepInterval(1);
            GPIO.motor.rotate(-2);
            status = "Feeding";
            t.start();
        }
        return status;
    }

    @RequestMapping(value = "/feederStatus", method = RequestMethod.GET)
    @ResponseBody
    public String feederStatus() {
        String status;
        if(count > 0) status = "Disabled";
        else status = "Feed";

        return status;
    }
}