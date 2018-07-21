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

public class DhtNN extends ObserveableComponentBase {

	private int validSample = 0;
	private static final int TOTAL_NUM_SAMPLES = 83;
	SampleDhtNN[] samples;

	private PinState currState;
	private PinState lastState;
	// private int[] states = new int[1000];
	private static final Pin DEFAULT_PIN = RaspiPin.GPIO_07;

	private int dht11RhInt, dht11RhDec, dht11TempInt, dht11TempDec, dht11Check = 0;
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

	public MisureDthNN getRhTempValues() {
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

	private MisureDthNN convertSampleToMisure() {
		MisureDthNN mis = new MisureDthNN();
		dht11Check = dht11TempInt = dht11TempDec = dht11RhInt = dht11RhDec = 0;
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

		System.out.println("dht11RhInt     " + dht11RhInt);
		System.out.println("dht11RhDec    " + dht11RhDec);
		System.out.println("dht11TempInt   " + dht11TempInt);
		System.out.println("dht11TempDec    " + dht11TempDec);
		System.out.println("dht11Check    " + dht11Check);

		if (checkParity()) {
			mis.setRh(
					Double.parseDouble(new Integer(dht11RhInt).toString() + "." + new Integer(dht11RhDec).toString()));
			mis.setTemp(Double
					.parseDouble(new Integer(dht11TempInt).toString() + "." + new Integer(dht11TempDec).toString()));
			System.out.println("check Ok ");
		} else {
			System.out.println("check ERROR ");
		}

		return mis;
	}

	private void calculateRhTemp() {
		if (getName().equals(NameDhtNN.DHT11.getName())) {
			dht11Check = dht11TempInt = dht11TempDec = dht11RhInt = dht11RhDec = 0;

			dht11RhInt = samples[3].bitValue << 7 | samples[5].bitValue << 6 | samples[7].bitValue << 5
					| samples[9].bitValue << 4 | samples[11].bitValue << 3 | samples[13].bitValue << 2
					| samples[15].bitValue << 1 | samples[17].bitValue;
			dht11RhDec = samples[19].bitValue << 7 | samples[21].bitValue << 6 | samples[23].bitValue << 5
					| samples[25].bitValue << 4 | samples[27].bitValue << 3 | samples[29].bitValue << 2
					| samples[31].bitValue << 1 | samples[33].bitValue;
			dht11TempInt = samples[35].bitValue << 7 | samples[37].bitValue << 6 | samples[39].bitValue << 5
					| samples[41].bitValue << 4 | samples[43].bitValue << 3 | samples[45].bitValue << 2
					| samples[47].bitValue << 1 | samples[49].bitValue;
			dht11TempDec = samples[51].bitValue << 7 | samples[53].bitValue << 6 | samples[55].bitValue << 5
					| samples[57].bitValue << 4 | samples[59].bitValue << 3 | samples[61].bitValue << 2
					| samples[63].bitValue << 1 | samples[65].bitValue;
			dht11Check = samples[67].bitValue << 7 | samples[69].bitValue << 6 | samples[71].bitValue << 5
					| samples[73].bitValue << 4 | samples[75].bitValue << 3 | samples[77].bitValue << 2
					| samples[79].bitValue << 1 | samples[81].bitValue;
		}

	}

	private boolean checkParity() {
		if (getName().equals(NameDhtNN.DHT11.getName()))
			return dht11Check == ((dht11RhInt + dht11RhDec + dht11TempInt + dht11TempDec) & 0xFF);
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
