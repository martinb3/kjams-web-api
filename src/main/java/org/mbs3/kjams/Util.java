package org.mbs3.kjams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.dd.plist.*;

public class Util {
	public static boolean doPing(CloseableHttpClient hc, URL url) {
	    try {
	    	doPost(hc, url, null);
	        
	        // don't even worry about it, kJams doesn't return
	        return true;
	        
	        //return (200 <= responseCode && responseCode <= 399);
	    } catch (Exception ex) {
	        return true;
	    }
	}
	
	
	
	public static NSObject doPost(CloseableHttpClient hc, URL url, List<NameValuePair> values) throws Exception {
		return doPost(hc, url, values, true);
	}
	
	public static NSObject doPost(CloseableHttpClient hc, URL url, List<NameValuePair> values, boolean expectingResponse) throws Exception {
		
		HttpPost method = new HttpPost(url.toString());
		
        if(values != null)
        	method.setEntity(new UrlEncodedFormEntity(values, Consts.UTF_8));
		
        String responseStream = null;
        try {
	        HttpResponse response = hc.execute(method);
	        HttpEntity entity = response.getEntity();
	        
	        InputStream is = entity.getContent();
	        responseStream = getStringFromInputStream(is);
	        
	        EntityUtils.consume(entity);
	        
	        
			int retcode = response.getStatusLine().getStatusCode();
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
	
	public static String dumpNSObject(Object obj) {
		if(obj == null)
			return null;
		
		if(!(obj instanceof NSObject)) {
			return obj.toString();
		}
		
		StringBuilder sb = new StringBuilder();
		
		if(obj instanceof NSArray) {
			NSArray arr = (NSArray)obj;
			
			sb.append("Array(\n");
			for(NSObject i : arr.getArray())
				sb.append("\t"+dumpNSObject(i)+",\n");
			sb.append("\t)\n");
			
		}
		else if(obj instanceof NSData) {
			NSData d = (NSData)obj;
			sb.append(d.getBase64EncodedData());
		}
		else if(obj instanceof NSDate) {
			NSDate d = (NSDate)obj;
			sb.append(d.getDate().toString());
		}
		else if(obj instanceof NSDictionary) {
			NSDictionary dict = (NSDictionary)obj;
			
			sb.append("Dict(\n");
			for(@SuppressWarnings("rawtypes") Map.Entry entry : dict.getHashMap().entrySet()) {
				sb.append("\tKey("+dumpNSObject(entry.getKey())+")=>Value("+dumpNSObject(entry.getValue())+"),\n");
			}
			sb.append("\t)\n");
		}
		else if(obj instanceof NSNumber) {
			NSNumber str = (NSNumber)obj;
			if(str.isInteger())
				sb.append(str.intValue());
			else if(str.isReal())
				sb.append(str.doubleValue());
			else if(str.isBoolean())
				sb.append(str.boolValue());
			else
				sb.append(str.toString());
		}
		else if(obj instanceof NSSet) {
			NSSet s = (NSSet)obj;
			
			sb.append("Set(\n");
			for(NSObject i : s.allObjects())
				sb.append("\t"+dumpNSObject(i)+",\n");
			sb.append("\t)\n");
		}
		else if(obj instanceof NSString) {
			NSString str = (NSString)obj;
			sb.append(str.getContent());
		}
		else {
			sb.append(obj.toString());
		}
		return sb.toString();
	}

	public static String getStringFromInputStream(InputStream is) throws IOException {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
 
		return sb.toString();
 
	}
	
	public static void ref(Object o) {
		o.toString();
	}
}
