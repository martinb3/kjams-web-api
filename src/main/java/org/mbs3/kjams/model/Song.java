package org.mbs3.kjams.model;

import java.util.Date;

public class Song {
	public Song(Date dAdd, Date date_, String albm, String arts, String name,
			int piIx, int soID) {
		super();
		DAdd = dAdd;
		Date_ = date_;
		this.albm = albm;
		this.arts = arts;
		this.name = name;
		this.piIx = piIx;
		this.soID = soID;
	}
	public final String name,arts,albm;
	public final int soID,piIx;
	public final Date DAdd,Date_;
	
	public String toString() {
		return name;
	}
	
}
