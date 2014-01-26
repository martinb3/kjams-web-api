package org.mbs3.kjams;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.mbs3.kjams.Constants;
import org.mbs3.kjams.model.Playlist;
import org.mbs3.kjams.model.Singer;
import org.mbs3.kjams.model.Song;

import com.dd.plist.*;

public class Service {
	
	private final CloseableHttpClient httpClient;
	private final Map<String,URL> service_endpoints = new HashMap<String,URL>();
	public Service(String url) throws IllegalArgumentException  {
		super();
		
		RequestConfig globalConfig = RequestConfig.custom()
		        .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
		        .build();
		httpClient = HttpClients.custom()
		        .setDefaultRequestConfig(globalConfig)
		        .disableAutomaticRetries()
		        .build();
		
		// strip trailing slashes 
		while(url.endsWith("/")) {
			url = url.substring(0, url.length()-1);
		}
		
		for(String endpoint_modifier : Constants.urls) {
			try {
				service_endpoints.put(endpoint_modifier, new URL(url+endpoint_modifier));
			}
			catch (MalformedURLException ex) {
				throw new IllegalArgumentException(ex);
			}
		}
		
		URL ep = service_endpoints.get(Constants.url_ping);
		if(!Util.doPing(httpClient, ep)) {
			throw new IllegalArgumentException("Invalid url " + ep.toString());	
		}
		
	}
	
	public List<Singer> getSingers() throws Exception {
		ArrayList<Singer> singers = new ArrayList<Singer>();
		
		URL singurl = service_endpoints.get(Constants.url_singers);
		NSDictionary data = (NSDictionary)Util.doPost(httpClient, singurl, null);
		NSArray playlists = (NSArray)data.get("Playlists");
		for(NSObject i : playlists.getArray()) {
			
			// needs a name
			if(!(i instanceof NSDictionary)) 
				continue;
			
			NSDictionary d = (NSDictionary)i;
			if(!(d.containsKey("Name")))
				continue;
			
			NSObject v = d.get("Name");
			if(!(v instanceof NSString))
				continue;
			
			String name = v.toString();
			if(!name.equals("Singers"))
				continue;
			
			NSObject items = d.get("Playlist Items");
			if(!(items instanceof NSArray))
				continue;
			
			for(NSObject singer: ((NSArray)items).getArray()) {
				NSDictionary singerDict = (NSDictionary)singer;

				String SNGR = singerDict.get("SNGR").toString();
				String Spas = singerDict.get("Spas").toString();
				int siID = ((NSNumber)singerDict.get("siID")).intValue();
				singers.add(new Singer(SNGR, Spas, siID));
			}
			
			
		}
		
		
		
		return singers;
	}

	public Singer createNewSinger(String singer, String password, String confirm) throws Exception {
		//POSTDATA=singername=martin2&password=secret&confirm=secret&submit=Jam+Out%21
		
		List<Singer> singers_before = getSingers();
		for(Singer s : singers_before) {
			if(s.SNGR.equals(singer))
				throw new IllegalAccessError("singer " + s + " already exists");
		}
		
		URL newsingurl = service_endpoints.get(Constants.url_newsinger);
		
        List<NameValuePair> data = Arrays.asList(new NameValuePair[]{
        		new BasicNameValuePair("singername", singer),
        		new BasicNameValuePair("password", password),
        		new BasicNameValuePair("confirm", confirm),
        		new BasicNameValuePair("submit", "Jam Out!")
        		});
        
        
		Util.doPost(httpClient, newsingurl, data);
		
		List<Singer> singers = getSingers();
		for(Singer s : singers) {
			if(s.SNGR.equals(singer))
				return s;
		}
		
		throw new IllegalAccessError("could not find singer after creation");
	}
	
	public List<Song> searchLibrary(String term) throws Exception {
		ArrayList<Song> searchResults = new ArrayList<Song>();
		
		URL newsingurl = service_endpoints.get(Constants.url_search);
		List<NameValuePair> data = Arrays.asList(new NameValuePair[]{new BasicNameValuePair("search", term)});
        NSObject response = Util.doPost(httpClient, newsingurl, data);
        
        NSDictionary dictPlaylists = (NSDictionary)response;
        NSArray arrPlaylists = (NSArray)dictPlaylists.get("Playlists");
        
        for(NSObject playlistObject : arrPlaylists.getArray()) {
        	NSDictionary dictPlaylist = (NSDictionary)playlistObject;

        	NSArray playlistItems = (NSArray)dictPlaylist.get("Playlist Items");
        	for(NSObject dictItemObj : playlistItems.getArray()) {
        		NSDictionary dictItem = (NSDictionary)dictItemObj;
        		
        		Song song = new Song(
        				((NSDate)dictItem.get("DAdd")).getDate(),
        				((NSDate)dictItem.get("Date")).getDate(),
        				((NSString)dictItem.get("albm")).getContent(),
        				((NSString)dictItem.get("arts")).getContent(),
        				((NSString)dictItem.get("name")).getContent(),
        				((NSNumber)dictItem.get("piIx")).intValue(),
        				((NSNumber)dictItem.get("soID")).intValue()
        				);
        		
        		searchResults.add(song);
        	}
        }
        
        return searchResults;
	}
	
	public void addToPlaylist(Playlist playlist, Song song) throws Exception {
		URL dropurl = service_endpoints.get(Constants.url_drop);
        
        List<NameValuePair> data = Arrays.asList(new NameValuePair[]{
        		new BasicNameValuePair("playlist", playlist.id+""),
        		new BasicNameValuePair("song", song.soID+""),
        		});
        
        
		Util.doPost(httpClient, dropurl, data, false);
	}

	public void removeFromPlayList(Playlist playlist, int position) throws Exception {
		URL dropurl = service_endpoints.get(Constants.url_remove);
		
        List<NameValuePair> data = Arrays.asList(new NameValuePair[]{
        		new BasicNameValuePair("playlist", playlist.id+""),
        		new BasicNameValuePair("piIx",  position+""),
        		});
        
		Util.doPost(httpClient, dropurl, data, false);
	}
	
	// TODO: reorderPlaylist
	public void reorderPlaylist() {
		
	}
	
	// TODO: pitch on playlists
	public void changePitch() {
		
	}

	public List<Playlist> getPlaylists() throws Exception {
	ArrayList<Playlist> searchResults = new ArrayList<Playlist>();
		
		URL newsingurl = service_endpoints.get(Constants.url_playlists);
        
        NSObject response = Util.doPost(httpClient, newsingurl, null);
        
        NSDictionary dictPlaylists = (NSDictionary)response;
        NSArray arrPlaylists = (NSArray)dictPlaylists.get("Playlists");
        
        for(NSObject playlistObject : arrPlaylists.getArray()) {
        	NSDictionary dictPlaylist = (NSDictionary)playlistObject;

        	NSArray playlistItems = (NSArray)dictPlaylist.get("Playlist Items");
        	for(NSObject dictItemObj : playlistItems.getArray()) {
        		NSDictionary dictItem = (NSDictionary)dictItemObj;
        		
	        	Playlist playlist = new Playlist(
	        				((NSString)dictItem.get("Name")).getContent(),
	        				((NSNumber)dictItem.get("Playlist ID")).intValue(),
	        				((NSNumber)dictItem.get("Playlist Type")).intValue()
	        				);
	        		
	       		searchResults.add(playlist);
        	}
        }
        
        return searchResults;
	}

	public void login(String singer, String password) throws Exception {
		// POSTDATA=singer=558512&password=secret&submit=Login
		
		int sid = -1;
		List<Singer> singers_before = getSingers();
		for(Singer s : singers_before) {
			if(s.SNGR.equals(singer))
				sid = s.siID;
		}
		
		URL newsingurl = service_endpoints.get(Constants.url_main);
       
        List<NameValuePair> data = Arrays.asList(new NameValuePair[]{
        		new BasicNameValuePair("singer", sid+""),
        		new BasicNameValuePair("password", password),
        		new BasicNameValuePair("submit", "Login"),
        		});
        

        //System.out.println(Util.dumpCookies((InternalHttpClient) httpClient));
		Util.doPost(httpClient, newsingurl, data);
		//System.out.println(Util.dumpCookies((InternalHttpClient) httpClient));
	}
	
	public void close() throws IOException {
		httpClient.close();
	}

}
