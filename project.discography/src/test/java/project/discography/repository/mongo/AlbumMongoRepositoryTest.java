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
import project.discography.model.Album;

public class AlbumMongoRepositoryTest {

	private static final String DATABASE_NAME = "test-discography";
	private static final String COLLECTION_NAME = "test-album-collection";

	private static MongoServer server;
	private static InetSocketAddress address;
	private MongoClient client;

	private MongoCollection<Document> collection;
	private AlbumMongoRepository repository;

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
		repository = new AlbumMongoRepository(client, DATABASE_NAME, COLLECTION_NAME);
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
	public void testDeleteAlbumsOfMusician() {
		addTestAlbumToDatabase("A", "notDelete", "1");
		addTestAlbumToDatabase("B", "toDelete", "2");
		repository.deleteAlbumsOfMusician("2");
		assertThat(readAllAlbumsFromDatabase()).containsExactly(new Album("A", "notDelete", "1"));
	}

	private void addTestAlbumToDatabase(String id, String title, String musician) {
		collection.insertOne(new Document().append("id", id).append("title", title).append("musician", musician));
	}

	private List<Album> readAllAlbumsFromDatabase() {
		return StreamSupport.stream(collection.find().spliterator(), false)
				.map(d -> new Album(d.getString("id"), d.getString("title"), d.getString("musician")))
				.collect(Collectors.toList());
	}

}
