package project.discography.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import project.discography.controller.DiscographyController;
import project.discography.model.Musician;
import project.discography.repository.AlbumRepository;
import project.discography.repository.MusicianRepository;
import project.discography.repository.mongo.AlbumMongoRepository;
import project.discography.repository.mongo.MusicianMongoRepository;

@RunWith(GUITestRunner.class)
public class DiscographySwingViewIT extends AssertJSwingJUnitTestCase {

	private static final String ALBUM = "album";
	private static final String MUSICIAN = "musician";
	private static final String DISCOGRAPHY = "discography";
	private static MongoServer server;
	private static InetSocketAddress address;

	private MongoClient client;

	private MusicianRepository musicianRepository;
	private AlbumRepository albumRepository;

	private DiscographySwingView view;
	private DiscographyController controller;
	private FrameFixture window;

	@BeforeClass
	public static void setUpServer() {
		server = new MongoServer(new MemoryBackend());
		address = server.bind();
	}

	@Override
	protected void onSetUp() throws Exception {
		client = new MongoClient(new ServerAddress(address));
		client.getDatabase(DISCOGRAPHY).drop();
		musicianRepository = new MusicianMongoRepository(client, DISCOGRAPHY, MUSICIAN);
		albumRepository = new AlbumMongoRepository(client, DISCOGRAPHY, ALBUM);

		GuiActionRunner.execute(() -> {
			view = new DiscographySwingView();
			controller = new DiscographyController(view, musicianRepository, albumRepository);
			view.setController(controller);
			return view;
		});

		window = new FrameFixture(robot(), view);
		window.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		client.close();
		super.onTearDown();
	}

	@AfterClass
	public static void shutDownServer() {
		server.shutdown();
	}

	@Test
	@GUITest
	public void testAllMusicians() {
		musicianRepository.saveMusician(new Musician("1", "aMusician"));
		musicianRepository.saveMusician(new Musician("2", "anotherMusician"));
		GuiActionRunner.execute(() -> controller.allMusicians());
		assertThat(window.list("musicians").contents()).containsExactly("1 - aMusician", "2 - anotherMusician");
	}

}
