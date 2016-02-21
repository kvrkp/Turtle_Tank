package turtle_tank;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.time.LocalTime;
import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.print.Doc;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(Main.class, args);                        // start Spring Application

        LocalTime curTime;                                              // create LocalTime object

        // local variables
        boolean firstBoot = true;
        int hour, minute, second, sunriseHour = 0, sunsetHour = 0, sunriseMinute = 0, sunsetMinute = 0;
        int breakfastHour = 0, breakfastMinute = 0, dinnerHour = 0, dinnerMinute = 0;

        Temperature temperature = new Temperature();                    // create Temperature object

        // create TmpDB object. This will log temperatures to database every minute
        TmpDB tmpDB = new TmpDB();

        // create SunRSDB object. This will log sunrise, sunset, breakfast, dinner to database once a day and at startup
        SunRSDB sunRSDB = new SunRSDB();

        (new Thread(temperature)).start();                              // start Temperature thread, updated every 5 seconds

        // this will ensure that all pins are off if application is interrupted
        GPIO.gpio.setShutdownOptions(true, PinState.LOW, GPIO.pins);
        GPIO.mainLight.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        GPIO.uvbLight.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        GPIO.heatLight.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        GPIO.bubbles.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        GPIO.waterHeat.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        GPIO.feeder.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        GPIO.waterSwitch.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);

        // create and register gpio pin listener -- light on/off button
        GPIO.buttonLights.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if(event.getState() == PinState.HIGH) {                 // if button clicked toggle lights
                    GPIO.mainLight.toggle();
                    GPIO.uvbLight.toggle();
                }
            }
        });

        // create and register gpio pin listener -- water on/off button
        GPIO.buttonFloat.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if(event.getState() == PinState.HIGH) {                 // if float low turn on water
                    GPIO.waterSwitch.low();
                } else {
                    GPIO.waterSwitch.high();
                }
            }
        });

        // create and register gpio pin listener -- motionDock on/off button
        GPIO.buttonMotionDock.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if(event.getState() == PinState.HIGH) {                                             // if circuit complete motion on dock
                    DockController.setOutOfWater(true);                                             // set to true right away
                } else {
                    DockController.setOutOfWater(false);
                }
            }
        });

        // ***** begin main program loop -- events triggered by time ***** //
        while(true) {
            // get local time
            curTime = LocalTime.now();
            hour = curTime.getHour();
            minute = curTime.getMinute();
            second = curTime.getSecond();

            // update sunrise sunset at 00:09am, and update breakfast, and dinner time. Or on first boot
            if((hour == 0 && minute == 9 && second == 15) || (firstBoot == true)) {
                // create sunriseSunset object
                SunriseSunset sunriseSunset = new SunriseSunset();

                DockController.setOutOfWater(true);

                // initialize local variables
                sunriseHour = sunriseSunset.getSunRiseHour();
                sunriseMinute = sunriseSunset.getSunRiseMinute();
                sunsetHour = sunriseSunset.getSunSetHour();
                sunsetMinute = sunriseSunset.getSunSetMinute();
                System.out.println(hour + ":" + leadingZero(minute) + "\tSunrise, Sunset updated\n\tSunrise: " + sunriseHour + ":" +
                        leadingZero(sunriseMinute) + "am" + "\n\tSunset: " + (sunsetHour - 12) + ":" + leadingZero(sunsetMinute) + "pm");

                // set sunriseSunset object to null and allow for garbage collection
                sunriseSunset = null;

                // first boot determine turtle tanks state
                if(firstBoot) {
                    if(hour <= sunsetHour && hour >= sunriseHour) {
                        GPIO.mainLight.low();
                        GPIO.uvbLight.low();
                        GPIO.bubbles.low();
                        SunRSDB.setNightimeBool(false);
                    } else {
                        GPIO.mainLight.high();
                        GPIO.uvbLight.high();
                        GPIO.bubbles.high();
                        SunRSDB.setNightimeBool(true);
                    }
                    firstBoot = false;
                }

                // update breakfast, and dinner time
                breakfastHour = getBreakfastHour(sunriseHour, sunriseMinute);
                breakfastMinute = getBreakfastMin(sunriseMinute);
                dinnerHour = getDinnerHour(sunsetHour, sunsetMinute);
                dinnerMinute = getDinnerMin(sunsetMinute);

                // update sunrise, sunset, breakfast, dinner values within SunRSDB object
                sunRSDB.setSunRSDB_Values(Integer.toString(sunriseHour) + ":" + leadingZero(sunriseMinute) + " am",
                        Integer.toString(sunsetHour - 12) + ":" + leadingZero(sunsetMinute) + " pm",
                        Integer.toString(breakfastHour) + ":" + leadingZero(breakfastMinute) + " am",
                        Integer.toString(dinnerHour - 12) + ":" + leadingZero(dinnerMinute) + " pm");

                // update SunRSDB database
                (new Thread(sunRSDB)).start();
            }

            // turtle tank active @ Sunrise
            if(hour == sunriseHour && minute == sunriseMinute && second == 0) {
                GPIO.mainLight.low();
                GPIO.uvbLight.low();
                GPIO.bubbles.low();
                SunRSDB.setNightimeBool(false);
                try {                                                   // post today's routine to Donnie's many followers
                    TwitterPost.postTodaysRoutine();
                } catch (TwitterException e) {
                    System.err.println("Twitter: today's routine not posted");
                }
            }

            // turtle tank not active @ Sunset
            if(hour == sunsetHour && minute == sunsetMinute && second == 0) {
                GPIO.mainLight.high();
                GPIO.uvbLight.high();
                GPIO.bubbles.high();
                SunRSDB.setNightimeBool(true);
                FeederController.setDailyFeedTotal(0);
            }

            // log temps every minute to database
            if(second == 0) {
                tmpDB.setBaskingWaterAir(temperature.getBasking(), temperature.getWater(), temperature.getAir());
                (new Thread(tmpDB)).start();
            }

            // maintain basking below 92 degrees during daytime hrs, air below 80 during night time hrs
            // check every 10 seconds
            if(second % 10 == 0 && GPIO.mainLight.getState() == PinState.LOW) {
                if(temperature.getBasking() > temperature.getTooHotDay() && GPIO.heatLight.getState() == PinState.LOW) GPIO.heatLight.high();
                else if(temperature.getBasking() < temperature.getTooHotDay() && GPIO.heatLight.getState() == PinState.HIGH) GPIO.heatLight.low();
            } else if(second % 10 == 0 && GPIO.mainLight.getState() == PinState.HIGH) {
                if(temperature.getAir() > temperature.getTooHotNight() && GPIO.heatLight.getState() == PinState.LOW) GPIO.heatLight.high();
                else if(temperature.getAir() < temperature.getTooHotNight() && GPIO.heatLight.getState() == PinState.HIGH) GPIO.heatLight.low();
            }

            // maintain water temp of 76 degrees, check every 10 sec
            if(second % 10 == 0) {
                if(temperature.getWater() > temperature.getTooHotWater() && GPIO.waterHeat.getState() == PinState.LOW) GPIO.waterHeat.high();
                else if(temperature.getWater() < temperature.getTooHotWater() && GPIO.waterHeat.getState() == PinState.HIGH) GPIO.waterHeat.low();
            }

            // feed turtle 5 mins after sunrise,
            if(hour == breakfastHour && minute == breakfastMinute && second == 0) {
                System.out.println(FeederController.feederToggle(""));
            }

            // feed turtle 30 mins prior to sunset
            if(hour == dinnerHour && minute == dinnerMinute && second == 0) {
                if(!FeederController.overFed()) {
                    System.out.println(FeederController.feederToggle(""));
                }
            }

            // sleep main for one sec
            Thread.sleep(1000);
        }
    }

    static int getBreakfastHour(int sunriseHour, int sunriseMinute) {
        if(sunriseMinute + 5 > 59) sunriseHour++ ;
        return sunriseHour;
    }

    static int getBreakfastMin(int sunriseMinute) {
        if(sunriseMinute + 5 > 59) sunriseMinute = java.lang.Math.abs(60 - (sunriseMinute + 5));
        else sunriseMinute += 5;
        return sunriseMinute;
    }

    static int getDinnerHour(int sunsetHour, int sunsetMinute) {
        if(sunsetMinute - 30 < 0) sunsetHour-- ;
        return sunsetHour;
    }

    static int getDinnerMin(int sunsetMinute) {
        if(sunsetMinute - 30 < 0) sunsetMinute = 60 + (sunsetMinute - 30);
        else sunsetMinute -= 30;
        return sunsetMinute;
    }

    static String leadingZero(int minuteInt) {
        String minute;
        if (minuteInt < 10) minute = "0" + Integer.toString(minuteInt);
        else minute = Integer.toString(minuteInt);
        return minute;
    }
}