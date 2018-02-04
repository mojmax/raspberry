package it.moj.raspi.devices;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

public class RotaryEncoder {
	private GpioPinDigitalInput pinDt;
	private GpioPinDigitalInput pinClk;
	private GpioPinDigitalInput pinMs;
	private static final Pin default_Pin_Dt = RaspiPin.GPIO_00;
	private static final Pin default_Pin_Clk = RaspiPin.GPIO_01;
	private static final Pin default_Pin_Ms = RaspiPin.GPIO_02;
	private int oldClk = 0;
	private int oldDt = 0;
	private int[] sample = { 0, 0, 0, 0 };

	public RotaryEncoder(GpioController gpio, Pin pinClk, Pin pinDt) {
		this.pinDt = gpio.provisionDigitalInputPin(pinDt);
		this.pinClk = gpio.provisionDigitalInputPin(pinClk);
	}

	public boolean isClockWise1() {
		return (pinDt.isHigh() && pinClk.isHigh());

	}

	public boolean isCounterClockWise() {
		return (pinDt.isHigh() && pinClk.isLow());
	}

	public int valuePinDT() {
		int rt = 0;
		if (pinDt.isLow()) {
			rt = 0;
		} else if (pinDt.isHigh()) {
			rt = 1;
		}
		return rt;
	}

	public int valuePinClk() {
		int rt = 0;
		if (pinClk.isLow()) {
			rt = 0;
		} else if (pinClk.isHigh()) {
			rt = 1;
		}
		return rt;
	}

	public int getCange() {
		int ret = 0;

		int clk = valuePinClk(); // MSB = most significant bit
		int dt = valuePinDT(); // LSB = least significant bit
		
		// nothing Changes
		if (clk == oldClk && dt == oldDt) {
			
			ret = 0;

		} else {
			// Sometings Changes
			System.out.println("clk: " + clk + " DT: " + dt);
			int current = (clk << 1) | dt; // converting the 2 pin value to
											// single binary number
			sample[0] = sample[1];
			sample[1] = sample[2];
			sample[2] = sample[3];
			sample[3] = current;
			int sequence = current << 6 | sample[2] << 4 | sample[1] << 2 | sample[0];
		//	if (sequence == 0b10000111) {
			if (sequence == 135) {
				System.out.println("turn right");
				ret = +1;
			}
		//	if (sequence == 0b01001011) {
			if (sequence == 75) {
				System.out.println("turn left");
				ret = -1;

			}
			oldClk = clk;
			oldDt = dt;
		}

		return ret;
	}

}
