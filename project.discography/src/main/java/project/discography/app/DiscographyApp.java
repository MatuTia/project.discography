package project.discography.app;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import project.discography.view.swing.DiscographySwingView;

@Command(mixinStandardHelpOptions = true)
public class DiscographyApp implements Callable<Void> {

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--database-name" }, description = "Database name")
	private String databaseName = "discography";

	@Option(names = { "--album-collection-name" }, description = "Album collection name")
	private String albumCollectionName = "album";

	@Option(names = { "--musician-collection-name" }, description = "Musician collection name")
	private String musicianCollectionName = "musician";

	public static void main(String[] args) {
		new CommandLine(new DiscographyApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				DiscographySwingView frame = new DiscographySwingView();
				frame.setVisible(true);
			} catch (Exception e) {
				Logger.getLogger("DiscographyApp").log(Level.SEVERE, "Exception", e);
			}
		});
		return null;
	}

}
