package turtle_tank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import twitter4j.TwitterException;

import java.time.LocalTime;

@Controller
public class FeederController {

    // variables to ensure no over feeding is able to occur
    private static volatile LocalTime timeoutTime;
    private static volatile int count = 0;
    private static volatile int overFeedingCount = 0;
    private volatile String[] overFeeding = {"Whoa, Whoa, Buddy. Let's just chill.",
            "Awe, Donnie just isn't feeling the grub right now.",
            "Do you really want to harm Donnie?",
            "Enough, just let the turtle catch his breath.",
            "Come back another time, he can only eat so much.",
            "This is getting out of hand...",
            "Every time you click, it will cost you a PayPal quarter.",
            "Feeder is disabled for now try again after "};

    // inner class for timeout
    private class Timeout implements Runnable {

        @Override
        public void run() {
            try {
                timeoutTime = LocalTime.now();                      // get time stamp
                FeederController.count = 1;                         // feeding flag to reflect feeding

                try {                                               // shout out to Donnie's peeps on Twitter
                    TwitterPost.postSnacks();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }

                GPIO.motor.setStepsPerRevolution(2038);
                GPIO.motor.setStepSequence(GPIO.sequence);
                GPIO.motor.setStepInterval(1);
                GPIO.motor.rotate(-2);                              // one full rotation
                Thread.sleep(3600000);                              // timeout for 1 hr
            } catch (InterruptedException e) {
                System.err.println("Feeder Thread Interrupted");
            }
            FeederController.count = 0;                             // after timeout of 1 hr feeding flag allow feed
            FeederController.overFeedingCount = 0;                  // reset to reflect start of overFeeding array
        }
    }

    @RequestMapping(value = "/feederToggle", method = RequestMethod.GET)
    @ResponseBody
    synchronized public String feederToggle() {
        String status;

        if(SunRSDB.getNighttimeBool()) status = "NightTime";                         // check to see if it is night time
        else {
            if(count > 0) status = (overFeedingCount == 7) ? overFeeding[overFeedingCount] +
                                   ((1+timeoutTime.getHour() < 13) ? (1+timeoutTime.getHour()) : (1+timeoutTime.getHour() - 12)) +
                                   ":" + ((timeoutTime.getMinute() > 9) ? timeoutTime.getMinute() : "0"+timeoutTime.getMinute() ) +
                                   " MST" : overFeeding[overFeedingCount++];        // output overFeeding[7] + timestamp
            else {
                Timeout fClass = new Timeout();                                     // instantiate new Timeout class
                (new Thread(fClass)).start();                                       // start new thread
                status = "Feeding";                                                 // return Feeding to front end
            }
        }
        return status;
    }

    @RequestMapping(value = "/feederStatus", method = RequestMethod.GET)
    @ResponseBody
    synchronized public String feederStatus() {
        String status;
        if(count > 0 || SunRSDB.getNighttimeBool()) status = "Disabled";            // if count > 0 or if night time then disabled
        else status = "Feed";
        return status;
    }
}