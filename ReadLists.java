package cse598.crawlers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
//import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

public class ReadLists{
	public ReadLists(){
	}

	public ConcurrentSkipListMap<Integer, String> ReadProcessedNodeList() {
		String csvFile = "D:\\FlickrProcessedNodeList.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		ConcurrentSkipListMap<Integer, String> procnodes = new ConcurrentSkipListMap<Integer, String>();
		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), Charset.forName("windows-1252")));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] nodes = line.split(cvsSplitBy);
				procnodes.put(Integer.parseInt(nodes[0]), nodes[1]);
			}
			//loop map

//			for (Entry<Integer, String> entry : procnodes.entrySet()) {
//				System.out.println("[Key= " + entry.getKey() + " , User="
//						+ entry.getValue() + "]");
//			}
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
		return procnodes;
	}
	
	public ConcurrentSkipListMap<Integer, String> ReadPendingNodeList() {
		String csvFile = "D:\\FlickrNodeList.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		ConcurrentSkipListMap<Integer, String> pendingnodes = new ConcurrentSkipListMap<Integer, String>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), Charset.forName("UTF-8")));
			while ((line = br.readLine()) != null) {
				String[] nodes = line.split(cvsSplitBy);
				pendingnodes.put(Integer.parseInt(nodes[0]), nodes[1]);
			}
			//loop map

//			            for (Entry<Integer, String> entry : pendingnodes.entrySet()) {
//			                         System.out.println("[Key= " + entry.getKey() + " , User="
//			                         + entry.getValue() + "]");
//			            }
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
		return pendingnodes;
	}

}
