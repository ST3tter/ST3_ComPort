package main;

import java.awt.EventQueue;

import user.UART;

public class Main {

	/**
	 * <b>Description:</b>
	 * <p>
	 * Main-Methode des Projektes.
	 * <p>
	 * Von hier wird das MainGUI und somit auch alle anderen Komponenten
	 * instanziiert.
	 * 
	 * @param args
	 *            Wird nicht bennoetigt
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {

					//hello
					// Instanziierung des UART Objekts
					UART frame = new UART();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
