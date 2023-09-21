package project.discography.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import project.discography.controller.DiscographyController;
import project.discography.guice.DiscographyAppDefaultModule;
import project.discography.model.Album;
import project.discography.model.Musician;
import project.discography.repository.AlbumRepository;
import project.discography.repository.MusicianRepository;

@RunWith(GUITestRunner.class)
public class DiscographySwingViewIT extends AssertJSwingJUnitTestCase {

	private static final String DISCOGRAPHY = "discography";
	private static MongoServer server;
	private static InetSocketAddress address;

	@Inject
	private MongoClient client;

	@Inject
	private MusicianRepository musicianRepository;

	@Inject
	private AlbumRepository albumRepository;

	@Inject
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

		final Module moduleForTesting = Modules.override(new DiscographyAppDefaultModule().databaseName(DISCOGRAPHY))
				.with(new AbstractModule() {

					@Override
					protected void configure() {
						bind(MongoClient.class).toInstance(new MongoClient(new ServerAddress(address)));
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

	@Test
	@GUITest
	public void testAddMusicianButtonSucces() {
		window.textBox("idMusician").enterText("1");
		window.textBox("nameMusician").enterText("newMusician");
		window.button(JButtonMatcher.withText("Add Musician")).click();
		assertThat(window.list("musicians").contents()).containsExactly("1 - newMusician");
	}

	@Test
	@GUITest
	public void testAddMusicianButtonErrorDuplicateMusicianId() {
		musicianRepository.saveMusician(new Musician("1", "existingMusician"));
		window.textBox("idMusician").enterText("1");
		window.textBox("nameMusician").enterText("newMusician");
		window.button(JButtonMatcher.withText("Add Musician")).click();
		assertThat(window.list("musicians").contents()).isEmpty();
		window.label("error").requireText("Already exist a musician with id 1: 1 - existingMusician");
	}

	@Test
	@GUITest
	public void testDeleteMusicianButtongSucces() {
		GuiActionRunner.execute(() -> controller.newMusician(new Musician("1", "toDelete")));
		window.list("musicians").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Musician")).click();
		assertThat(window.list("musicians").contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testDeleteMusicianButtongErrorNotFoundMusician() {
		GuiActionRunner.execute(() -> {
			controller.newMusician(new Musician("1", "toDelete"));
			view.getAlbumListModel().addElement(new Album("A", "anAlbum", "1"));
		});
		window.list("musicians").selectItem(0);
		musicianRepository.deleteMusician("1");
		window.button(JButtonMatcher.withText("Delete Musician")).click();
		assertThat(window.list("musicians").contents()).isEmpty();
		assertThat(window.list("albums").contents()).isEmpty();
		window.label("error").requireText("Not exist a musician with id 1: 1 - toDelete");
	}

	@Test
	@GUITest
	public void testUpdateMusicianButtonSucces() {
		GuiActionRunner.execute(() -> controller.newMusician(new Musician("1", "toUpdate")));
		window.list("musicians").selectItem(0);
		window.textBox("nameMusician").enterText("updated");
		window.button(JButtonMatcher.withText("Update Musician")).click();
		assertThat(window.list("musicians").contents()).containsExactly("1 - updated");
	}

	@Test
	@GUITest
	public void testUpdateMusicianButtonErrorNotFoundMusician() {
		GuiActionRunner.execute(() -> {
			controller.newMusician(new Musician("1", "toUpdate"));
			view.getAlbumListModel().addElement(new Album("A", "anAlbum", "1"));
		});
		window.list("musicians").selectItem(0);
		musicianRepository.deleteMusician("1");
		window.textBox("nameMusician").enterText("updated");
		window.button(JButtonMatcher.withText("Update Musician")).click();
		assertThat(window.list("musicians").contents()).isEmpty();
		assertThat(window.list("albums").contents()).isEmpty();
		window.label("error").requireText("Not exist a musician with id 1: 1 - toUpdate");
	}

	@Test
	@GUITest
	public void testMusicianDiscographySucces() {
		GuiActionRunner.execute(() -> controller.newMusician(new Musician("1", "aMusician")));
		albumRepository.saveAlbum(new Album("A", "anAlbum", "1"));
		window.list("musicians").selectItem(0);
		assertThat(window.list("albums").contents()).containsExactly("A - anAlbum - 1");
	}

	@Test
	@GUITest
	public void testMusicianDiscographyErrorNotFoundMusician() {
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(new Musician("1", "aMusician")));
		albumRepository.saveAlbum(new Album("A", "anAlbum", "1"));
		window.list("musicians").selectItem(0);
		assertThat(window.list("musicians").contents()).isEmpty();
		assertThat(window.list("albums").contents()).isEmpty();
		window.label("error").requireText("Not exist a musician with id 1: 1 - aMusician");
	}

	@Test
	@GUITest
	public void testAddAlbumButtonSucces() {
		GuiActionRunner.execute(() -> controller.newMusician(new Musician("1", "aMusician")));
		window.list("musicians").selectItem(0);
		window.textBox("idAlbum").enterText("A");
		window.textBox("titleAlbum").enterText("newAlbum");
		window.button(JButtonMatcher.withText("Add Album")).click();
		assertThat(window.list("musicians").contents()).containsExactly("1 - aMusician");
		assertThat(window.list("albums").contents()).containsExactly("A - newAlbum - 1");
	}

	@Test
	@GUITest
	public void testAddAlbumButtonErrorMusiciaNotFound() {
		GuiActionRunner.execute(() -> {
			Musician aMusician = new Musician("1", "aMusician");
			controller.newMusician(aMusician);
			controller.newAlbum(aMusician, new Album("Z", "anAlbum", "1"));
		});
		window.list("musicians").selectItem(0);
		musicianRepository.deleteMusician("1");
		window.textBox("idAlbum").enterText("A");
		window.textBox("titleAlbum").enterText("newAlbum");
		window.button(JButtonMatcher.withText("Add Album")).click();
		assertThat(window.list("musicians").contents()).isEmpty();
		assertThat(window.list("albums").contents()).isEmpty();
		window.label("error").requireText("Not exist a musician with id 1: 1 - aMusician");
	}

	@Test
	@GUITest
	public void testAddAlbumButtonErrorDuplicateAlbumID() {
		GuiActionRunner.execute(() -> controller.newMusician(new Musician("1", "aMusician")));
		window.list("musicians").selectItem(0);
		albumRepository.saveAlbum(new Album("A", "existingAlbum", "1"));
		window.textBox("idAlbum").enterText("A");
		window.textBox("titleAlbum").enterText("newAlbum");
		window.button(JButtonMatcher.withText("Add Album")).click();
		assertThat(window.list("musicians").contents()).containsExactly("1 - aMusician");
		assertThat(window.list("albums").contents()).isEmpty();
		window.label("error").requireText("Already exist an album with id A: A - existingAlbum - 1");
	}

	@Test
	@GUITest
	public void testDeleteAlbumButtonSucces() {
		GuiActionRunner.execute(() -> {
			Musician aMusician = new Musician("1", "aMusician");
			controller.newMusician(aMusician);
			controller.newAlbum(aMusician, new Album("A", "toDelete", "1"));
		});
		window.list("musicians").selectItem(0);
		window.list("albums").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Album")).click();
		assertThat(window.list("musicians").contents()).containsExactly("1 - aMusician");
		assertThat(window.list("albums").contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testDeleteAlbumButtonErrorNotFoundMusician() {
		GuiActionRunner.execute(() -> controller.newMusician(new Musician("1", "aMusician")));
		window.list("musicians").selectItem(0);
		GuiActionRunner.execute(() -> view.getAlbumListModel().addElement(new Album("A", "toDelete", "1")));
		musicianRepository.deleteMusician("1");
		window.list("albums").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Album")).click();
		assertThat(window.list("musicians").contents()).isEmpty();
		assertThat(window.list("albums").contents()).isEmpty();
		window.label("error").requireText("Not exist a musician with id 1: 1 - aMusician");
	}

	@Test
	@GUITest
	public void testDeleteAlbumButtonErrorNotFoundAlbum() {
		GuiActionRunner.execute(() -> controller.newMusician(new Musician("1", "aMusician")));
		window.list("musicians").selectItem(0);
		GuiActionRunner.execute(() -> view.getAlbumListModel().addElement(new Album("A", "toDelete", "1")));
		window.list("albums").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Album")).click();
		assertThat(window.list("musicians").contents()).containsExactly("1 - aMusician");
		assertThat(window.list("albums").contents()).isEmpty();
		window.label("error").requireText("Not exist an album with id A: A - toDelete - 1");
	}

	@Test
	@GUITest
	public void testUpdateAlbumButtonSucces() {
		GuiActionRunner.execute(() -> {
			Musician musician = new Musician("1", "aMusician");
			controller.newMusician(musician);
			controller.newAlbum(musician, new Album("A", "toUpdate", "1"));
		});
		window.list("musicians").selectItem(0);
		window.list("albums").selectItem(0);
		window.textBox("titleAlbum").enterText("updated");
		window.button(JButtonMatcher.withText("Update Album")).click();
		assertThat(window.list("musicians").contents()).containsExactly("1 - aMusician");
		assertThat(window.list("albums").contents()).containsExactly("A - updated - 1");
	}

	@Test
	@GUITest
	public void testUpdateAlbumButtonErrorNotFoundMusician() {
		GuiActionRunner.execute(() -> controller.newMusician(new Musician("1", "aMusician")));
		window.list("musicians").selectItem(0);
		GuiActionRunner.execute(() -> view.getAlbumListModel().addElement(new Album("A", "toUpdate", "1")));
		musicianRepository.deleteMusician("1");
		window.list("albums").selectItem(0);
		window.textBox("titleAlbum").enterText("updated");
		window.button(JButtonMatcher.withText("Update Album")).click();
		assertThat(window.list("musicians").contents()).isEmpty();
		assertThat(window.list("albums").contents()).isEmpty();
		window.label("error").requireText("Not exist a musician with id 1: 1 - aMusician");
	}

	@Test
	@GUITest
	public void testUpdateAlbumButtonErrorNotFoundAlbum() {
		GuiActionRunner.execute(() -> controller.newMusician(new Musician("1", "aMusician")));
		window.list("musicians").selectItem(0);
		GuiActionRunner.execute(() -> view.getAlbumListModel().addElement(new Album("A", "toUpdate", "1")));
		window.list("albums").selectItem(0);
		window.textBox("titleAlbum").enterText("updated");
		window.button(JButtonMatcher.withText("Update Album")).click();
		assertThat(window.list("musicians").contents()).containsExactly("1 - aMusician");
		assertThat(window.list("albums").contents()).isEmpty();
		window.label("error").requireText("Not exist an album with id A: A - toUpdate - 1");
	}

}
