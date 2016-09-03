package turtle_tank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import twitter4j.TwitterException;

import java.time.LocalTime;

@Controller
public class FeederController {
    // variables to ensure no over feeding is able to occur
    private static volatile LocalTime timeoutTime;
    private static volatile boolean feeding = false;
    private static volatile int dailyFeedTotal = 0;

    // inner class for timeout
    private static class Timeout implements Runnable {
        @Override
        public void run() {
            try {
                timeoutTime = LocalTime.now();                      // get time stamp
                FeederController.feeding = true;                    // feeding flag to reflect feeding
                incrementDailyFeedTotal();                          // increment to ensure no over feeding

                try {                                               // shout out to Donnie's peeps on Twitter
                    TwitterPost.postSnacks();
                } catch (TwitterException e) {
                    System.err.println("Twitter Feed Error: " + e.getMessage());
                }

                GPIO.motor.setStepsPerRevolution(2048);
                GPIO.motor.setStepSequence(GPIO.sequence);
                GPIO.motor.setStepInterval(1);
                GPIO.motor.rotate(1);                              // one full rotation
                Thread.sleep(7200000);                              // timeout for 2 hrs
            } catch (InterruptedException e) {
                System.err.println("Feeder Thread Interrupted");
            }
            FeederController.feeding = false;                       // after timeout of 2 hrs feeding flag allow feed
        }
    }

    @RequestMapping(value = "/feederToggle", method = RequestMethod.GET)
    @ResponseBody
    synchronized public static String feederToggle(@RequestParam(value="password", required = false) String password) {
        String status;

        if(password.equals(GodModeController.getThePassword())) {                   // check to see if passwords match, and enable godMode
            GPIO.motor.setStepsPerRevolution(2048);
            GPIO.motor.setStepSequence(GPIO.sequence);
            GPIO.motor.setStepInterval(1);
            GPIO.motor.rotate(1);
            status = "Feeding";
        } else if(SunRSDB.getNighttimeBool()) status = "Disabled";                  // check to see if it is night time
        else if (overFed()) status = "OverFed";                                     // check to see if overfed
        else {
            if(FeederController.feeding) status = getTimeoutTime();                 // output timestamp
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
    synchronized public static String feederStatus() {
        String status;
        if(SunRSDB.getNighttimeBool()) status = "Disabled";
        else if(overFed()) status = "OverFed";
        else if(FeederController.feeding) status = getTimeoutTime();
        else status = "Feed";
        return status;
    }

    @RequestMapping(value = "/getTimeoutTime", method = RequestMethod.GET)
    @ResponseBody
    synchronized public static String getTimeoutTime() {
        if(SunRSDB.getNighttimeBool()) return "Disabled";
        else if (overFed()) return "OverFed";
        else {
            return "Try again after " + ((2 + timeoutTime.getHour() < 13) ? (2 + timeoutTime.getHour()) : (2 + timeoutTime.getHour() - 12)) +
                    ":" + ((timeoutTime.getMinute() > 9) ? timeoutTime.getMinute() : "0" + timeoutTime.getMinute()) +
                    ((2 + timeoutTime.getHour() < 13) ? " am" : " pm") + " MST.";
        }
    }

    public static boolean overFed() { return dailyFeedTotal >= 5; }

    public static void incrementDailyFeedTotal() {
        dailyFeedTotal++;
    }

    public static void setDailyFeedTotal(int a) {
        dailyFeedTotal = a;
    }
}