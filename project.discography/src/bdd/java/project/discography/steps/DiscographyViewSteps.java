package project.discography.steps;

import static project.discography.steps.DatabaseSteps.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DiscographyViewSteps {

	private static final String MUSICIANS = "musicians";
	private static final String ALBUMS = "albums";

	private FrameFixture window;

	@BeforeAll
	public static void setUpThread() {
		FailOnThreadViolationRepaintManager.install();
	}

	@After
	public void tearDown() {
		if (window != null)
			window.cleanUp();
	}

	@Given("The discography view is shown")
	public void the_discography_view_is_shown() {
		application("project.discography.app.DiscographyApp").withArgs("--mongo-host=" + mongo.getHost(),
				"--mongo-port=" + DatabaseSteps.mongo.getFirstMappedPort(), "--database-name=" + DB_NAME,
				"--musician-collection-name=" + MUSICIAN_COLLECTION, "--album-collection-name=" + ALBUM_COLLECTION)
				.start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {

			@Override
			protected boolean isMatching(JFrame component) {
				return "Discography View".equals(component.getTitle()) && component.isShowing();
			}

		}).using(BasicRobot.robotWithCurrentAwtHierarchy());
	}

	@When("The user clicks the {string} button")
	public void the_user_clicks_the_button(String buttonName) {
		window.button(JButtonMatcher.withText(buttonName)).click();
	}

	@Then("The album list is cleared")
	public void the_album_list_is_cleared() {
		assertThat(window.list(ALBUMS).contents()).isEmpty();
	}

	@Then("The discography of the selected musician is show in the album list")
	public void the_discography_of_the_selected_musician_is_show_in_the_album_list() {
		assertThat(window.list(ALBUMS).contents()).allSatisfy(e -> assertThat(e).contains(ALBUM_FIXTURE_ID_MUSICIAN));
	}

	@Given("The user selects a musician from the musician list")
	public void the_user_selects_a_musician_from_the_musician_list() {
		window.list(MUSICIANS).selectItem(Pattern.compile(".*" + MUSICIAN_FIXTURE_2_NAME + ".*"));
	}

	@Given("The user selects an album from the album list")
	public void the_user_selects_an_album_from_the_album_list() {
		window.list(ALBUMS).selectItem(Pattern.compile(".*" + ALBUM_FIXTURE_2_TITLE + ".*"));
	}

	@Given("The user provides the musician data in the text fields")
	public void the_user_provides_the_musician_data_in_the_text_fields() {
		window.textBox("idMusician").enterText("3");
		window.textBox("nameMusician").enterText("newMusician");
	}

	@Given("The user provides the musician data in the text fields, specifying an existing id")
	public void the_user_provides_the_musician_data_in_the_text_fields_specifying_an_existing_id() {
		window.textBox("idMusician").enterText("2");
		window.textBox("nameMusician").enterText("newMusician");
	}

	@Given("The user provides the album data in the text fields")
	public void the_user_provides_the_album_data_in_the_text_fields() {
		window.textBox("idAlbum").enterText("C");
		window.textBox("titleAlbum").enterText("newAlbum");
	}

	@Given("The user provides the album data in the text fields, specifying an existing id")
	public void the_user_provides_the_album_data_in_the_text_fields_specifying_an_existing_id() {
		window.textBox("idAlbum").enterText("B");
		window.textBox("titleAlbum").enterText("newAlbum");

	}

	@Then("The musician list contains the new musician")
	public void the_musician_list_contains_the_new_musician() {
		assertThat(window.list(MUSICIANS).contents()).anySatisfy(e -> assertThat(e).contains("3", "newMusician"));
	}

	@Then("The albm list contains the new album")
	public void the_albm_list_contains_the_new_album() {
		assertThat(window.list(ALBUMS).contents())
				.anySatisfy(e -> assertThat(e).contains("C", "newAlbum", ALBUM_FIXTURE_ID_MUSICIAN));
	}

	@Then("The selected musician is removed from the musician list")
	public void the_selected_musician_is_removed_from_the_musician_list() {
		assertThat(window.list(MUSICIANS).contents()).noneSatisfy(e -> assertThat(e).contains(MUSICIAN_FIXTURE_2_NAME));
	}

	@Then("The selected album is removed from the album list")
	public void the_selected_album_is_removed_from_the_album_list() {
		assertThat(window.list(ALBUMS).contents()).noneSatisfy(e -> assertThat(e).contains(ALBUM_FIXTURE_2_TITLE)); 
	}

	@Given("The user provides the updated name of the musician in the text field")
	public void the_user_provides_the_updated_name_of_the_musician_in_the_text_field() {
		window.textBox("nameMusician").enterText("updated");
	}

	@Given("The user provides the updated title of the album in the text field")
	public void the_user_provides_the_updated_title_of_the_album_in_the_text_field() {
		window.textBox("titleAlbum").enterText("updated");
	}

	@Then("The selected musician is updated with the new name")
	public void the_selected_musician_is_updated_with_the_new_name() {
		assertThat(window.list(MUSICIANS).contents()).anySatisfy(e -> assertThat(e).contains("updated"));
	}

	@Then("The selected album is updated with the new title")
	public void the_selected_album_is_updated_with_the_new_title() {
		assertThat(window.list(ALBUMS).contents()).anySatisfy(e -> assertThat(e).contains("updated"));
	}

	@Then("An error is shown containing the name of the existing musician")
	public void an_error_is_shown_containing_the_name_of_the_existing_musician() {
		assertThat(window.label("error").text()).contains(MUSICIAN_FIXTURE_2_NAME);
	}

	@Then("An error is shown containing the name of the selected musician")
	public void an_error_is_shown_containing_the_name_of_the_selected_musician() {
		assertThat(window.label("error").text()).contains(MUSICIAN_FIXTURE_2_NAME);
	}

	@Then("An error is shown containing the title of the existing album")
	public void an_error_is_shown_containing_the_title_of_the_existing_album() {
		assertThat(window.label("error").text()).contains(ALBUM_FIXTURE_2_TITLE);
	}

	@Then("An error is shown containing the title of the selected album")
	public void an_error_is_shown_containing_the_title_of_the_selected_album() {
		assertThat(window.label("error").text()).contains(ALBUM_FIXTURE_2_TITLE);
	}

}