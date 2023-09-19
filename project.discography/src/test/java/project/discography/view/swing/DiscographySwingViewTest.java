package project.discography.view.swing;

import static org.mockito.Mockito.mockitoSession;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import project.discography.controller.DiscographyController;

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

}
