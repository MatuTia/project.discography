package project.discography.view.swing;

import static org.mockito.Mockito.mockitoSession;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JListFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import project.discography.controller.DiscographyController;
import project.discography.model.Album;
import project.discography.model.Musician;

@RunWith(GUITestRunner.class)
public class DiscographySwingViewTest extends AssertJSwingJUnitTestCase {

	@Mock
	private DiscographyController controller;
	private MockitoSession session;

	private DiscographySwingView view;
	private FrameFixture window;

	@Override
	protected void onSetUp() throws Exception {
		session = mockitoSession().initMocks(this).strictness(Strictness.STRICT_STUBS).startMocking();
		GuiActionRunner.execute(() -> {
			view = new DiscographySwingView();
			view.setController(controller);
			return view;
		});
		window = new FrameFixture(robot(), view);
		window.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		session.finishMocking();
		super.onTearDown();
	}

	@Test
	@GUITest
	public void testInitialState() {
		window.label(JLabelMatcher.withText("id Musician"));
		window.label(JLabelMatcher.withText("name Musician"));
		window.label(JLabelMatcher.withText("id Album"));
		window.label(JLabelMatcher.withText("title Album"));
		window.label("error").requireText(" ");

		window.textBox("idMusician").requireEmpty();
		window.textBox("nameMusician").requireEmpty();
		window.textBox("idAlbum").requireEmpty();
		window.textBox("titleAlbum").requireEmpty();

		window.list("musicians");
		window.list("albums");

		window.button(JButtonMatcher.withText("Add Musician")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete Musician")).requireDisabled();
		window.button(JButtonMatcher.withText("Update Musician")).requireDisabled();
		window.button(JButtonMatcher.withText("Add Album")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete Album")).requireDisabled();
		window.button(JButtonMatcher.withText("Update Album")).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddMusicianButtonShouldBeEnableWhenMusicianFieldsAreNotEmpty() {
		window.textBox("idMusician").enterText("1");
		window.textBox("nameMusician").enterText("newMusician");
		window.button(JButtonMatcher.withText("Add Musician")).requireEnabled();
	}

	@Test
	@GUITest
	public void testAddMusicianButtonShouldBeDisableWhenAtLeastOneMusicianFieldIsBlanck() {
		JTextComponentFixture id = window.textBox("idMusician");
		JTextComponentFixture name = window.textBox("nameMusician");
		JButtonFixture button = window.button(JButtonMatcher.withText("Add Musician"));

		id.enterText("1");
		name.enterText(" ");
		button.requireDisabled();

		id.setText("");
		name.setText("");

		id.enterText(" ");
		name.enterText("newMusician");
		button.requireDisabled();
	}

	@Test
	public void testAddMusicianButtonShouldDelegateToDiscographyControllerNewMusician() {
		window.textBox("idMusician").enterText("1");
		window.textBox("nameMusician").enterText("newMusician");
		window.button(JButtonMatcher.withText("Add Musician")).click();
		verify(controller).newMusician(new Musician("1", "newMusician"));
	}

	@Test
	@GUITest
	public void testDeleteMusicianButtonShouldBeEnabledOnlyWhenAMusicianIsSelected() {
		JListFixture list = window.list("musicians");
		JButtonFixture button = window.button(JButtonMatcher.withText("Delete Musician"));
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(new Musician("1", "toDelete")));

		list.selectItem(0);
		button.requireEnabled();

		list.clearSelection();
		button.requireDisabled();
	}

	@Test
	public void testDeleteMusicianButtonShouldDelegateToDiscographyControllerDeleteMusician() {
		Musician toDelete = new Musician("1", "toDelete");
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(toDelete));
		window.list("musicians").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Musician")).click();
		verify(controller).deleteMusician(toDelete);
	}

	@Test
	@GUITest
	public void testUpdateMusicianButtonShouldBeEnabledWhenMusicianIsSelectedAndNameFieldIsNotEmpty() {
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(new Musician("1", "toUpdate")));
		window.list("musicians").selectItem(0);
		window.textBox("nameMusician").enterText("updated");
		window.button(JButtonMatcher.withText("Update Musician")).requireEnabled();
	}

	@Test
	@GUITest
	public void testUpdateMusicianButtonShouldBeDisabledWhenNameFieldIsBlankOrMusicianIsNotSelected() {
		JListFixture list = window.list("musicians");
		JTextComponentFixture name = window.textBox("nameMusician");
		JButtonFixture button = window.button(JButtonMatcher.withText("Update Musician"));
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(new Musician("1", "toUpdate")));

		name.enterText("updated");
		button.requireDisabled();

		name.setText("");

		list.selectItem(0);
		name.enterText(" ");
		button.requireDisabled();
	}

	@Test
	public void testUpdateMusicianButtonShouldDelegateToDiscographyContollerUpdateMusician() {
		Musician toUpdate = new Musician("1", "toUpdate");
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(toUpdate));
		window.list("musicians").selectItem(0);
		window.textBox("nameMusician").enterText("updated");
		window.button(JButtonMatcher.withText("Update Musician")).click();
		verify(controller).updateMusician(toUpdate, new Musician("1", "updated"));
	}

	@Test
	public void testListMusicianShouldDelegateToDiscographyFindAllAlbumsOfMusician() {
		Musician musician = new Musician("1", "aMusician");
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(musician));
		window.list("musicians").selectItem(0);
		verify(controller).musicianDiscography(musician);
		window.list("musicians").clearSelection();
		verifyNoMoreInteractions(controller);
	}

	@Test
	@GUITest
	public void testAddAlbumButtonShouldBeEnableWhenMusicianIsSelectedAndAlbumTextFieldsAreNotEmpty() {
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(new Musician("1", "aMusician")));
		window.list("musicians").selectItem(0);
		window.textBox("idAlbum").enterText("A");
		window.textBox("titleAlbum").enterText("newAlbum");
		window.button(JButtonMatcher.withText("Add Album")).requireEnabled();
	}

	@Test
	@GUITest
	public void testAddAlbumButtonShouldBeDisableWhenAtLeastOneFieldIsEmptyOrMusicianIsNotSelected() {
		JListFixture list = window.list("musicians");
		JTextComponentFixture id = window.textBox("idAlbum");
		JTextComponentFixture title = window.textBox("titleAlbum");
		JButtonFixture button = window.button(JButtonMatcher.withText("Add Album"));
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(new Musician("1", "aMusician")));

		id.enterText("A");
		title.enterText("newAlbum");
		button.requireDisabled();

		id.setText("");
		title.setText("");

		list.selectItem(0);
		id.enterText(" ");
		title.enterText("newAlbum");
		button.requireDisabled();

		id.setText("");
		title.setText("");
		list.clearSelection();

		list.selectItem(0);
		id.enterText("A");
		title.enterText(" ");
		button.requireDisabled();
	}

	@Test
	public void testAddAlbumButtonShouldDelegateToDiscographyControllerNewAlbum() {
		Musician musician = new Musician("1", "aMusician");
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(musician));
		window.list("musicians").selectItem(0);
		window.textBox("idAlbum").enterText("A");
		window.textBox("titleAlbum").enterText("newAlbum");
		window.button(JButtonMatcher.withText("Add Album")).click();
		verify(controller).newAlbum(musician, new Album("A", "newAlbum", "1"));
	}

	@Test
	@GUITest
	public void testDeleteAlbumButtonShouldBeEnabledOnlyWhenMusicianAndAlbumIsSelected() {
		JListFixture musicians = window.list("musicians");
		JListFixture albums = window.list("albums");
		JButtonFixture button = window.button(JButtonMatcher.withText("Delete Album"));
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(new Musician("1", "aMusician")));
		GuiActionRunner.execute(() -> view.getAlbumListModel().addElement(new Album("A", "toDelete", "1")));

		musicians.selectItem(0);
		button.requireDisabled();

		albums.selectItem(0);
		button.requireEnabled();

		musicians.clearSelection();
		button.requireDisabled();
	}

	@Test
	public void testDeleteAlbumButtonShouldDelegateToDiscographyControllerDeleteAlbum() {
		Musician musician = new Musician("1", "aMusician");
		Album album = new Album("A", "toDelete", "1");
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(musician));
		GuiActionRunner.execute(() -> view.getAlbumListModel().addElement(album));
		window.list("musicians").selectItem(0);
		window.list("albums").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Album")).click();
		verify(controller).deleteAlbum(musician, album);
	}

	@Test
	@GUITest
	public void testUpdateAlbumButtonSholdBeEnableWhenMusicianAndAlbumAreSelectedAndTitleFieldIsNotEmpty() {
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(new Musician("1", "aMusician")));
		GuiActionRunner.execute(() -> view.getAlbumListModel().addElement(new Album("A", "toUpdate", "1")));
		window.list("musicians").selectItem(0);
		window.list("albums").selectItem(0);
		window.textBox("titleAlbum").enterText("updated");
		window.button(JButtonMatcher.withText("Update Album")).requireEnabled();
	}

	@Test
	@GUITest
	public void testUpdateAlbumButtonSholdBeDisableWhenTitleFieldIsBlanckOrMusicianOrAlbumAreNotSelected() {
		JListFixture musicians = window.list("musicians");
		JListFixture albums = window.list("albums");
		JTextComponentFixture title = window.textBox("titleAlbum");
		JButtonFixture button = window.button(JButtonMatcher.withText("Update Album"));
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(new Musician("1", "aMusician")));
		GuiActionRunner.execute(() -> view.getAlbumListModel().addElement(new Album("A", "toUpdate", "1")));

		musicians.selectItem(0);
		albums.selectItem(0);
		title.enterText(" ");
		button.requireDisabled();

		musicians.clearSelection();
		albums.clearSelection();
		title.setText("");

		musicians.selectItem(0);
		title.enterText("updated");
		button.requireDisabled();

		musicians.clearSelection();
		albums.clearSelection();
		title.setText("");

		albums.selectItem(0);
		title.enterText(" ");
		button.requireDisabled();
	}

	@Test
	public void testUpdateAlbumButtonShouldDelegateToDiscographyControllerUpdateAlbum() {
		Musician musician = new Musician("1", "aMusician");
		Album toUpdate = new Album("A", "toUpdate", "1");
		GuiActionRunner.execute(() -> view.getMusicianListModel().addElement(musician));
		GuiActionRunner.execute(() -> view.getAlbumListModel().addElement(toUpdate));
		window.list("musicians").selectItem(0);
		window.list("albums").selectItem(0);
		window.textBox("titleAlbum").enterText("updated");
		window.button(JButtonMatcher.withText("Update Album")).click();
		verify(controller).updateAlbum(musician, toUpdate, new Album("A", "updated", "1"));
	}

}
