package turtle_tank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FeederController {

    @RequestMapping(value = "/feeder")
    public void feeder() {
        GPIO.motor.rotate(-2);
    }
}