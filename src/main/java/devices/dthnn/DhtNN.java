package devices.dthnn;

import com.pi4j.component.ObserveableComponentBase;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public abstract class DhtNN extends ObserveableComponentBase {

	private int validSample = 0;
	private static final int TOTAL_NUM_SAMPLES = 83;
	SampleDhtNN[] samples;

	private PinState currState;
	private PinState lastState;
	
	private static final Pin DEFAULT_PIN = RaspiPin.GPIO_07;

	protected int dhtNNRhInt, dhtNNRhDec, dhtNNTempInt, dhtNNTempDec, dhtNNCheck = 0;
	
	private GpioPinDigitalMultipurpose dht11Pin;

	public DhtNN() {
		final GpioController gpio = GpioFactory.getInstance();
		dht11Pin = gpio.provisionDigitalMultipurposePin(DEFAULT_PIN, PinMode.DIGITAL_INPUT, PinPullResistance.PULL_UP);

	}

	public DhtNN(int pin) {
		Pin rPin = RaspiPin.getPinByAddress(pin);
		final GpioController gpio = GpioFactory.getInstance();
		dht11Pin = gpio.provisionDigitalMultipurposePin(rPin, PinMode.DIGITAL_INPUT, PinPullResistance.PULL_UP);
	}
	protected abstract void calculateRhTemp();
	public MisureDhtNN getRhTempValues() {
		takeSamples();
		printSamples();
		return convertSampleToMisure();

	}

	public void printSamples() {
		int i = 0;
		for (SampleDhtNN s : samples) {
			System.out.println("Sample[" + (i++) + "]" + s);

		}
	}

	private void takeSamples() {
		validSample = 0;
		samples = new SampleDhtNN[TOTAL_NUM_SAMPLES];

		dht11Pin.setMode(PinMode.DIGITAL_OUTPUT);
		dht11Pin.low();
		waitNanosecond(20000000); // at leas 18 ms
		dht11Pin.setMode(PinMode.DIGITAL_INPUT); // change Mode of DHT11 now
													// Input

		long currentTime = System.nanoTime();
		long oldTime = currentTime;
		lastState = dht11Pin.getState();

		SampleDhtNN.setSeq(0);
		for (int i = 0; i < TOTAL_NUM_SAMPLES; i++) {
			currState = takeNewState();
			currentTime = System.nanoTime();
			if (currState != null && (currState.isLow() || currState.isHigh())) {
				samples[i] = new SampleDhtNN(currentTime - oldTime, currState);
			}
			oldTime = currentTime;
		}
	}

	private MisureDhtNN convertSampleToMisure() {
		MisureDhtNN mis = new MisureDhtNN();
		dhtNNCheck = dhtNNTempInt = dhtNNTempDec = dhtNNRhInt = dhtNNRhDec = 0;
		if (samples.length != TOTAL_NUM_SAMPLES) {
			System.out.println("Sample ERROR  - nume samples " + samples.length);
			return mis;
		}
		System.out.println("valid Sample  " + validSample);
		if (validSample != TOTAL_NUM_SAMPLES) {
			System.out.println("valid Sample ERROR  " + validSample);
			return mis;
		}

		calculateRhTemp();

		System.out.println("dht11RhInt     " + dhtNNRhInt);
		System.out.println("dht11RhDec    " + dhtNNRhDec);
		System.out.println("dht11TempInt   " + dhtNNTempInt);
		System.out.println("dht11TempDec    " + dhtNNTempDec);
		System.out.println("dht11Check    " + dhtNNCheck);

		if (checkParity()) {
			mis.setRh(
					Double.parseDouble(new Integer(dhtNNRhInt).toString() + "." + new Integer(dhtNNRhDec).toString()));
			mis.setTemp(Double
					.parseDouble(new Integer(dhtNNTempInt).toString() + "." + new Integer(dhtNNTempDec).toString()));
			System.out.println("check Ok ");
		} else {
			System.out.println("check ERROR ");
		}

		return mis;
	}

	 
	private boolean checkParity() {
		if (getName().equals(NameDhtNN.DHT11.getName()))
			return dhtNNCheck == ((dhtNNRhInt + dhtNNRhDec + dhtNNTempInt + dhtNNTempDec) & 0xFF);
		else
			return false;
	}

	private PinState takeNewState() {
		PinState state;
		int maxSample = 0;
		do {
			state = dht11Pin.getState();
			// wait 1 microsecond
			waitNanosecond(1000);

			maxSample++;
		} while (state == lastState && (maxSample < 500));
		lastState = state;
		if (maxSample >= 500)
			return null;
		validSample++;
		return state;

	}

	private void waitNanosecond(long nano) {
		long waitUntil = System.nanoTime() + nano;
		while (waitUntil > System.nanoTime()) {
		}
	}

	public SampleDhtNN[] getStats() {
		return samples;
	}
}
