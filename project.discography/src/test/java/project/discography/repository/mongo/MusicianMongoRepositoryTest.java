package project.discography.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import project.discography.model.Musician;

public class MusicianMongoRepositoryTest {

	private static final String COLLECTION_NAME = "test-musician-collection";
	private static final String DATABASE_NAME = "test-discography";

	private static MongoServer server;
	private static InetSocketAddress address;
	private MongoClient client;

	private MongoCollection<Document> collection;
	private MusicianMongoRepository repository;

	@BeforeClass
	public static void setUpServer() {
		server = new MongoServer(new MemoryBackend());
		address = server.bind();
	}

	@Before
	public void setUp() {
		client = new MongoClient(new ServerAddress(address));
		MongoDatabase database = client.getDatabase(DATABASE_NAME);
		database.drop();
		collection = database.getCollection(COLLECTION_NAME);
		repository = new MusicianMongoRepository(client, DATABASE_NAME, COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@AfterClass
	public static void shutDownServer() {
		server.shutdown();
	}

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(repository.findAllMusicians()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestMusicianToDatabase("1", "aMusician");
		addTestMusicianToDatabase("2", "otherMusician");
		assertThat(repository.findAllMusicians()).containsExactly(new Musician("1", "aMusician"),
				new Musician("2", "otherMusician"));
	}

	@Test
	public void testFindByIdNotFound() {
		assertThat(repository.findMusicianById("id")).isNull();
	}

	@Test
	public void testFindByIdFound() {
		addTestMusicianToDatabase("1", "aMusician");
		assertThat(repository.findMusicianById("1")).isEqualTo(new Musician("1", "aMusician"));
	}

	@Test
	public void testSaveMusician() {
		Musician musician = new Musician("1", "newMusician");
		repository.saveMusician(musician);
		assertThat(readAllMusiciansFromDatabase()).containsExactly(musician);
	}

	@Test
	public void testDeleteMusician() {
		addTestMusicianToDatabase("1", "toDelete");
		repository.deleteMusician("1");
		assertThat(readAllMusiciansFromDatabase()).isEmpty();
	}

	@Test
	public void testUpdateMusician() {
		addTestMusicianToDatabase("1", "toUpdate");
		Musician updated = new Musician("1", "updated");
		repository.updateMusician("1", updated);
		assertThat(readAllMusiciansFromDatabase()).containsExactly(updated);
	}

	private void addTestMusicianToDatabase(String id, String name) {
		collection.insertOne(new Document().append("id", id).append("name", name));
	}

	private List<Musician> readAllMusiciansFromDatabase() {
		return StreamSupport.stream(collection.find().spliterator(), false)
				.map(d -> new Musician(d.getString("id"), d.getString("name"))).collect(Collectors.toList());
	}

}
