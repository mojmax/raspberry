package devices.dthnn;

public class Dht11 extends DhtNn {
	private static final long tbeDht11Time = 22000000;
											 										
	public Dht11() {
		super(msSleepDhtNnTime,  tbeDht11Time);
		setName("Dht11");
	}
	
	public Dht11(int ipin) {
		super(ipin, msSleepDhtNnTime,  tbeDht11Time);
		
		setName("Dht11");
	}
	
	
	@Override
	protected DhtNnValues calculateRhTemp(DhtNnValues mis) {
		// TODO Auto-generated method stub
		if (checkParity()) {
			
			mis.setRh( new Double(dhtNNRhInt) );
			mis.setTemp(new Double(dhtNNTempInt));
			return mis;
			
		} else {
			return null;
			
		}
	}

}
