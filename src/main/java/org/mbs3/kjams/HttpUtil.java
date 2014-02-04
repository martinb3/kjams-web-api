package org.mbs3.kjams;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.mbs3.kjams.model.NameValuePair;

import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

public class HttpUtil {
	private static Object _lock = new Object();
	private static HttpUtil _ref;
	public static HttpUtil getInstance() {
		synchronized(_lock) {
			if(_ref == null) {
				_ref = new HttpUtil();
			}
		}
		return _ref;
	}
	
	private final CookieManager cookieManager;
	private HttpUtil() {
		cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(cookieManager);
	}
	
	public boolean doPing(URL url) {
	    try {
	    	doPost(url, null);
	        
	        // don't even worry about it, kJams doesn't return
	        return true;
	        
	        //return (200 <= responseCode && responseCode <= 399);
	    } catch (Exception ex) {
	        return true;
	    }
	}
	
	
	
	public NSObject doPost(URL url, List<NameValuePair> values) throws Exception {
		return doPost(url, values, true);
	}
	
	public NSObject doPost(URL url, List<NameValuePair> values, boolean expectingResponse) throws Exception {
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setDoOutput(true);
		urlConnection.setRequestMethod("POST");

		List<HttpCookie> cooks = cookieManager.getCookieStore().getCookies();
		System.out.println("Found " + cooks.size() + " cookies");
		for(HttpCookie cookie : cooks) {
			System.out.println(cookie);
		}
		
        if(values != null) {
        	OutputStream os = urlConnection.getOutputStream();
        	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        	writer.write(getQuery(values));
        	writer.flush();
        }
        
        urlConnection.connect();
        
		
        String responseStream = null;
        try {
	        
	        InputStream is = urlConnection.getInputStream();
	        responseStream = Util.getStringFromInputStream(is);
	        
			int retcode = urlConnection.getResponseCode();
			if(retcode != 200)
				throw new IllegalAccessError("create returned " + retcode);
        } 
        catch (Exception ex) {
        	if(expectingResponse)
        		throw ex;
        }
        
		NSObject o = null;
		try {
			if(responseStream != null && responseStream.contains("DOCTYPE plist")) {
				o = PropertyListParser.parse(responseStream.getBytes());
			}
		}
		catch (Exception ex) {
			// don't care, doesn't always return
			ex.printStackTrace();
		}
		
		return o;
	}

	/*public static NSObject _getData(Session session, URL url) throws IOException, Exception {
		String type = "application/x-www-form-urlencoded";
		String encodedData= "";
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setDoOutput(true);
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", type );
		conn.setRequestProperty( "Content-Length", String.valueOf(encodedData.length()));
		
		OutputStream os = conn.getOutputStream();
		os.write( encodedData.getBytes() );
		
		NSObject o = PropertyListParser.parse(conn.getInputStream());
		return o;
	}*/
	
	private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : params)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue().toString(), "UTF-8"));
	    }

	    return result.toString();
	}

}
