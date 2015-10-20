package hva.row8.Interfaces;

/**
 * Created by Mats-Mac on 06-10-15.
 */
public interface DataReceiveListener {
	void onDataReceive(int module, byte[] data);
	void onConnectionDrop();
	//void onConnectionDrop(boolean onStart);
}
