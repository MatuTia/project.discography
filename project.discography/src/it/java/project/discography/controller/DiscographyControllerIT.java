package project.discography.controller;

import static org.mockito.Mockito.mockitoSession;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import project.discography.model.Musician;
import project.discography.repository.AlbumRepository;
import project.discography.repository.MusicianRepository;
import project.discography.repository.mongo.AlbumMongoRepository;
import project.discography.repository.mongo.MusicianMongoRepository;
import project.discography.view.DiscographyView;

public class DiscographyControllerIT {

	private static final String DATABASE_NAME = "test-discography";
	private static final String ALBUM_COLLECTION = "test-album-collection";
	private static final String MUSICIAN_COLLECTION = "test-musician-collection";

	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.0");

	@Mock
	private DiscographyView view;
	private MockitoSession session;
	private MongoClient client;

	private AlbumRepository albumRepository;
	private MusicianRepository musicianRepository;

	private DiscographyController controller;

	@BeforeClass
	public static void setUpServer() {
		mongo.start();
	}

	@Before
	public void setUp() {
		session = mockitoSession().initMocks(this).strictness(Strictness.STRICT_STUBS).startMocking();
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getFirstMappedPort()));
		client.getDatabase(DATABASE_NAME).drop();
		musicianRepository = new MusicianMongoRepository(client, DATABASE_NAME, MUSICIAN_COLLECTION);
		albumRepository = new AlbumMongoRepository(client, DATABASE_NAME, ALBUM_COLLECTION);
		controller = new DiscographyController(view, musicianRepository, albumRepository);
	}

	@After
	public void releaseMock() {
		client.close();
		session.finishMocking();
	}

	@BeforeClass
	public static void shutDownServer() {
		mongo.close();
	}

	@Test
	public void testAllMusician() {
		Musician musician = new Musician("1", "aMusician");
		musicianRepository.saveMusician(musician);
		controller.allMusicians();
		verify(view).showAllMusicians(Arrays.asList(musician));
	}

	@Test
	public void testNewMusician() {
		Musician newMusician = new Musician("1", "newMusician");
		controller.newMusician(newMusician);
		verify(view).musicianAdded(newMusician);
	}

	@Test
	public void testDeleteMusician() {
		Musician toDelete = new Musician("1", "toDelete");
		musicianRepository.saveMusician(toDelete);
		controller.deleteMusician(toDelete);
		verify(view).musicianRemoved(toDelete);
	}

}
