package turtle_tank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FeederController {

    @RequestMapping(value = "/feeder", method = RequestMethod.GET)
    public void feeder() {
        GPIO.motor.setStepsPerRevolution(2038);
        GPIO.motor.setStepSequence(GPIO.sequence);
        GPIO.motor.setStepInterval(1);
        GPIO.motor.rotate(-2);
    }
}