package joystick.listeners;

/**
 * Place program description here
 *
 * @author Mats Otten
 * @project desktop-interface
 * @since 29-09-15
 */
public interface DataReceiveListener {
	void onDataReceive(int module, byte[] data);
}
