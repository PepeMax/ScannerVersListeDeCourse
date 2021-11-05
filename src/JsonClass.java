import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonClass {
	
	
	public static void writeInBDD(String barcode, String name) throws IOException, ParseException  {
		
		JSONParser jsonParser = new JSONParser();
        
        FileReader reader = new FileReader("BDD.json");
		
        JSONObject obj = (JSONObject) jsonParser.parse(reader);
                
        JSONArray items = (JSONArray) obj.get("items");
        
        JSONObject newJSONItem = new JSONObject();
        newJSONItem.put("barcode", barcode);
        newJSONItem.put("name", name);
        
        items.add(newJSONItem);
                
        PrintWriter pw = new PrintWriter("BDD.json");
        pw.write(obj.toJSONString());
          
        pw.flush();
        pw.close();
    
	}
	
	public static void readInBDD(String barcode, FentreListeDeCourse parent) throws IOException, ParseException  {
		
		JSONParser jsonParser = new JSONParser();
        
        FileReader reader = new FileReader("BDD.json");
		
        Object obj = jsonParser.parse(reader);
        
        JSONObject jsonObject = (JSONObject) obj;
        
        JSONArray items = (JSONArray) jsonObject.get("items");
                        
        boolean findItem = false;
        String jsonBarcode;
    	String jsonName;
    	
        int i = 0;
        
        do {
        	JSONObject jsonItem = (JSONObject) items.get(i);
        	jsonBarcode = (String) jsonItem.get("barcode");
        	jsonName = (String) jsonItem.get("name");
            
            if (jsonBarcode.equals(barcode)) {
            	parent.addToList(jsonName);
            	findItem = true;
            }
            
            i++;
        	
        }while(i < items.size() && !findItem);
        
        if(!findItem) {
        	String name;
        	do {
        		name = JOptionPane.showInputDialog("Rentrer le nom du produit");
			} while (name == null || name.equals(""));
        	
        	writeInBDD(barcode, name);
        }
        

         
       
       
        
	}

}
