package turtle_tank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;

public class SunriseSunset {

    // instance variables
    static private int sunRiseMinute, sunRiseHour, sunSetMinute, sunSetHour;

    // constructor sets instance variables
    SunriseSunset() {
        // create LocalDate object and set year, month, day
        LocalDate date = LocalDate.now();
        String year = Integer.toString(date.getYear());
        String month = Integer.toString(date.getMonthValue());
        String day = Integer.toString(date.getDayOfMonth());
        String state = "UT", city = "provo";

        // make URL to webpage
        try {
            URL url = new URL("http://aa.usno.navy.mil/rstt/onedaytable?ID=AA&year=" + year + "&month=" + month + "&day=" + day + "&state=" + state + "&place=" + city);

            // get input stream through URL
            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String curLine = null;

            // read each line until Sunrise or Sunset is found
            while((curLine = br.readLine()) != null) {
                if(curLine.contains("Sunrise")) { // if Sunrise found...

                    // search curLine for ":" then get charAt - 2 from ":" if equal to '>' then hour less than 10 else greater than or equal to 10
                    sunRiseHour = (curLine.charAt(curLine.indexOf(":") - 2) == '>') ?
                            Integer.parseInt(curLine.substring(curLine.indexOf(":") - 1, curLine.indexOf(":"))) :
                            Integer.parseInt(curLine.substring(curLine.indexOf(":") - 2, curLine.indexOf(":")));

                    // search curLine for ":" then substring 2 chars AFTER that, and cast from string to int
                    sunRiseMinute = Integer.parseInt(curLine.substring(curLine.indexOf(":") + 1, curLine.indexOf(":") + 3));

                }else if (curLine.contains("Sunset")) { // if Sunset found...

                    // search curLine for ":" then get charAt - 2 from ":" if equal to '>' then hour less than 10 else greater than or equal to 10
                    sunSetHour = (curLine.charAt(curLine.indexOf(":") - 2) == '>') ?
                            Integer.parseInt(curLine.substring(curLine.indexOf(":") - 1, curLine.indexOf(":"))) :
                            Integer.parseInt(curLine.substring(curLine.indexOf(":") - 2, curLine.indexOf(":")));

                    // search curLine for ":" then substring 2 chars AFTER that, and cast from string to int
                    sunSetMinute = Integer.parseInt(curLine.substring(curLine.indexOf(":") + 1, curLine.indexOf(":") + 3));
                }
            }
        }catch(IOException e) {
            System.out.println("Unable to retrieve Sunrise and Sunset\nDefaults set\n\tSunrise: 7:00am\n\tSunset: 8:00pm");
            sunRiseHour = 7;
            sunSetHour = 8;
            sunRiseMinute = sunSetMinute = 0;
        }
    }

    static int getSunRiseHour() {
        return sunRiseHour;
    }

    static int getSunRiseMinute() {
        return sunRiseMinute;
    }

    static int getSunSetHour() {
        return sunSetHour + 12;
    }

    static int getSunSetMinute() {
        return sunSetMinute;
    }
}