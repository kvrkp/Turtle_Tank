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
        DecimalFormat df = new DecimalFormat("#.#");

        switch(name) {
            case "water":
                temp = df.format((double) (Temperature.getWater()));
                break;

            case "air":
                temp = df.format((double) (Temperature.getAir()));
                break;

            case "basking":
                temp = df.format((double) (Temperature.getBasking()));
                break;

            default: temp = "Fail";
        }
        return temp;
    }
}