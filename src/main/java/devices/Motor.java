package devices;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;

public class Motor {

	private int value = 0;
	private static final int PIN_PWM_DEFAULT = 0;
	private static final int LOW_VALUE = 0;
	private static final int HIGH_VALUE = 100;
	private static final Pin PIN_1A_DEFAULT = RaspiPin.GPIO_12;
	private static final Pin PIN_2A_DEFAULT = RaspiPin.GPIO_13;
	private int pinPwm = PIN_PWM_DEFAULT;
	private boolean isRight = true;
	private GpioPinDigitalOutput gpioPin1A;
	private GpioPinDigitalOutput gpioPin2A;

	public Motor() {
		pinPwmSetup(PIN_PWM_DEFAULT, HIGH_VALUE);
	}

	public Motor(GpioController gpio) {
		pinPwmSetup(PIN_PWM_DEFAULT, HIGH_VALUE);
		this.gpioPin1A = gpio.provisionDigitalOutputPin(PIN_1A_DEFAULT);
		this.gpioPin2A = gpio.provisionDigitalOutputPin(PIN_2A_DEFAULT);
		goRight();
	}

	public Motor(GpioController gpio, int pinPwm) {
		pinPwmSetup(pinPwm, HIGH_VALUE);
		this.gpioPin1A = gpio.provisionDigitalOutputPin(PIN_1A_DEFAULT);
		this.gpioPin2A = gpio.provisionDigitalOutputPin(PIN_2A_DEFAULT);
		goRight();
	}

	public Motor(GpioController gpio, int pinPwm, Pin pin1A, Pin pin2A) {
		pinPwmSetup(pinPwm, HIGH_VALUE);
		this.gpioPin1A = gpio.provisionDigitalOutputPin(pin1A);
		this.gpioPin2A = gpio.provisionDigitalOutputPin(pin2A);
		goRight();
	}

	private void pinPwmSetup(int pinPwm, int value) {
		this.pinPwm = pinPwm;
		this.value = value;
		Gpio.wiringPiSetup();
		SoftPwm.softPwmCreate(pinPwm, LOW_VALUE, HIGH_VALUE);
		SoftPwm.softPwmWrite(pinPwm, value);
		
	}
	public void toggleDirection() {
		if (isRight) {
			goLeft();
		} else { 
			goRight();
		}
	}
	public void goRight() {
		this.gpioPin1A.high();
		this.gpioPin2A.low();
		isRight = true;
	}
	public void goLeft() {
		this.gpioPin1A.low();
		this.gpioPin2A.high();
		isRight = false;
	}
	public void setMaxSpeed() {
		this.value = HIGH_VALUE;
		SoftPwm.softPwmWrite(pinPwm, value);
		
	}
	public void stop() {
		this.value = LOW_VALUE;
		SoftPwm.softPwmWrite(pinPwm, value);
		
	}
	public void setSpeed(int val) {
		if ( val >= 0 && val <= 100 ) {
			this.value = val;
			SoftPwm.softPwmWrite(pinPwm, value);
		}
		
	}
	public void printStatus() {
		if ( isRight ) { 
			System.out.println("go Right"); 
		} else  {
			System.out.println("go Left");	
		}	
		
	}
	

}
