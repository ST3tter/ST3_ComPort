package com;

import java.io.IOException;
import java.io.InputStream;

import purejavacomm.SerialPort;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;
import user.UART;

/**
 * Process the incoming data bytes of the serial interface.
 * <p>
 * Implements the SerialPortEventListner and therefore waits on data to be
 * available on the serial port. During initialization of the serial port within
 * the corresponding SerialInterface the SerialReceive object is configured as
 * Listener of events SerialPortEvent.DATA_AVAILABLE.
 * 
 * @author vod1
 * @version 1.1; Juli 2013
 * 
 *******************************************************************************/
public class SerialRecv implements SerialPortEventListener {

	// ----- Data
	// ---------------------------------------------------------------
	/** Reads available data from the serial port */
	private InputStream serialReader = null;

	/**
	 * Used to call the method SerialInterface.putByte() to process the received
	 * data bytes.
	 */
	private SerialInterface serialInterface = null;

	// ----- Implementation
	// -----------------------------------------------------

	/* ---------------------------------------------------------------------- */
	public SerialRecv(SerialPort serialPort, UART uart) throws IOException {

		// this.serialInterface = uart;
		serialReader = serialPort.getInputStream();
	}

	/* ---------------------------------------------------------------------- */
	@Override
	public synchronized void serialEvent(SerialPortEvent serialPortEvent) {

		switch (serialPortEvent.getEventType()) {

		case SerialPortEvent.BI: {
			// serial interface received unknown event
			break;
		}
		case SerialPortEvent.OE: {
			// serial interface received unknown event
			break;
		}
		case SerialPortEvent.FE: {
			// serial interface received unknown event
			break;
		}
		case SerialPortEvent.PE: {
			// serial interface received unknown event
			break;
		}
		case SerialPortEvent.CD: {
			// serial interface received unknown event
			break;
		}
		case SerialPortEvent.CTS: {
			// serial interface received unknown event
			break;
		}
		case SerialPortEvent.DSR: {
			// serial interface received unknown event
			break;
		}
		case SerialPortEvent.RI: {
			// serial interface received unknown event
			break;
		}
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY: {
			// serial interface received unknown event
			break;
		}
		/* New data is available. Receive the data bytes and process them */
		/* with the method CommInterface.putByte() */
		case SerialPortEvent.DATA_AVAILABLE: {

			try {
				while (serialReader.available() > 0) {

					byte newByte = (byte) serialReader.read();
					serialInterface.putByte(newByte);
				}
			} catch (IOException e) {
				// TODO add Exception handling
			}

			break;
		}
		default: {
			break;
		}
		}
	}
}
