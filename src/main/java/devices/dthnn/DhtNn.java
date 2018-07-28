package devices.dthnn;

import java.util.concurrent.TimeUnit;

import com.pi4j.component.ObserveableComponentBase;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public abstract class DhtNn extends ObserveableComponentBase implements Runnable {
	
	protected static final long msSleepDht22Time = 5000;
	public DhtNnValues mis;
	private int totValidSample = 0;
	private static final int TOTAL_NUM_SAMPLES = 87;
	private static final int FORTY_SAMPLES = 40;
	private DhtNnSample[] samples;
	protected int[] validSamples;
	private long tbeTime;
	private PinState currState;
	private PinState lastState;
	private static final Pin DEFAULT_PIN = RaspiPin.GPIO_07;
	private String name;
	private long msSleepTime = 5000; 

	private Pin pin = DEFAULT_PIN;

	private final GpioController gpio;

	{
		gpio = GpioFactory.getInstance();
	};

	protected int dhtNNRhInt, dhtNNRhDec, dhtNNTempInt, dhtNNTempDec, dhtNNCheck = 0;

	private GpioPinDigitalMultipurpose dht11Pin;

	private DhtNn() {
		setPin(pin);
		msSleepTime = msSleepDht22Time;
		
	}
	public DhtNn(long msSleepTime) {
		this();
		this.msSleepTime = msSleepTime;
	}
	public DhtNn(long msSleepTime, long tbeTime) {
		this();
		this.msSleepTime = msSleepTime;
		this.tbeTime = tbeTime;
	}
	public DhtNn(int ipin,long msSleepTime, long tbeTime) {
		this.msSleepTime = msSleepTime;
		this.tbeTime = tbeTime;
		Pin pin = RaspiPin.getPinByAddress(ipin);
		setPin(pin);
	}
	public DhtNn(int ipin, long msSleepTime) {
		this.msSleepTime = msSleepTime;
		Pin pin = RaspiPin.getPinByAddress(ipin);
		setPin(pin);
	}

	private void setPin(Pin pin) {
		dht11Pin = gpio.provisionDigitalMultipurposePin(pin, PinMode.DIGITAL_INPUT);
	}

	protected abstract void calculateRhTemp(DhtNnValues mis);
	public String getName() {
		return this.name;
	};
	public void setName(String name) {
		this.name=name;
	};

	public DhtNnValues getRhTempValues() {
		takeSamples();
		// printSamples();
		return convertSampleToMisure();

	}

	private void printSamples() {
		int i = 0;
		for (DhtNnSample s : samples) {
			System.out.println("Sample[" + (i++) + "]" + s);
		}
	}

	private void takeSamples() {
		totValidSample = 0;
		samples = new DhtNnSample[TOTAL_NUM_SAMPLES];
		validSamples = new int[FORTY_SAMPLES];
		dht11Pin.setMode(PinMode.DIGITAL_OUTPUT);
		lastState = PinState.LOW;
		dht11Pin.low();

		waitNanosecond(tbeTime); //
		dht11Pin.setMode(PinMode.DIGITAL_INPUT); // change Mode of DHTNN
													// Now is Input

		long currentTime = System.nanoTime();
		long oldTime = currentTime;

		DhtNnSample.setSeq(0);
		for (int i = 0; i < TOTAL_NUM_SAMPLES; i++) {
			currState = takeNewState();

			if (currState != null && (currState.isLow() || currState.isHigh())) {
				currentTime = System.nanoTime();
				samples[i] = new DhtNnSample(currentTime - oldTime, currState);
			}
			oldTime = currentTime;
		}
		
		int lastLowValidSample = 0;
		for (int i = TOTAL_NUM_SAMPLES - 1; i >= 0; i--) {
			if (samples[i] != null) {
				if (samples[i].bitValue == 0) {
					lastLowValidSample = i;
					// 
					i = -1;
				}
			}
		}
		printSamples();
		System.out.println("lastLowValidSample "+lastLowValidSample);
		if (lastLowValidSample > 1) {
			for (int i = lastLowValidSample - 1, x = FORTY_SAMPLES - 1; i >= 0; i = i - 2) {
				if (x > -1) {
					if (samples[i] != null)  
						validSamples[x--] = samples[i].bitValue;
				}
				totValidSample++;
				if (totValidSample == FORTY_SAMPLES) {
					i = -1;
				}
			}
		}
	}

	private DhtNnValues convertSampleToMisure() {
		DhtNnValues mis = null;
		dhtNNCheck = dhtNNTempInt = dhtNNTempDec = dhtNNRhInt = dhtNNRhDec = 0;
		if (samples.length != TOTAL_NUM_SAMPLES) {
			System.out.println("Sample ERROR  - nume samples " + samples.length);
			return mis;
		}
		// System.out.println("valid Sample " + validSample);
		if (totValidSample != FORTY_SAMPLES) {
			System.out.println("valid Sample ERROR  " + totValidSample);
			return mis;
		}
		mis = new DhtNnValues();

		takeByte();
		calculateRhTemp(mis);

		// System.out.println("dht11RhInt " + dhtNNRhInt + " "
		// +Integer.toBinaryString(dhtNNRhInt));
		// System.out.println("dht11RhDec " + dhtNNRhDec + " " +
		// Integer.toBinaryString(dhtNNRhDec));
		// System.out.println("dht11TempInt " + dhtNNTempInt + " " +
		// Integer.toBinaryString(dhtNNTempInt));
		// System.out.println("dht11TempDec " + dhtNNTempDec + " "
		// +Integer.toBinaryString(dhtNNTempDec));
		// System.out.println("dht11Check " + dhtNNCheck + " "
		// +Integer.toBinaryString(dhtNNCheck));

		return mis;
	}

	private void takeByte() {
		dhtNNCheck = dhtNNTempInt = dhtNNTempDec = dhtNNRhInt = dhtNNRhDec = 0;

		dhtNNRhInt = validSamples[0] << 7 | validSamples[1] << 6 | validSamples[2] << 5 | validSamples[3] << 4
				| validSamples[4] << 3 | validSamples[5] << 2 | validSamples[6] << 1 | validSamples[7];

		dhtNNRhDec = validSamples[8] << 7 | validSamples[9] << 6 | validSamples[10] << 5 | validSamples[11] << 4
				| validSamples[12] << 3 | validSamples[13] << 2 | validSamples[14] << 1 | validSamples[15];

		dhtNNTempInt = validSamples[16] << 7 | validSamples[17] << 6 | validSamples[18] << 5 | validSamples[19] << 4
				| validSamples[20] << 3 | validSamples[21] << 2 | validSamples[22] << 1 | validSamples[23];
		dhtNNTempDec = validSamples[24] << 7 | validSamples[25] << 6 | validSamples[26] << 5 | validSamples[27] << 4
				| validSamples[28] << 3 | validSamples[29] << 2 | validSamples[30] << 1 | validSamples[31];
		dhtNNCheck = validSamples[32] << 7 | validSamples[33] << 6 | validSamples[34] << 5 | validSamples[35] << 4
				| validSamples[36] << 3 | validSamples[37] << 2 | validSamples[38] << 1 | validSamples[39];

	}

	protected boolean checkParity() {
		StringBuilder str = new StringBuilder();

		for (int i = 0; i < FORTY_SAMPLES; i++) {
			str = str.append(new Integer(validSamples[i]));
		}
		boolean check = (dhtNNCheck == ((dhtNNRhInt + dhtNNRhDec + dhtNNTempInt + dhtNNTempDec) & 0xFF));
		System.out.println("40 Bit" + str + " is check passed ? " + check);
		return check;

	}
	private PinState takeNewStateOld() {
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
//		validSample++;
		return state;

	}

	private PinState takeNewState() {
		PinState state;
		
		// current time + 200 Micro Sec
		
		long timeLimit = System.nanoTime() ;
		//System.out.println("Wait from  :" + timeLimit);
		timeLimit += 400000; 
		//System.out.println("Wait until  :" + timeLimit);
		boolean exitLoop = false;
		
		do {
			state = dht11Pin.getState();
		} while (state == lastState && !(exitLoop = (System.nanoTime() > timeLimit ? true : false)));
		
		lastState = state;
		
		if (exitLoop) {
			return null;
		} else {
			return state;
		}

	}
	
	private void waitNanosecond(long nano) {
		long waitUntil = System.nanoTime() + nano;
		while (waitUntil > System.nanoTime()) {
		}
	}
	
	public DhtNnSample[] getStats() {
		return samples;
	}
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(msSleepTime);
			} catch (InterruptedException e) {
			}
			mis = getRhTempValues();
			System.out.println(getName() +" Misurati   " + mis);
		}
		
	}
}
