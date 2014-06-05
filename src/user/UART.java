package user;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
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
	
	private JComboBox<String> comPortComboBox;
	private JComboBox<String> baudrateComboBox;
	private JComboBox<String> dataBitsComboBox;
	private JComboBox<String> parityBitComboBox;
	private JComboBox<String> stopBitComboBox;

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

	/** Flag set if a new message is incoming (< detected)*/
	private boolean receivingMessage = false;

	/**
	 * Create the frame.
	 */
	public UART() {

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

		 //Ist schon ein Port geoeffnet wird nichts gemacht
		 if (serialPortOpen != false) {
		 System.out.println("Serialport already opened");
		 return;
		 }
		 
		 String selectedPort = (String) comPortComboBox.getSelectedItem();
		 
		 if(!selectedPort.contentEquals("Select port")){
			 
			 System.out.println("Oeffne Serialport");
			 
			 try {
				 // get COM Port Identifier
				 CommPortIdentifier commPortID = CommPortIdentifier.getPortIdentifier(selectedPort);
				
				 // open COM Port
				 serialPort = (SerialPort) commPortID.open("serial " + selectedPort, 2000);
				
				 // SerialSend und SerialReceive erzeugen
				 serialSend = new SerialSend(serialPort, this);
				 serialRecv = new SerialRecv(serialPort, this);
				 
				 Map<String, Integer> serialConfigurationMap = getConfigurationMap();
				 
				 System.out.println(Integer.parseInt((String) baudrateComboBox.getSelectedItem()));
				 System.out.println((String) dataBitsComboBox.getSelectedItem());
				 System.out.println((String) stopBitComboBox.getSelectedItem());
				 System.out.println((String) parityBitComboBox.getSelectedItem());
				 
				
				 serialPort.setSerialPortParams(Integer.parseInt((String) baudrateComboBox.getSelectedItem()) ,
				 serialConfigurationMap.get((String) dataBitsComboBox.getSelectedItem()),
				 serialConfigurationMap.get((String) stopBitComboBox.getSelectedItem()),
				 serialConfigurationMap.get((String) parityBitComboBox.getSelectedItem()));
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

				serialPortOpen = true;
		 }
		 else{
			 System.out.println("Please select a port");
		 }
		

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
			
		} else {
			System.out.println("Serialport already closed");
		}
	}

	private void initGUI() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 350, 400);
		this.setResizable(false);

		contentPane = new JPanel(new MigLayout("", "[100][250]",
				"[][][][][]40[]10[]40[]")); // Row constraints

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

		String[] baud = { "1200", "2400", "4800", "9600", "19200", "38400",
				"57600", "115200", "230400", "460800", "921600" };
		String[] data = { "5", "6", "7", "8" };
		String[] par = { "even", "odd", "none" };
		String[] stop = { "1", "1.5", "2" };
		
		comPortComboBox = new JComboBox<String>(new String[] {"Select port"});
		comPortComboBox.setSelectedItem("Select port");
		
		
	    // Combobox Items mit den Namen der Ports hinzufuegen
	    Enumeration enumComm = CommPortIdentifier.getPortIdentifiers();
	    while (enumComm.hasMoreElements()) {
	    	CommPortIdentifier serialPortId = (CommPortIdentifier) enumComm.nextElement();
	     	if(serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	     		
	     		//Darauf achten das nur tty und com Schnittstellen hinzugefuegt werden
	     		if((serialPortId.getName().startsWith("tty")) | (serialPortId.getName().startsWith("com"))){
	     			comPortComboBox.addItem(serialPortId.getName());
	     		}
	    	}
	    }

		baudrateComboBox = new JComboBox<String>(baud);
		baudrateComboBox.setSelectedItem("9600");

		dataBitsComboBox = new JComboBox<String>(data);
		dataBitsComboBox.setSelectedItem("8");

		parityBitComboBox = new JComboBox<String>(par);
		parityBitComboBox.setSelectedItem("none");

		stopBitComboBox = new JComboBox<String>(stop);
		stopBitComboBox.setSelectedItem("1");

		contentPane.add(comPortComboBox, "width 150, height 30, cell 1 0");
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
	
	private Map<String, Integer> getConfigurationMap(){
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		map.put("5", SerialPort.DATABITS_5);
		map.put("6", SerialPort.DATABITS_6);
		map.put("7", SerialPort.DATABITS_7);
		map.put("8", SerialPort.DATABITS_8);
		
		map.put("1", SerialPort.STOPBITS_1);
		map.put("1.5", SerialPort.STOPBITS_1_5);
		map.put("2", SerialPort.STOPBITS_2);
		
		map.put("none", SerialPort.PARITY_NONE);
		map.put("even", SerialPort.PARITY_EVEN);
		map.put("odd", SerialPort.PARITY_ODD);
		
		return map;
	}
	
	
	public void putByte(byte newByte) {

		byte[] ar = new byte[1];
		ar[0] = newByte;
		msg = new String(ar);

		if (newByte == '<') {
			// new Message Start detected
			// reset Msg String and add Start-delimiter
			//outmsg = msg;
			receivingMessage = true;

		} else if (newByte == '>' && receivingMessage == true) {
			// End of Message
			// Put Message in Receive-Buffer
			//outmsg = outmsg.concat(msg);
			try {
				inQueue.put(outmsg);
				testMethod(outmsg);
			} catch (InterruptedException e) {
				// TODO handle Exception
			}
			msg = "";
			outmsg = "";
			receivingMessage = false;
		} else if (receivingMessage == true) {
			// Add Byte to receive Message String
			outmsg = outmsg.concat(msg);
		} else {
			// Dont process Data
		}
	}
	
	private void testMethod(String inString)
	{
		String[] splittedString = inString.split(";");
		System.out.println("Event ID: " +splittedString[0] +" Timestamp: " +splittedString[1] +" Task: " +splittedString[2]);
	}

}
