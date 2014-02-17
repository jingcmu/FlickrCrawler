package cse598.crawlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import cse598.crawlers.ReadLists;
import cse598.crawlers.SimpleCrawlerInterface;


@SuppressWarnings("rawtypes")
public class crawler implements SimpleCrawlerInterface {

	public crawler() {
	}

	public ConcurrentSkipListMap<Integer, String> getFriends(String username){
		ReadLists readlist = new ReadLists();
		ConcurrentSkipListMap<Integer, String> friends = readlist.ReadPendingNodeList();
		ConcurrentSkipListMap<Integer, String> donenodes = readlist.ReadProcessedNodeList();

		//check if the value in to be processed node is already processed
		//if yes, then remove it from the friends list

		Iterator iter = friends.entrySet().iterator();
		while(iter.hasNext()) {
			ConcurrentSkipListMap.Entry entry = (ConcurrentSkipListMap.Entry)iter.next();
			String fr = (String)entry.getValue();
			if(donenodes.containsValue(fr) == true){
				iter.remove();
			}
		}

		int previous = 0;
		int end = 0;
		int avatarPos = 0;
		int begin = 0;
		Integer mapNo = friends.lastKey();
		System.out.println("mapNo is :"+mapNo);
		Integer doneNo = donenodes.lastKey();
		System.out.println("doneNo is :"+ doneNo);
		String page;

		//counting no of unique user id, Modify this with each re-run after checking from edgelist
		//in the edge list, find the unique nos of users in first column, that no will be usercounter here
		int usercounter = 3652;

		//array for username friends collection
		ArrayList<String> edge = new ArrayList<String>();

		int counter = 0;

		Iterator it = friends.entrySet().iterator();
		if( usercounter < 5020){
			while(it.hasNext() && usercounter < 5020) {
				ConcurrentSkipListMap.Entry entry = (ConcurrentSkipListMap.Entry)it.next();
				String username2 = (String)entry.getValue();
				//get the max page number of friends
				double numberFriendsPerPage = 30.0;
				int numPages = (int) Math.ceil(getNoFriends(username2)/numberFriendsPerPage);
				if (numPages == 0){
					numPages = 1;
				}

				for(int i=1; i<=numPages; i++){
					page = readFriendsAjaxPage(username2, i);
					previous = page.indexOf("PeopleResults");
					end = previous;

					//get the friends in pages same as earlier
					while(page.indexOf("rel=\"contact\"><img",previous) != -1 && usercounter < 5020){
						avatarPos = page.indexOf("img src=",previous);
						begin = page.indexOf("#", avatarPos);
						end = page.indexOf("\" alt=\"", begin);
						String friend = page.substring(begin+1, end);
						//System.out.print("username :" + username2);
						//System.out.println(" -> "+ friend);


						//adding username -> friends to an array
						int ind = edge.size();
						if(ind >=2 && !edge.get(ind - 2).equals(username2)){
							usercounter++;
							System.out.println("usercounter value :"+ usercounter);
						}

						edge.add(counter, username2);
						counter++;
						edge.add(counter, friend);
						counter++;

						//If the username discovered above is not already in array list friends or it is not empty or it is not the username itself
						//or it does not starts with the username, add it to the array list friends.
						if(!friends.containsValue(friend) && !donenodes.containsValue(friend)
								&& friend!="" && !friend.equals(username2) && !friend.startsWith(username2+"/")) {
							friends.put(mapNo++, friend);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						previous = end;
					}
				}

				//add the processed username to done list
				donenodes.put(++doneNo, username2);
				//remove the processed node from the pending list
				it.remove();

				if(usercounter % 10 == 0){
					writeNodeList(friends, donenodes);
					writeEdgeList(new File("D:\\FlickrEdgeList.csv"), edge);
					edge = clearArrayList(edge);
					counter = 2;

				}
			}
		}
		return friends;
	}


	/*
	 * This method clears the ArrayList edge after writing it
	 * in the file.
	 *
	 */
	public static ArrayList<String> clearArrayList(ArrayList<String> edge){
		ArrayList<String> temp = new ArrayList<String>();
		int index = edge.size();
		if(index >=4){
			temp.add(edge.get(index-2));
			temp.add(edge.get(index-1));
			edge.clear();
			edge.addAll(temp);
			temp.clear();
		}
		return edge;

	}



	/*
	 * Write Node List
	 *
	 */
	@SuppressWarnings({ "unchecked" })
	public static void writeNodeList(ConcurrentSkipListMap friendslist, ConcurrentSkipListMap donenodes) {
		File aFile = new File("D:\\FlickrNodeList.csv");
		try {
			PrintStream PS = new PrintStream(aFile, "UTF-8");
			TreeMap Tfriendslist = new TreeMap(friendslist);
			Iterator i = Tfriendslist.entrySet().iterator();
			PS.print(239); //0xEF
			PS.print(187); //0xBB
			PS.print(191); //0xBF
			PS.print("\n");
			while(i.hasNext()) {
				Map.Entry entry = (Map.Entry)i.next();
				String val = (String)entry.getValue();
				Integer key = (Integer)entry.getKey();
				PS.print(key);
				PS.print("," );
				PS.print(val);
				PS.print("\n" );
			}
			PS.flush();
			PS.close();

			File doneFile = new File("D:\\FlickrProcessedNodeList.csv");
			PrintStream PSdone = new PrintStream(doneFile, "UTF-8");
			TreeMap Tdonenodes = new TreeMap(donenodes);
			Iterator j = Tdonenodes .entrySet().iterator();
			PSdone.print(239); //0xEF
			PSdone.print(187); //0xBB
			PSdone.print(191); //0xBF
			PSdone.print("\n");
			while(j.hasNext()) {
				Map.Entry entry = (Map.Entry)j.next();
				String val = (String)entry.getValue();
				Integer key = (Integer)entry.getKey();
				PSdone.print(key);
				PSdone.print("," );
				PSdone.print(val);
				PSdone.print("\n" );
			}
			PSdone.flush();
			PSdone.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/*
	 * write edge list
	 *
	 */

	public static void writeEdgeList(File aFile, ArrayList<String> edge) {
		String commaSeparatedValues = "";
		Iterator<String> iter = edge.iterator();
		while (iter.hasNext()) {
			commaSeparatedValues = commaSeparatedValues + iter.next() + "," +iter.next() + "\n";
		}
		/**Remove the last comma**/
		if (commaSeparatedValues.endsWith(",")) {
			commaSeparatedValues = commaSeparatedValues.substring(0,
					commaSeparatedValues.lastIndexOf(","));
		}
		try {
			if (!aFile.exists()) {
				aFile.createNewFile();
			}
			FileWriter FW = new FileWriter(aFile, true);
			BufferedWriter out = new BufferedWriter(FW);
			out.write(commaSeparatedValues);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// @Override
	public String getCrawlerType() {
		return "HTML";
	}


	//@Override
	public String getSiteName() {
		return "Flickr";
	}
	public String makeRequest(String Method, String URL, String PostData) {

		URLConnection conn = null;
		BufferedReader data = null;
		String line;
		StringBuffer buf = new StringBuffer();
		URL theURL = null;
		//check if the URL starts with http://
		if(!URL.toLowerCase().startsWith("http://"))
			URL = "http://"+URL;
		try
		{
			theURL = new URL(URL);
			conn = theURL.openConnection();
			if(Method == "GET") {
				data = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = data.readLine()) != null) {
					buf.append(line + "\n");
				}
				data.close();
			}
			else {
				conn.setDoOutput(true);
				OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

				//write parameters
				writer.write(PostData);
				writer.flush();

				//get the response
				data = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = data.readLine()) != null) {
					buf.append(line);
				}
				writer.close();
				data.close();
			}
		}
		catch ( MalformedURLException e)
		{
			System.out.println("Bad URL: " + theURL);
		}
		catch (IOException e) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.print("IO Error:" + e.getMessage()+" ");
			return "IOERROR";
		}
		return buf.toString();
	}


	public int getNoFriends(String username) {
		int noFriends = 0;
		//Read the friends page
		String page = readFriendsPage(username);

		if(page.indexOf("class=\"Results") !=-1){
			int begin = page.indexOf("class=\"Results\">");
			int end = page.indexOf("People", begin);

			if(begin != -1 && end != -1) {
				String noFriend = page.substring(begin+17, end-1);
				noFriends = Integer.valueOf(noFriend.replaceAll(",", ""));
			}
		}
		if(noFriends > 1002){
			noFriends = 990;
		}
		return noFriends;
	}

	//this is giving the page where all the friend list is present
	public String readFriendsPage(String username){
		String wholepage = "";

		wholepage = makeRequest("GET", "http://flickr.com/people/"+ username +"/contacts/", "");
		return wholepage;
	}

	public String readFriendsAjaxPage(String username, int num){
		String wholepage = "";
		try {
			wholepage = makeRequest("GET", "http://flickr.com/people/"+ username +"/contacts/?filter=&page=" + num ,"");
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return wholepage;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//create an HTML-scraper instance
		crawler cw = new crawler();
		String sampleUser = "101473385@N05";
		System.out.println("getFriends");
		System.out.println(cw.getFriends(sampleUser));
		System.out.println("getSiteName");
		System.out.println(cw.getSiteName());
		System.out.println("getCrawlerType");
		System.out.println(cw.getCrawlerType());
	}
}