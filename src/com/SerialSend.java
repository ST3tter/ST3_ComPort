package com;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

import purejavacomm.SerialPort;
import user.UART;

/**
 * Sends telegrams
 * 
 * @author vod1
 * @version 1.1; Juli 2013
 * 
 *******************************************************************************/
public class SerialSend implements Runnable {

	// ----- Data
	// ---------------------------------------------------------------

	/** Writer to write data to the serial port and thus to the target. */
	private OutputStream serialWriterOS = null;

	/** SerialSend is running in a separate thread */
	private Thread serialSendThread = new Thread(this);

	/** Queue where the tag processors can put messages into */
	private LinkedBlockingQueue<String> outQueue = null;

	// ----- Implementation
	// -----------------------------------------------------

	/* ---------------------------------------------------------------------- */
	public SerialSend(SerialPort serialPort, UART uart) throws IOException {
		serialWriterOS = serialPort.getOutputStream();
		// outQueue = uart.getOutQueue();

		// start thread
		serialSendThread.start();
	}

	/**
	 * Sends Data over serial connection
	 **************************************************************************/
	@Override
	public void run() {

		try {
			String sendS = outQueue.take();
			serialWriterOS.write(sendS.getBytes());

		} catch (Exception e) {
			// TODO Error handling
		}
	}
}
