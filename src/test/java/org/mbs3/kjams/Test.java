package org.mbs3.kjams;

import java.util.List;
import java.util.UUID;

import org.mbs3.kjams.model.Playlist;
import org.mbs3.kjams.model.Song;

public class Test {

	public static void main(String[] args) throws Exception {
		// no discovery yet, this will only work if you know the server URL
		String url = "http://localhost:8080/";
		Service svc = new Service(url);
		
		// list singers
		System.out.println("Singers that already exist: " + svc.getSingers());

		// create our own singer
		String SNGR = "test"+UUID.randomUUID().toString().substring(0, 5);
		//String SNGR="test9e93b";
		System.out.println(svc.createNewSinger(SNGR, "secret", "secret"));
		
		// authenticate as new singer
		svc.login(SNGR, "secret");
		
		// search library
		List<Song> songs = svc.searchLibrary("mary");
		System.out.println("Song search results: " + songs);
		
		// list playlists
		List<Playlist> playlists = svc.getPlaylists();
		System.out.println("Existing playlists: " + playlists);
		
		Playlist tonight = null;
		for(Playlist p : playlists) {
			if(p.name.equals("Tonight"))
				tonight = p;
		}
		
		// add a song to the playlist
		svc.addToPlaylist(tonight, songs.get(0));
		
		// remove a song from the playlist
		svc.removeFromPlayList(tonight, 1);
		
		// TODO: still need to implement pitch, reorder, and logout
	}

}
