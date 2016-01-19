package turtle_tank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RelayController {

    @RequestMapping("/relayToggle")
    public String relayToggle(@RequestParam(value="name", required = true, defaultValue = "") String name) {
        String status;

        switch(name) {
            case "mainLight":
                GPIO.mainLight.toggle();
                if (GPIO.mainLight.isHigh()) status = "Off";
                else status = "On";
                break;

            case "uvbLight":
                GPIO.uvbLight.toggle();
                if (GPIO.uvbLight.isHigh()) status = "Off";
                else status = "On";
                break;

            case "heatLight":
                GPIO.heatLight.toggle();
                if (GPIO.heatLight.isHigh()) status = "Off";
                else status = "On";
                break;

            case "bubbles":
                GPIO.bubbles.toggle();
                if (GPIO.bubbles.isHigh()) status = "Off";
                else status = "On";
                break;

            default: status = "Fail";
        }
        return status;
    }

    @RequestMapping("/relayStatus")
    public String relayStatus(@RequestParam(value="name", required = true, defaultValue = "") String name) {
        String status;

        switch(name) {
            case "mainLight":
                if (GPIO.mainLight.isHigh()) status = "Off";
                else status = "On";
                break;

            case "uvbLight":
                if (GPIO.uvbLight.isHigh()) status = "Off";
                else status = "On";
                break;

            case "heatLight":
                if (GPIO.heatLight.isHigh()) status = "Off";
                else status = "On";
                break;

            case "bubbles":
                if (GPIO.bubbles.isHigh()) status = "Off";
                else status = "On";
                break;

            case "waterHeat ":
                if (GPIO.waterHeat.isHigh()) status = "Off";
                else status = "On";
                break;

            case "feeder ":
                if (GPIO.feeder.isHigh()) status = "Off";
                else status = "On";
                break;

            case "waterSwitch":
                if (GPIO.waterSwitch.isHigh()) status = "Off";
                else status = "On";
                break;

            default: status = "Fail";
        }
        return status;
    }
}