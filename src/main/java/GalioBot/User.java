package GalioBot;

public class User implements java.io.Serializable, Comparable<User>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5200021709255127450L;
	private String username;
	private int kickedcount;
	
	public User(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void addkick() {
		kickedcount++;
	}
	
	public int getKickCount() {
		return kickedcount;
	}

	@Override
	public int compareTo(User o) {
		return o.getKickCount() - this.getKickCount();
	}
}
