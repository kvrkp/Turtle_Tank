package turtle_tank;

import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.*;

public class GPIO {
    // var to count how many times waterSwitch has turned on
    private static volatile int waterSwitchCount  = 0;
    private static volatile boolean twitterTimeOut = false;

    // create gpio controller instance, always required
    final static GpioController gpio = GpioFactory.getInstance();

    // provision gpio pins #22,#23,#24,#25,#26,#27,#28,#29 as output pins, set defaults -- relay board
    final static GpioPinDigitalOutput mainLight = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, PinState.HIGH);
    final static GpioPinDigitalOutput uvbLight = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, PinState.HIGH);
    final static GpioPinDigitalOutput heatLight = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.HIGH);
    final static GpioPinDigitalOutput bubbles = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.HIGH);
    final static GpioPinDigitalOutput waterHeat = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_26, PinState.HIGH);
    final static GpioPinDigitalOutput waterSwitch = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, PinState.HIGH);
    // final static GpioPinDigitalOutput DEAD = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, PinState.HIGH);
    // final static GpioPinDigitalOutput DEAD = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, PinState.HIGH);

    // provision gpio pin #0 as an input pin with its internal pull down resistor enabled -- light on/off button
    final static GpioPinDigitalInput buttonLights = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);
    final static GpioPinDigitalInput buttonFloat = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
    final static GpioPinDigitalInput buttonWaterDetected = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP);

    // provision gpio pins #3,#4,#5,#6 as output pins, set defaults -- stepper motor/feeder
    final static GpioPinDigitalOutput[] pins = {gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW)};

    // create motor component -- stepper motor/feeder
    final static GpioStepperMotorComponent motor = new GpioStepperMotorComponent(GPIO.pins);

    // stepper motor sequence -- stepper motor/feeder
    final static byte[] sequence = {0b0001, 0b0011, 0b0010, 0b0110, 0b0100, 0b1100, 0b1000, 0b1001};

    static int getWaterSwitchCount() {
        return waterSwitchCount;
    }

    static void setWaterSwitchCount(int _waterSwitchCount) {
        GPIO.waterSwitchCount = _waterSwitchCount;
    }

    static boolean isTwitterTimeOut() {
        return twitterTimeOut;
    }

    static void setTwitterTimeOut(boolean _twitterTimeOut) {
        GPIO.twitterTimeOut = _twitterTimeOut;
    }
}