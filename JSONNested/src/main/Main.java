package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
	
	public static JSONArray dataList;
	public static List<JSONObject> winners;
	public static List<JSONObject> losers;
	
	public static void main(String[] args) {
		
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(
			        "C:\\Users\\Tejal\\Documents\\ASU\\3_Fall 2016\\CSE 591 Data Visualization\\Assignments\\Assignment2\\MySolution\\MatchesCleanedLessCols.json"));
			JSONObject jsonObject = (JSONObject) obj;
	        dataList = (JSONArray) jsonObject.get("datalist");
	        winners = new ArrayList<JSONObject>();
            losers = new ArrayList<JSONObject>();
            Iterator<JSONObject> iterator = dataList.iterator();
            while (iterator.hasNext()) {
            	JSONObject jobj = iterator.next();
                if (jobj.get("wl").equals("Winner")) {
                	winners.add(jobj);
                }
                else {
                	losers.add(jobj);
                }
            }
            JSONObject root = getMaxMid(winners);
            root.put("mid", (Long)(root.get("mid"))+1);
            findChildren(root);
            System.out.println(root.get("mid"));
            FileWriter file = new FileWriter("C:\\Users\\Tejal\\Documents\\ASU\\3_Fall 2016\\CSE 591 Data Visualization\\Assignments\\Assignment2\\MySolution\\MatchesCleanedNested.json");
    		file.write(root.toJSONString());
    		file.close();
    		System.out.println(root.toJSONString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void findChildren(JSONObject root) {
		JSONObject leftChild = findLeftChild(root);
		if (leftChild!=null) {
			JSONObject rightChild = findRightChild(leftChild);
			JSONArray children = new JSONArray();
			children.add(leftChild);
			children.add(rightChild);
			root.put("children", children);
			//System.out.println(leftChild.get("mid"));
			findChildren(leftChild);
			if (rightChild!=null) {
				findChildren(rightChild);
			}
		}
	}
	
	public static JSONObject findLeftChild(JSONObject root) {
		JSONObject left = null;
		List<JSONObject> winnerRecs = getWinnerRecs(root);
		left = getMaxMid(winnerRecs);
		return left;
	}
	
	public static List<JSONObject> getWinnerRecs(JSONObject winner){
		List<JSONObject> winnerRecs = new ArrayList<JSONObject>();
		Iterator<JSONObject> iterator = winners.iterator();
		while (iterator.hasNext()) {
        	JSONObject jobj2 = iterator.next();
            if (winner.get("player").toString().equals(jobj2.get("player").toString())
            		&& (Long)winner.get("mid") > (Long)jobj2.get("mid")) {
            	winnerRecs.add(jobj2);
            }
        }
		return winnerRecs;
	}
	
	public static JSONObject findRightChild(JSONObject root) {
		for (JSONObject jobj : losers) {
			if (((Long)root.get("mid")).longValue() == ((Long)jobj.get("mid")).longValue())
				return jobj;
		}
		return null;
	}
	
	public static JSONObject getMaxMid(List<JSONObject> winners) {
		JSONObject jobj = null;
		if (winners!=null && !winners.isEmpty()) {
			jobj = (JSONObject) winners.get(0);
			Iterator<JSONObject> iterator = winners.iterator();
			while (iterator.hasNext()) {
				JSONObject jobj2 = iterator.next();
				if ((Long) jobj.get("mid") < (Long) jobj2.get("mid")) {
					jobj = jobj2;
				}
			}
		}
		return jobj;
	}

	public static void method1() {
		Map<Long, Map<String,JSONObject>> matches = new TreeMap<Long, Map<String,JSONObject>>(Collections.reverseOrder());
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(
                    "C:\\Users\\Tejal\\Documents\\ASU\\3_Fall 2016\\CSE 591 Data Visualization\\Assignments\\Assignment2\\MySolution\\MatchesCleanedLessCols.json"));
 
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray dataList = (JSONArray) jsonObject.get("datalist");
            List<JSONObject> winners = new ArrayList<JSONObject>();
            List<JSONObject> losers = new ArrayList<JSONObject>();
            Iterator<JSONObject> iterator = dataList.iterator();
            while (iterator.hasNext()) {
            	JSONObject jobj = iterator.next();
                if (jobj.get("wl").equals("Winner")) {
                	winners.add(jobj);
                }
                else {
                	losers.add(jobj);
                }
            }
            Iterator<JSONObject> iteratorw = winners.iterator();
            while (iteratorw.hasNext()) {
            	JSONObject jobj1 = iteratorw.next();
            	Long mid1 = (Long) jobj1.get("mid");
                Iterator<JSONObject> iterator2 = losers.iterator();
                //System.out.println(jobj.get("player"));
                while (iterator2.hasNext()) {
                	JSONObject jobj2 = iterator2.next();
                	Long mid2 = (Long) jobj2.get("mid");
                	if (mid1.longValue()==mid2.longValue()) {
                		//System.out.println(jobj.get("mid")+"\t"+jobj2.get("mid")+"\t"+jobj.get("player")+"\t"+jobj2.get("player"));
                		Map<String,JSONObject> players = new HashMap<String,JSONObject>();
                		players.put("winner", jobj1);
                		players.put("loser", jobj2);
                		matches.put((Long) jobj1.get("mid"), players);
                	}
                }
            }
            int h = (int) Math.floor(Math.log(matches.keySet().size())/Math.log(2));
            System.out.println(h);
            ArrayList<Long> keys = new ArrayList<Long>(matches.keySet());
            matches.put(keys.get(0)+1,matches.get(keys.get(0)));
            keys = new ArrayList<Long>(matches.keySet());
            JSONObject root = matches.get(keys.get(0)).get("winner");
            /*for (Long mid : matches.keySet()) {
            	System.out.println(mid+"\t"+matches.get(mid).get("winner").get("player")+"\t"+matches.get(mid).get("loser").get("player"));
            }*/
            for (int i=0; i<=h-2; i++) {
            	for (int j=(int) (Math.pow(2, i)-1); j<(int) (Math.pow(2, i+1)); j++) {
            		JSONArray children = new JSONArray();
            		
            	}
            }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
