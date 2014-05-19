package com;

import java.util.concurrent.LinkedBlockingQueue;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;

/**
 * Serial communication port. Used to connect/disconnect a serial port.
 * <p>
 * If connect() is called the SerialPort tries to open the desired serial port.
 * After the port is opened it will be configured and a class of SerialSend and
 * SerialReceive is instantiated.
 * <p>
 * If disconnect is called the cleanup() method of SerialSend and SerialReceive
 * is called. Finally the serial port is closed.
 * 
 * @author vod1
 * @version 1.1; Juli 2013
 * 
 *******************************************************************************/
public class SerialInterface {

	// ----- Data
	// ---------------------------------------------------------------
	/** The opened RS-232 serial communications port. */
	private SerialPort serialPort = null;

	/**
	 * Processes the messages from the tag processor and send them to the bird
	 * tag or bird tag interface.
	 */
	private SerialSend serialSend = null;

	/**
	 * Receives the incoming data bytes
	 */
	private SerialRecv serialRecv = null;

	/**
	 * Queue for incoming Data from GPS Module. Input Queue.
	 */
	private LinkedBlockingQueue<String> inQueue = new LinkedBlockingQueue<String>();

	/**
	 * Queue for outgoing Data to the GPS Module. Output Queue.
	 */
	private LinkedBlockingQueue<String> outQueue = new LinkedBlockingQueue<String>();

	/**
	 * Variable to hold temporary String containing one "Sign"
	 */
	private String msg = "";

	/**
	 * String containing one GPS Message
	 */
	private String outmsg = "";

	/**
	 * Flag indicating if a Start Sign ('$') has been detected in the incoming
	 * Serial Data.
	 */
	private boolean startDetected = false;

	// ----- Implementation
	// -----------------------------------------------------

	/* ---------------------------------------------------------------------- */
	public SerialInterface() {
	}

	/**
	 * open a new COM port
	 * <p>
	 * configure Serial Communication after successful opening the COM port 8N1,
	 * 4800 Baud
	 **************************************************************************/
	public void connect(String port) {

		try {
			// get COM Port Identifier
			CommPortIdentifier commPortIndetifier = CommPortIdentifier
					.getPortIdentifier(port);

			// open COM Port
			serialPort = (SerialPort) commPortIndetifier.open("serial " + port,
					2000);

			// SerialSend und SerialReceive erzeugen
			// serialSend = new SerialSend(serialPort, this);
			// serialRecv = new SerialRecv(serialPort, this);

			// SerialPort konfigurieren
			// 8N1, 4800 Baud
			serialPort.setSerialPortParams(4800, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.addEventListener(serialRecv);
			serialPort.notifyOnBreakInterrupt(false);
			serialPort.notifyOnCarrierDetect(false);
			serialPort.notifyOnCTS(false);
			serialPort.notifyOnDataAvailable(true);
			serialPort.notifyOnDSR(false);
			serialPort.notifyOnFramingError(false);
			serialPort.notifyOnOutputEmpty(false);
			serialPort.notifyOnOverrunError(false);
			serialPort.notifyOnParityError(false);
			serialPort.notifyOnRingIndicator(false);

		} catch (NoSuchPortException e) {
			e.printStackTrace();
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * close Serial Ports
	 **************************************************************************/
	public void disconnect() {

		if (serialSend != null) {
			serialSend = null;
		}
		if (serialRecv != null) {
			serialRecv = null;
		}
		if (serialPort != null) {
			try {
				serialPort.removeEventListener();
				serialPort.notifyOnDataAvailable(false);
				serialPort.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			serialPort = null;
		}
	}

	/* ---------------------------------------------------------------------- */
	public LinkedBlockingQueue<String> getInQueue() {

		return inQueue;
	}

	/* ---------------------------------------------------------------------- */
	public LinkedBlockingQueue<String> getOutQueue() {

		return outQueue;
	}

	/**
	 * process Message and put it in receivebuffer
	 * 
	 * @param newByte
	 *            Received data byte
	 **************************************************************************/
	public void putByte(byte newByte) {

		byte[] ar = new byte[1];
		ar[0] = newByte;
		msg = new String(ar);

		if (newByte == '$') {
			// new Message Start detected
			// reset Msg String and add Start-delimiter
			outmsg = msg;
			startDetected = true;

		} else if (newByte == '\n' && startDetected == true) {
			// End of Message
			// Put Message in Receive-Buffer
			outmsg = outmsg.concat(msg);
			try {
				inQueue.put(outmsg);
			} catch (InterruptedException e) {
				// TODO handle Exception
			}
			msg = "";
			outmsg = "";
			startDetected = false;
		} else if (startDetected == true) {
			// Add Byte to receive Message String
			outmsg = outmsg.concat(msg);
		} else {
			// Dont process Data
		}
	}

}
