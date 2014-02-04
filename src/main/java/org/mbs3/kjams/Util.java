package org.mbs3.kjams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.dd.plist.*;

public class Util {
		
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
