package org.mbs3.kjams.model;

public class Singer {

	public final String SNGR, Spas;
	public final int siID;
	
	public Singer(String sNGR, String spas, int siID) {
		super();
		SNGR = sNGR;
		Spas = spas;
		this.siID = siID;
	}
	
	public String toString() {
		return SNGR;
	}
}
