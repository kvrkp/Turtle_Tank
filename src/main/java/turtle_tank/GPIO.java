package turtle_tank;

import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.*;

public class GPIO {
    // create gpio controller instance, always required
    final static GpioController gpio = GpioFactory.getInstance();

    // provision gpio pins #22,#23,#24,#25,#26,#27,#28,#29 as output pins, set defaults -- relay board
    final static GpioPinDigitalOutput mainLight = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, PinState.HIGH);
    final static GpioPinDigitalOutput uvbLight = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, PinState.HIGH);
    final static GpioPinDigitalOutput heatLight = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW);
    final static GpioPinDigitalOutput bubbles = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.HIGH);
    final static GpioPinDigitalOutput waterHeat = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_26, PinState.LOW);
    final static GpioPinDigitalOutput feeder = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, PinState.LOW);
    final static GpioPinDigitalOutput waterSwitch = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, PinState.HIGH);
    // final GpioPinDigitalOutput DEAD = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, PinState.LOW);

    // provision gpio pin #0 as an input pin with its internal pull down resistor enabled -- light on/off button
    final static GpioPinDigitalInput buttonLights = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);
    final static GpioPinDigitalInput buttonFloat = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
    final static GpioPinDigitalInput buttonMotionDock = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

    // provision gpio pins #3,#4,#5,#6 as output pins, set defaults -- stepper motor/feeder
    final static GpioPinDigitalOutput[] pins = {gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW)};

    // create motor component -- stepper motor/feeder
    final static GpioStepperMotorComponent motor = new GpioStepperMotorComponent(GPIO.pins);

    // stepper motor sequence -- stepper motor/feeder
    final static byte[] sequence = {0b0001, 0b0011, 0b0010, 0b0110, 0b0100, 0b1100, 0b1000, 0b1001};
}