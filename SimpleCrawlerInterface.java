package cse598.crawlers;

import java.util.concurrent.ConcurrentSkipListMap;

public interface SimpleCrawlerInterface {

	public ConcurrentSkipListMap<Integer, String> getFriends(String username);//Returns the list of friends
	public int getNoFriends(String username);//Number of friends for user "username"
	public String getSiteName();//Returns sitename
	public String getCrawlerType();//Returns Crawler type {API, HTML}
}
