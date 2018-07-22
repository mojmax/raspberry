package devices.dthnn;

public class Dht11 extends DhtNN {
	private static final long tbeDht11Time = 20000000;

	public Dht11() {
		super(tbeDht11Time);
	}
	
	public Dht11(int ipin) {
		super(ipin,tbeDht11Time);
	}
	
	
	@Override
	protected void calculateRhTemp(MisureDhtNN mis) {
		// TODO Auto-generated method stub
		if (checkParity()) {
			mis.setRh(
					Double.parseDouble(new Integer(dhtNNRhInt).toString() + "." + new Integer(dhtNNRhDec).toString()));
			mis.setTemp(Double
					.parseDouble(new Integer(dhtNNTempInt).toString() + "." + new Integer(dhtNNTempDec).toString()));
			System.out.println("check Ok ");
		} else {
			System.out.println("check ERROR ");
		}
	}

}
