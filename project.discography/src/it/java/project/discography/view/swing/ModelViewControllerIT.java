package project.discography.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import project.discography.controller.DiscographyController;
import project.discography.model.Musician;
import project.discography.repository.AlbumRepository;
import project.discography.repository.MusicianRepository;
import project.discography.repository.mongo.AlbumMongoRepository;
import project.discography.repository.mongo.MusicianMongoRepository;

@RunWith(GUITestRunner.class)
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {

	private static final String ALBUM = "album";
	private static final String MUSICIAN = "musician";
	private static final String DISCOGRAPHY = "discography";

	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.0");

	private MongoClient client;

	private AlbumRepository albumRepository;
	private MusicianRepository musicianRepository;

	private DiscographyController controller;
	private DiscographySwingView view;
	private FrameFixture window;

	@BeforeClass
	public static void setUpServer() {
		mongo.start();
	}

	@BeforeClass
	public static void shutDownServer() {
		mongo.close();
	}

	@Override
	protected void onSetUp() throws Exception {
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getFirstMappedPort()));
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

	@Test
	public void testAddMusician() {
		window.textBox("idMusician").enterText("1");
		window.textBox("nameMusician").enterText("newMusician");
		window.button(JButtonMatcher.withText("Add Musician")).click();
		assertThat(musicianRepository.findMusicianById("1")).isEqualTo(new Musician("1", "newMusician"));
	}

	@Test
	public void testDeleteMusician() {
		musicianRepository.saveMusician(new Musician("1", "toDelete"));
		GuiActionRunner.execute(() -> controller.allMusicians());
		window.list("musicians").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Musician")).click();
		assertThat(musicianRepository.findMusicianById("1")).isNull();
	}

}