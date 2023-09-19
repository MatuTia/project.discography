package project.discography.app;

import java.awt.EventQueue;

import project.discography.view.swing.DiscographySwingView;

public class DiscographyApp {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DiscographySwingView frame = new DiscographySwingView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
