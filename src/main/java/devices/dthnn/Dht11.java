package devices.dthnn;

public class Dht11 extends DhtNN {
	
	protected void calculateRhTemp() {
		
			dhtNNCheck = dhtNNTempInt = dhtNNTempDec = dhtNNRhInt = dhtNNRhDec = 0;

			dhtNNRhInt = samples[3].bitValue << 7 | samples[5].bitValue << 6 | samples[7].bitValue << 5
					| samples[9].bitValue << 4 | samples[11].bitValue << 3 | samples[13].bitValue << 2
					| samples[15].bitValue << 1 | samples[17].bitValue;
			dhtNNRhDec = samples[19].bitValue << 7 | samples[21].bitValue << 6 | samples[23].bitValue << 5
					| samples[25].bitValue << 4 | samples[27].bitValue << 3 | samples[29].bitValue << 2
					| samples[31].bitValue << 1 | samples[33].bitValue;
			dhtNNTempInt = samples[35].bitValue << 7 | samples[37].bitValue << 6 | samples[39].bitValue << 5
					| samples[41].bitValue << 4 | samples[43].bitValue << 3 | samples[45].bitValue << 2
					| samples[47].bitValue << 1 | samples[49].bitValue;
			dhtNNTempDec = samples[51].bitValue << 7 | samples[53].bitValue << 6 | samples[55].bitValue << 5
					| samples[57].bitValue << 4 | samples[59].bitValue << 3 | samples[61].bitValue << 2
					| samples[63].bitValue << 1 | samples[65].bitValue;
			dhtNNCheck = samples[67].bitValue << 7 | samples[69].bitValue << 6 | samples[71].bitValue << 5
					| samples[73].bitValue << 4 | samples[75].bitValue << 3 | samples[77].bitValue << 2
					| samples[79].bitValue << 1 | samples[81].bitValue;
		

	}

}
