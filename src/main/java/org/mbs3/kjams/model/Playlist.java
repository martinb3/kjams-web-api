package org.mbs3.kjams.model;

public class Playlist {
	public final String name;
	public final int id,type;
	public Playlist(String name, int id, int type) {
		super();
		this.name = name;
		this.id = id;
		this.type = type;
	}

	public String toString() {
		return name;
	}
}
