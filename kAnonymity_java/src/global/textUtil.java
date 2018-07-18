package global;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class textUtil {
	
	// load stop word list from file
	public  static TreeMap<String, Integer> loadMap(String inStr){
		
		TreeMap<String, Integer> outMap = new TreeMap<String, Integer>();
		try {
			FileInputStream stream = new FileInputStream(inStr);
    		InputStreamReader reader = new InputStreamReader(stream);
    		BufferedReader buffer = new BufferedReader(reader); 

			String label = new String("");
			Object oldValue;
			
			while ((label = buffer.readLine()) != null && !label.equals("")) {
				
				oldValue = outMap.get(label) ;
				
				if (oldValue == null)
					outMap.put(label, new Integer(1));
			}
		    stream.close();
	    } catch (IOException ex) {
			ex.printStackTrace();
		}		
		return outMap;				
	}
	
	// load stop word list from file
	public  static Map loadIdfMap(String inStr){
		
		Map idfMap = new TreeMap();
		
		try {
			FileInputStream stream = new FileInputStream(inStr);
    		InputStreamReader reader = new InputStreamReader(stream);
    		BufferedReader buffer = new BufferedReader(reader); 

			String label = new String("");
			
			while ((label = buffer.readLine()) != null) {
				
				StringTokenizer st = new StringTokenizer(label,"|");
				
				String kStr = st.nextToken();
				String vStr = st.nextToken();
				
				Double newValue = new Double(vStr);
				
				idfMap.put(kStr,newValue);				
						
			}//end_of_while
			
		    stream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}		
		return idfMap;	
	}
	
		
	public static Writer createTXTFile(String inStr){
		
		Writer outFile;
		
		try {
			outFile = new BufferedWriter(new FileWriter(new File(inStr)));
			return outFile;
		} catch(IOException e) {
	    	System.out.println(e);
		}
		return null;
	}

	public static void saveTXTFile(Writer outFile){
		
		try {
			outFile.close();
		} catch(IOException e) {
	    	System.out.println(e);
		}
	}
	
	public static void writeString (Writer inWriter, String inStr){
		
		try {
			inWriter.write(inStr);
			inWriter.flush();
		} catch(IOException e) {
    		System.out.println(e);
		}
	}	
	
	public static void PrintArray(boolean[] inVector, int idx){
		for (int i=0; i<idx; ++i)
			System.out.print("["+ i +":" + inVector[i] +"]");
		System.out.println();
	}
	
	
	public static void PrintArray(int[] inVector, int idx){
		for (int i=0; i<idx; ++i)
			System.out.print("["+ i +":" + inVector[i] +"]");
		System.out.println();
	}
	
	public static void PrintArray(double[] inVector, int idx){
		for (int i=0; i<idx; ++i)
			System.out.print(inVector[i] +":");
		System.out.println();
	}
	
	public static void PrintArray0(double[][] inVector, int idx){
		
		for (int i=0; i<idx; ++i)
			System.out.print("["+i+":"+inVector[0][i]+":" + inVector[1][i]+"]");
		System.out.println();
	}
	
	public static void main(String[] args) {
		textUtil test = new textUtil();
	}
}
