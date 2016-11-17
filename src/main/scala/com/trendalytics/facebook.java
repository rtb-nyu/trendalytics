package myProject;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonSimpleExample {
     public static void main(String[] args) {

	JSONParser parser = new JSONParser();

	try {

		Object obj = parser.parse(new FileReader("C:/Users/bootstrap/Documents/RTBD/fbevents.json"));
		JSONArray jsonArray = (JSONArray) obj;
		FileWriter fileWriter= new FileWriter("C:/Users/bootstrap/Documents/RTBD/fb.txt");
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		
		for(int i = 0; i < jsonArray.size(); i++) {
		
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			StringBuilder sb = new StringBuilder();
			sb.append((String) jsonObject.get("id"));
			sb.append(";;");
			sb.append((String) jsonObject.get("name"));
			sb.append(";;");
			sb.append((String) jsonObject.get("description"));
			sb.append(";;");
			sb.append((String) jsonObject.get("distance"));
			sb.append(";;");
			
			sb.append((String) jsonObject.get("startTime"));
			sb.append(";;");
			sb.append( jsonObject.get("timeFromNow").toString());
			sb.append(";;");
			JSONObject jsObj = (JSONObject) jsonObject.get("stats");
			sb.append((String) jsObj.get("attending").toString());
			sb.append(";;");
			sb.append((String) jsObj.get("declined").toString());
			sb.append(";;");
			sb.append((String) jsObj.get("maybe").toString());
			sb.append(";;");
			sb.append((String) jsObj.get("noreply").toString());
			sb.append(";;");
			jsObj = (JSONObject) jsonObject.get("venue");
			sb.append((String) jsObj.get("id"));
			sb.append(";;");
			sb.append((String) jsObj.get("name"));
			sb.append(";;");
			JSONObject jsonObj = (JSONObject) jsObj.get("location");
			sb.append((String) jsonObj.get("city"));
			sb.append(";;");
			sb.append((String) jsonObj.get("country"));
			sb.append(";;");
			sb.append((String) jsonObj.get("latitude").toString());
			sb.append(";;");
			sb.append((String) jsonObj.get("longitude").toString());
			sb.append(";;");
			sb.append((String) jsonObj.get("state"));
			sb.append(";;");
			sb.append((String) jsonObj.get("street"));
			sb.append(";;");
			sb.append((String) jsonObj.get("zip"));
			sb.append("&*&");
			
			
			bufferedWriter.write(sb.toString());
			
			
			
			
		}
		
		bufferedWriter.close();


	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (ParseException e) {
		e.printStackTrace();
	}

     }

}
