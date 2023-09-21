package project.discography.steps;

import org.bson.Document;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;

public class DatabaseSteps {
	static final String ALBUM_COLLECTION = "Album-Collection";
	static final String DB_NAME = "Discography";
	static final String MUSICIAN_COLLECTION = "Musician-Collection";

	static final String MUSICIAN_FIXTURE_1_ID = "1";
	static final String MUSICIAN_FIXTURE_1_NAME = "aMusician";

	static final String MUSICIAN_FIXTURE_2_ID = "2";
	static final String MUSICIAN_FIXTURE_2_NAME = "anotherMusician";

	static final String ALBUM_FIXTURE_ID_MUSICIAN = MUSICIAN_FIXTURE_2_ID;

	static final String ALBUM_FIXTURE_1_ID = "A";
	static final String ALBUM_FIXTURE_1_TITLE = "anAlbum";

	static final String ALBUM_FIXTURE_2_ID = "B";
	static final String ALBUM_FIXTURE_2_TITLE = "anotherAlbum";

	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.0");

	MongoClient client;

	@BeforeAll
	public static void setUpServer() {
		mongo.start();
	}

	@Before
	public void setUp() {
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getFirstMappedPort()));
		client.getDatabase(DB_NAME).drop();
	}

	@After
	public void tearDown() {
		client.close();
	}

	@AfterAll
	public static void shutDownServer() {
		mongo.stop();
	}

	@Given("The database contains a few musicians")
	public void the_database_contains_a_few_musicians() {
		addTestMusicianToDatabase(MUSICIAN_FIXTURE_1_ID, MUSICIAN_FIXTURE_1_NAME);
		addTestMusicianToDatabase(MUSICIAN_FIXTURE_2_ID, MUSICIAN_FIXTURE_2_NAME);
	}

	private void addTestMusicianToDatabase(String id, String name) {
		client.getDatabase(DB_NAME).getCollection(MUSICIAN_COLLECTION)
				.insertOne(new Document().append("id", id).append("name", name));
	}

	@Given("The database contains a few albums")
	public void the_database_contains_a_few_albums() {
		addTestAlbumToDatabase(ALBUM_FIXTURE_1_ID, ALBUM_FIXTURE_1_TITLE, ALBUM_FIXTURE_ID_MUSICIAN);
		addTestAlbumToDatabase(ALBUM_FIXTURE_2_ID, ALBUM_FIXTURE_2_TITLE, ALBUM_FIXTURE_ID_MUSICIAN);
	}

	private void addTestAlbumToDatabase(String id, String title, String musicianId) {
		client.getDatabase(DB_NAME).getCollection(ALBUM_COLLECTION)
				.insertOne(new Document().append("id", id).append("title", title).append("musician", musicianId));
	}

	@Given("The selected musician is in the meantime removed from the database")
	public void the_selected_musician_is_in_the_meantime_removed_from_the_database() {
		client.getDatabase(DB_NAME).getCollection(MUSICIAN_COLLECTION)
				.deleteOne(Filters.eq("id", MUSICIAN_FIXTURE_2_ID));
	}

	@Given("The selected album is in the meantime removed from the database")
	public void the_selected_album_is_in_the_meantime_removed_from_the_database() {
		client.getDatabase(DB_NAME).getCollection(ALBUM_COLLECTION).deleteOne(Filters.eq("id", ALBUM_FIXTURE_2_ID));
	}

	@Given("The musician who will be selected was removed the database")
	public void the_musician_who_will_be_selected_was_removed_the_database() {
		client.getDatabase(DB_NAME).getCollection(MUSICIAN_COLLECTION)
				.deleteOne(Filters.eq("id", MUSICIAN_FIXTURE_2_ID));
	}

}