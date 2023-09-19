package project.discography.app;

import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import project.discography.view.swing.DiscographySwingView;

public class DiscographyApp {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				DiscographySwingView frame = new DiscographySwingView();
				frame.setVisible(true);
			} catch (Exception e) {
				Logger.getLogger("DiscographyApp").log(Level.SEVERE, "Exception", e);
			}
		});
	}

}
