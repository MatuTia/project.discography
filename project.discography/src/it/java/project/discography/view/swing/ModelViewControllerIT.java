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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.mongodb.MongoClient;

import project.discography.controller.DiscographyController;
import project.discography.guice.DatabaseName;
import project.discography.guice.DiscographyAppDefaultModule;
import project.discography.guice.MongoHost;
import project.discography.guice.MongoPort;
import project.discography.model.Album;
import project.discography.model.Musician;
import project.discography.repository.AlbumRepository;
import project.discography.repository.MusicianRepository;

@RunWith(GUITestRunner.class)
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {

	private static final String DISCOGRAPHY = "discography";

	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.0");

	@Inject
	private MongoClient client;

	@Inject
	private AlbumRepository albumRepository;

	@Inject
	private MusicianRepository musicianRepository;

	@Inject
	private DiscographySwingView view;

	private DiscographyController controller;
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

		final Module moduleForTesting = Modules.override(new DiscographyAppDefaultModule()).with(new AbstractModule() {

			@Override
			protected void configure() {
				bind(String.class).annotatedWith(DatabaseName.class).toInstance(DISCOGRAPHY);
				bind(String.class).annotatedWith(MongoHost.class).toInstance(mongo.getHost());
				bind(Integer.class).annotatedWith(MongoPort.class).toInstance(mongo.getFirstMappedPort());
			}
		});

		final Injector injector = Guice.createInjector(moduleForTesting);

		view = GuiActionRunner.execute(() -> {
			injector.injectMembers(this);
			client.getDatabase(DISCOGRAPHY).drop();
			controller = view.getController();
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
		selectNewMusician("1", "toDelete");
		window.button(JButtonMatcher.withText("Delete Musician")).click();
		assertThat(musicianRepository.findMusicianById("1")).isNull();
	}

	@Test
	public void testUpdateMusician() {
		selectNewMusician("1", "toUpdate");
		window.textBox("nameMusician").enterText("updated");
		window.button(JButtonMatcher.withText("Update Musician")).click();
		assertThat(musicianRepository.findMusicianById("1")).isEqualTo(new Musician("1", "updated"));
	}

	@Test
	public void testAddAlbum() {
		selectNewMusician("1", "aMusician");
		window.textBox("idAlbum").enterText("A");
		window.textBox("titleAlbum").enterText("newAlbum");
		window.button(JButtonMatcher.withText("Add Album")).click();
		assertThat(albumRepository.findAlbumById("A")).isEqualTo(new Album("A", "newAlbum", "1"));
	}

	@Test
	public void testDeleteAlbum() {
		albumRepository.saveAlbum(new Album("A", "toDelete", "1"));
		selectNewMusician("1", "aMusician");
		window.list("albums").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Album")).click();
		assertThat(albumRepository.findAlbumById("A")).isNull();
	}

	@Test
	public void testUpdateAlbum() {
		albumRepository.saveAlbum(new Album("A", "toUpdate", "1"));
		selectNewMusician("1", "aMusician");
		window.list("albums").selectItem(0);
		window.textBox("titleAlbum").enterText("updated");
		window.button(JButtonMatcher.withText("Update Album")).click();
		assertThat(albumRepository.findAlbumById("A")).isEqualTo(new Album("A", "updated", "1"));
	}

	private void selectNewMusician(String id, String name) {
		musicianRepository.saveMusician(new Musician("1", "aMusician"));
		GuiActionRunner.execute(() -> controller.allMusicians());
		window.list("musicians").selectItem(0);
	}

}