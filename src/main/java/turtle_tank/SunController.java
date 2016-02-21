package turtle_tank;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import turtle_tank.SunRSDB;

@Controller
public class SunController {

    @RequestMapping(value = "/getRoutine", method = RequestMethod.GET)
    @ResponseBody
    public String getSunrise(@RequestParam(value="name", required = true) String name) {
        String routine;
        switch (name) {
            case "sunrise":
                routine = SunRSDB.getSunrise();
                break;
            case "breakfast":
                routine = SunRSDB.getBreakfast();
                break;
            case "dinner":
                routine = SunRSDB.getDinner();
                break;
            case "sunset":
                routine = SunRSDB.getSunset();
                break;
            case "nightTimeBool":
                routine = Boolean.toString(SunRSDB.getNighttimeBool());
                break;
            default:
                routine = "Fail";
        }
        return routine;
    }
}