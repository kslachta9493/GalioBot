package GalioBot;

public class Map implements Comparable<Map>{
	
	private String map;
	private int count;
	private String data;
	private String splash;
	
	public Map(String map) {
		this.map = map;
		count = 0;
	}
	public Map(String map, String data, String splash) {
		this.map = map;
		this.data = data;
		this.splash = splash;
		count = 0;
	}
	
	public void setMap(String map) {
		this.map = map;
	}
	
	public String getMap() {
		return map;
	}
	
	public void increment() {
		count++;
	}
	
	public String getCount() {
		
		if (count == 0) {
			return "Not Selected";
		}
		return String.valueOf(count);
	}
	
	public int compareTo(Map m) {
		
		if (m.getCount().equals("Not Selected") && this.getCount().equals("Not Selected")) {
			return 0;
		}
		if (m.getCount().equals("Not Selected") && !this.getCount().equals("Not Selected")) {
			return -1;
		}
		if (!m.getCount().equals("Not Selected") && this.getCount().equals("Not Selected")) {
			return 1;
		}
		
		return Integer.parseInt(m.getCount()) - Integer.parseInt(this.getCount());
	}
	
	public String getData() {
		return data;
	}
	
	public String getSplash() {
		return splash;
	}
}
