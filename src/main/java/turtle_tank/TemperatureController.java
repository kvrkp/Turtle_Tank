package turtle_tank;

        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TemperatureController {

    @RequestMapping(value = "/getTemp")
    public String getTemp(@RequestParam(value="name", required = true, defaultValue = "") String name) {
        String temp;

        switch(name) {
            case "water":
                temp = Float.toString(Temperature.getWater());
                break;

            case "air":
                temp = Float.toString(Temperature.getAir());
                break;

            case "basking":
                temp = Float.toString(Temperature.getBasking());
                break;

            default: temp = "Fail";
        }
        return temp;
    }
}