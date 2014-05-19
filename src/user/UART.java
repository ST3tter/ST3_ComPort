package user;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;

import com.SerialRecv;
import com.SerialSend;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class UART extends JFrame {

	private JPanel contentPane;

	private JButton openButton;
	private JButton closeButton;

	private Boolean serialPortOpen = false;

	/** The opened RS-232 serial communications port. */
	private SerialPort serialPort = null;

	/**
	 * Processes the messages from the tag processor and send them to the bird
	 * tag or bird tag interface.
	 */
	private SerialSend serialSend = null;

	/** Receives the incoming data bytes */
	private SerialRecv serialRecv = null;

	/** Queue for incoming Data from GPS Module. Input Queue. */
	private LinkedBlockingQueue<String> inQueue = new LinkedBlockingQueue<String>();

	/** Queue for outgoing Data to the GPS Module. Output Queue. */
	private LinkedBlockingQueue<String> outQueue = new LinkedBlockingQueue<String>();

	/** Variable to hold temporary String containing one "Sign" */
	private String msg = "";

	/** String containing one GPS Message */
	private String outmsg = "";

	/**
	 * Flag indicating if a Start Sign ('$') has been detected in the incoming
	 * Serial Data.
	 */
	private boolean startDetected = false;

	/**
	 * Create the frame.
	 */
	public UART() {

		Enumeration portID = CommPortIdentifier.getPortIdentifiers();

		while (portID.hasMoreElements()) {
			System.out.println(((CommPortIdentifier) portID.nextElement())
					.getName());
		}

		/* Init the GUI */
		initGUI();
	}

	/**
	 * <b>Beschreibung:</b>
	 * <p>
	 * 
	 * Die Methode openSerialPort oeffenet eine serielle Schnittstelle mit
	 * <p>
	 * den Parametern PortName, Baudrate, DataBits,Parity und StopBits.
	 * 
	 */
	private void openSerialPort() {
		Boolean foundPort = false;

		// //Ist schon ein Port geoeffnet wird nichts gemacht
		// if (serialPortOpen != false) {
		// System.out.println("Serialport bereits geoeffnet");
		// return;
		// }
		// System.out.println("Oeffne Serialport");
		//
		//
		// try {
		// // get COM Port Identifier
		// //CommPortIdentifier commPortID =
		// CommPortIdentifier.getPortIdentifier(port);
		//
		// // open COM Port
		// //serialPort = (SerialPort) commPortIndetifier.open("serial " + port,
		// 2000);
		//
		// // SerialSend und SerialReceive erzeugen
		// serialSend = new SerialSend(serialPort, this);
		// serialRecv = new SerialRecv(serialPort, this);
		//
		// // SerialPort konfigurieren
		// // 8N1, 4800 Baud
		// serialPort.setSerialPortParams( 4800,
		// SerialPort.DATABITS_8,
		// SerialPort.STOPBITS_1,
		// SerialPort.PARITY_NONE);
		// serialPort.addEventListener(serialRecv);
		// serialPort.notifyOnBreakInterrupt(false);
		// serialPort.notifyOnCarrierDetect(false);
		// serialPort.notifyOnCTS(false);
		// serialPort.notifyOnDataAvailable(true);
		// serialPort.notifyOnDSR(false);
		// serialPort.notifyOnFramingError(false);
		// serialPort.notifyOnOutputEmpty(false);
		// serialPort.notifyOnOverrunError(false);
		// serialPort.notifyOnParityError(false);
		// serialPort.notifyOnRingIndicator(false);
		//
		// } catch (NoSuchPortException e) {
		// e.printStackTrace();
		// } catch (PortInUseException e) {
		// e.printStackTrace();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		serialPortOpen = true;
	}

	/**
	 * <b>Beschreibung:</b>
	 * <p>
	 * 
	 * Die Methode closeSerialPort schliesst die geoeffnete serielle
	 * Schnittstelle
	 * 
	 */
	private void closeSerialPort() {

		// Wenn ein Port geoeffnet ist wird dieser geschlossen
		if (serialPortOpen == true) {
			System.out.println("Schliesse Serialport");

			serialPortOpen = false;

			// serialPort.close();
		} else {
			System.out.println("Serialport bereits geschlossen");
		}
	}

	private void initGUI() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 250, 300);
		this.setResizable(false);

		contentPane = new JPanel(new MigLayout("", "[100][150]",
				"[][][][][]40[][]")); // Row constraints

		setContentPane(contentPane);

		openButton = new JButton("Open port");
		closeButton = new JButton("Close port");

		contentPane.add(openButton, "height 30,cell 0 5 2 1");
		contentPane.add(closeButton, "height 30, cell 0 6 2 1");

		contentPane.add(new JLabel("Port:"), " height 30, cell 0 0");
		contentPane.add(new JLabel("Baudrate:"), "height 30, cell 0 1");
		contentPane.add(new JLabel("Data bits:"), "height 30, cell 0 2");
		contentPane.add(new JLabel("Parity:"), "height 30, cell 0 3");
		contentPane.add(new JLabel("Stop bits:"), "height 30, cell 0 4");

		String baud[] = { "1200", "2400", "4800", "9600", "19200", "38400",
				"57600", "115200", "230400", "460800", "921600" };
		String data[] = { "5", "6", "7", "8" };
		String par[] = { "even", "odd", "none" };
		String stop[] = { "1", "1.5", "2" };

		JComboBox<String> baudrateComboBox = new JComboBox<String>(baud);
		baudrateComboBox.setSelectedItem("9600");

		JComboBox<String> dataBitsComboBox = new JComboBox<String>(data);
		dataBitsComboBox.setSelectedItem("8");

		JComboBox<String> parityBitComboBox = new JComboBox<String>(par);
		parityBitComboBox.setSelectedItem("none");

		JComboBox<String> stopBitComboBox = new JComboBox<String>(stop);
		stopBitComboBox.setSelectedItem("1");

		contentPane.add(baudrateComboBox, "width 150, height 30, cell 1 1");
		contentPane.add(dataBitsComboBox, "width 150, height 30, cell 1 2");
		contentPane.add(parityBitComboBox, "width 150, height 30, cell 1 3");
		contentPane.add(stopBitComboBox, "width 150, height 30, cell 1 4");

		// Actionlistener fuer den oeffne Button
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Port mit den entsprechenden Parametern oeffnen
				openSerialPort();
			}
		});

		// Actionlistener fuer den schliessen Button
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Port mit den entsprechenden Parametern oeffnen
				closeSerialPort();
			}
		});

	}

	/**
	 * <b>Beschreibung:</b>
	 * <p>
	 * 
	 * Klasse fuer die Reaktion auf das schliessen des Fensters
	 */
	class WindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent event) {

			// Wird das Fenster geschlossen soll auch die serielle Schnittstelle
			// geschlossen werden.
			closeSerialPort();
		}
	}

}
