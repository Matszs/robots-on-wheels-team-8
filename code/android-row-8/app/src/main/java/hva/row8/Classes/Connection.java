package hva.row8.Classes;

/**
 * Created by Mats-Mac on 06-10-15.
 */
public class Connection {
	public int id;
	public String name;
	public String ip;
	public String port;

	public Connection(int id, String name, String ip, String port) {
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.port = port;
	}
}
