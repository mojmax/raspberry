package it.moj.raspi.devices.dhtnn;

import com.pi4j.io.gpio.PinState;

public class SampleDhtNN {

	private static int seq = 0;
	private final int id = seq++;

	public static void setSeq(int seq) {
		SampleDhtNN.seq = seq;
	}

	long durationNanosec;
	int bitValue = 0;
	PinState pinState;

	public SampleDhtNN(long elapsedTime, PinState pinState) {

		this.durationNanosec = elapsedTime;
		// assign the value of the current bit
		this.pinState = pinState;
		if (pinState.isLow()) {
			bitValue = (elapsedTime < 40000 ? 0 : 1);
		}

	}

	public String toString() {
		return " id : " + id + " captured at level " + pinState.getValue() + " bit" + bitValue + " duration "
				+ durationNanosec;
	}
}
