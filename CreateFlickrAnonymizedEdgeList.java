package cse598.crawlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class CreateFlickrAnonymizedEdgeList {
	public static void main(String[] args) {
		CreateFlickrAnonymizedEdgeList fileread = new CreateFlickrAnonymizedEdgeList();
		ConcurrentSkipListMap<Integer, String> nodes = new ConcurrentSkipListMap<Integer, String>();
		ArrayList<String> edges = new ArrayList<String>();
		nodes = fileread.ReadNodeList();
		System.out.println("Nodes list read is done, Nodes are in a HashMap now.");
		edges = fileread.ReadEdgeList();
		System.out.println("Edge list read is done, Edges are in ArrayList now.");
		fileread.WriteAnonymizedEdgeList((new File("D:\\FlickrAnonymizedEdgeList.csv")), nodes, edges);

	}

	public ConcurrentSkipListMap<Integer, String> ReadNodeList() {
		String csvFile = "D:\\FlickrNodeList.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		ConcurrentSkipListMap<Integer, String> nodes = new ConcurrentSkipListMap<Integer, String>();
		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] sNode = line.split(cvsSplitBy);
				nodes.put(Integer.parseInt(sNode[0]), sNode[1]);
			}
			
{
//               System.out.println("[Key= " + entry.getKey() + " , User="
//               + entry.getValue() + "]");
//                                            }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return nodes;
	}

	public ArrayList<String> ReadEdgeList() {
		String csvFile = "D:\\FlickrEdgeList.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		ArrayList<String> edges = new ArrayList<String>();
		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] edge = line.split(cvsSplitBy);
				edges.add(edge[0]);
				edges.add(edge[1]);
			}
//loop map
//      for(int i=0; i < edges.size(); i++){
//         System.out.println("edge value is in edgelist :" + edges.get(i));
//        }

	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return edges;
	}


	/*
	 * Create the AnonymizedEdgeList in an ArrayList
	 * and write it to file at D:\\FlickrAnonymizedEdgeList.csv
	 */

	private void WriteAnonymizedEdgeList(File aFile, ConcurrentSkipListMap<Integer,String> nodes, ArrayList<String> edges) {
		ArrayList<Integer> anonymized = new ArrayList<Integer>();
		Map<String, Integer> reverseMap = new HashMap<String, Integer>();
		for(Map.Entry<Integer, String> entry : nodes.entrySet()) {
			String value = entry.getValue();
			Integer key = entry.getKey();
			reverseMap.put(value,key);
		}
		nodes.clear();
		nodes = new ConcurrentSkipListMap<Integer, String>();
		System.out.println("Cleared the Nodes list from the memory.");

		//create the anonymized ArrayList
		for(int i = 0; i<edges.size(); i++){
			String anonymizedkey = edges.get(i);
			Integer anonymizedvalue = reverseMap.get(anonymizedkey);
			anonymized.add(anonymizedvalue);
		}
		reverseMap.clear();
		reverseMap = new HashMap<String, Integer>();
		System.out.println("Cleared the reverse map from memory.");

		//   for(int i=0; i < anonymized.size(); i++){
		//                   System.out.println("anonymized value is :" + anonymized.get(i));
		//   }

		String commaSeparatedValues = "";
		Iterator<Integer> iter = anonymized.iterator();
		while (iter.hasNext()) {
			commaSeparatedValues = commaSeparatedValues + iter.next() + "," +iter.next() + "\n";
			iter.remove();
		}
		System.out.println("Values are in commaSeparatedValues list now.");


				if (commaSeparatedValues.endsWith(",")) {
			commaSeparatedValues = commaSeparatedValues.substring(0,
					commaSeparatedValues.lastIndexOf(","));
		}
		try {
			if (!aFile.exists()) {
				aFile.createNewFile();
			}
			FileWriter FW = new FileWriter(aFile, false);
			BufferedWriter out = new BufferedWriter(FW);
			out.write(commaSeparatedValues);
			System.out.println("File Created, Please check at D:\\FlickrAnonymizedEdgeList.csv");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}