package devices.dthnn;

public class Dht22 extends DhtNN {
	private static final long tbeDht22Time = 18000000;
	protected int dhtNNRhInt_256, dhtNNRhDec_16, dhtNNRhDec_0, dhtNNTempInt_4096, dhtNNTempInt_256, dhtNNTempDec_16, dhtNNTempDec_0, sign= 0;
	public Dht22() {
		super(tbeDht22Time);
		setName("Dht11");
	}
	
	public Dht22(int ipin) {
		super(ipin,tbeDht22Time);
		super.setName("Dht22");
	}
	
	protected void calculateRhTemp(MisureDhtNN mis) {
			
			dhtNNRhInt_256 		= validSamples[4]  << 3 | validSamples[5]  << 2 | validSamples[6]  << 1 | validSamples[7]  ;
			dhtNNRhDec_16 		= validSamples[8] << 3  | validSamples[9] << 2  | validSamples[10] << 1 | validSamples[11] ; 
			dhtNNRhDec_0 		= validSamples[12] << 3 | validSamples[13] << 2 | validSamples[14] << 1 | validSamples[15];
			dhtNNTempInt_4096 	= validSamples[16] << 3 | validSamples[17] << 2 | validSamples[18] << 1 | validSamples[19] ;
			dhtNNTempInt_256 	= validSamples[20] << 3 | validSamples[21] << 2 | validSamples[22] << 1 | validSamples[23];
			dhtNNTempDec_16 	= validSamples[24] << 3 | validSamples[25] << 2 | validSamples[26] << 1 | validSamples[27] ;
			dhtNNTempDec_0 		= validSamples[28] << 3 | validSamples[29] << 2 | validSamples[30] << 1 | validSamples[31];
			sign = validSamples[16] * -1;
			if (checkParity()) {
				mis.setRh(new Double(dhtNNRhInt_256*256 + dhtNNTempDec_16*16 + dhtNNRhDec_0)/10) ;
				mis.setTemp(new Double(dhtNNTempInt_256*256 + dhtNNTempDec_16*16 + dhtNNTempDec_0)/10);
			}  else {
				mis.setRh(0);
				mis.setTemp(0);
			}
	}
	
}
