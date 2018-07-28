package devices.dthnn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	
	protected static final long msSleepDhtNnTime = 5000;
	public DhtNnValues mis;
	private int totValidSample = 0;
	private static final int TOTAL_NUM_SAMPLES = 87;
	private static final int FORTY_SAMPLES = 40;
	private List<DhtNnSample> samples;
	protected List<Integer> validSamples;
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
		msSleepTime = msSleepDhtNnTime;
		
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

	protected abstract DhtNnValues calculateRhTemp(DhtNnValues mis);
	public String getName() {
		return this.name;
	};
	public void setName(String name) {
		this.name=name;
	};

	public DhtNnValues getRhTempValues() {
		takeSamples();
	//	printSamples();
		return convertSampleToMisure();
	}

	private void printSamples() {
		
		for (DhtNnSample s : samples) {
			System.out.println("Sample[" + s.getId() + "]" + s);
		}
		StringBuilder str = new StringBuilder();
		for (Integer i: validSamples) {
			str = str.append(i);
		}
		System.out.println("40 Bit" + str );
	}

	private void takeSamples() {
		totValidSample = 0;
		samples = new ArrayList<DhtNnSample>(TOTAL_NUM_SAMPLES);
		validSamples = new ArrayList<Integer>(FORTY_SAMPLES);
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
				samples.add(new DhtNnSample(currentTime - oldTime, currState));
			}
			oldTime = currentTime;
		}
		
		int lastLowValidSample = 0;
		
		Collections.reverse(samples);
		if(samples.get(0).pinState.isHigh()) {
			for (int i = 0; i < samples.size()  ; i++) {
				if ( i % 2 == 1 ) { 
					if ( validSamples.size() < FORTY_SAMPLES ) { 
						validSamples.add(samples.get(i).bitValue);
					}
				}
			}
		}
		Collections.reverse(validSamples);
		Collections.reverse(samples);

	}

	private DhtNnValues convertSampleToMisure() {
		DhtNnValues mis = null;
		dhtNNCheck = dhtNNTempInt = dhtNNTempDec = dhtNNRhInt = dhtNNRhDec = 0;
		if (validSamples.size() != FORTY_SAMPLES) {
			return mis;
		}
		mis = new DhtNnValues();

		takeByte();
		return calculateRhTemp(mis);
	}

	private void takeByte() {
		dhtNNCheck = dhtNNTempInt = dhtNNTempDec = dhtNNRhInt = dhtNNRhDec = 0;

		dhtNNRhInt = validSamples.get(0) << 7 | validSamples.get(1)<< 6 | validSamples.get(2) << 5 | validSamples.get(3) << 4
				| validSamples.get(4) << 3 | validSamples.get(5) << 2 | validSamples.get(6) << 1 | validSamples.get(7);

		dhtNNRhDec = validSamples.get(8) << 7 | validSamples.get(9) << 6 | validSamples.get(10) << 5 | validSamples.get(11) << 4
				| validSamples.get(12) << 3 | validSamples.get(13) << 2 | validSamples.get(14) << 1 | validSamples.get(15);

		dhtNNTempInt = validSamples.get(16) << 7 | validSamples.get(17) << 6 | validSamples.get(18) << 5 | validSamples.get(19) << 4
				| validSamples.get(20) << 3 | validSamples.get(21) << 2 | validSamples.get(22) << 1 | validSamples.get(23);
		dhtNNTempDec = validSamples.get(24) << 7 | validSamples.get(25) << 6 | validSamples.get(26) << 5 | validSamples.get(27) << 4
				| validSamples.get(28) << 3 | validSamples.get(29) << 2 | validSamples.get(30) << 1 | validSamples.get(31);
		dhtNNCheck = validSamples.get(32) << 7 | validSamples.get(33) << 6 | validSamples.get(34) << 5 | validSamples.get(35) << 4
				| validSamples.get(36) << 3 | validSamples.get(37) << 2 | validSamples.get(38) << 1 | validSamples.get(39);

	}

	protected boolean checkParity() {
			
		boolean check = (dhtNNCheck == ((dhtNNRhInt + dhtNNRhDec + dhtNNTempInt + dhtNNTempDec) & 0xFF));
	
		return check;

	}


	private PinState takeNewState() {
		PinState state;
		
		// current time + 200 Micro Sec
		
		long timeLimit = System.nanoTime() ;
		//System.out.println("Wait from  :" + timeLimit);
		timeLimit += 200000; 
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
	
	public List<DhtNnSample> getStats() {
		return samples;
	}
	@Override
	public String toString() {
		return "RH%"+  (mis==null?0:mis.getRh()) + " Temp " + (mis==null?0: mis.getTemp()); 
	}
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(msSleepTime);
			} catch (InterruptedException e) {
			}
			mis = getRhTempValues();
			if ( mis != null )
				System.out.println(getName() +" Misurati   " + mis);
		}
		
	}
}
