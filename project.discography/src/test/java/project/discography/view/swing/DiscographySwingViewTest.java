package project.discography.view.swing;

import static org.mockito.Mockito.mockitoSession;
import static org.mockito.Mockito.verify;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import project.discography.controller.DiscographyController;
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
}
