package turtle_tank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DecimalFormat;

@Controller
public class TemperatureController {

    @RequestMapping(value = "/getTemp", method = RequestMethod.GET)
    @ResponseBody
    public String getTemp(@RequestParam(value="name", required = true) String name) {
        String temp;

        switch(name) {
            case "water":
                temp = Integer.toString((int) Temperature.getWater());
                break;

            case "air":
                temp = Integer.toString((int) Temperature.getAir());
                break;

            case "basking":
                temp = Integer.toString((int) Temperature.getBasking());
                break;

            default: temp = "Fail";
        }
        return temp + "\u00b0";
    }
}