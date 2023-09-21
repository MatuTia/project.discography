package project.discography.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mongodb.MongoClient;

import project.discography.controller.DiscographyController;
import project.discography.repository.AlbumRepository;
import project.discography.repository.MusicianRepository;
import project.discography.repository.mongo.AlbumMongoRepository;
import project.discography.repository.mongo.MusicianMongoRepository;
import project.discography.view.swing.DiscographySwingView;

public class DiscographyAppDefaultModule extends AbstractModule {

	private String mongoHost = "localhost";
	private int mongoPort = 27017;
	private String databaseName = "discography";
	private String albumCollectionName = "album";
	private String musicianCollectionName = "musician";
	
	public DiscographyAppDefaultModule mongoHost(String mongoHost) {
		this.mongoHost = mongoHost;
		return this;
	}

	public DiscographyAppDefaultModule mongoPort(int mongoPort) {
		this.mongoPort = mongoPort;
		return this;
	}

	public DiscographyAppDefaultModule databaseName(String databaseName) {
		this.databaseName = databaseName;
		return this;

	}

	public DiscographyAppDefaultModule musicianCollectionName(String musicianCollectionName) {
		this.musicianCollectionName = musicianCollectionName;
		return this;

	}

	public DiscographyAppDefaultModule albumCollectionName(String albumCollectionName) {
		this.albumCollectionName = albumCollectionName;
		return this;
	}

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(MongoHost.class).toInstance(mongoHost);
		bind(Integer.class).annotatedWith(MongoPort.class).toInstance(mongoPort);
		bind(String.class).annotatedWith(DatabaseName.class).toInstance(databaseName);
		bind(String.class).annotatedWith(MusicianCollectionName.class).toInstance(musicianCollectionName);
		bind(String.class).annotatedWith(AlbumCollectionName.class).toInstance(albumCollectionName);

		bind(MusicianRepository.class).to(MusicianMongoRepository.class);
		bind(AlbumRepository.class).to(AlbumMongoRepository.class);

		install(new FactoryModuleBuilder().implement(DiscographyController.class, DiscographyController.class)
				.build(DiscographyControllerFactory.class));
	}

	@Provides
	MongoClient mongoClient(@MongoHost String host, @MongoPort int port) {
		return new MongoClient(host, port);
	}

	@Provides
	DiscographySwingView view(DiscographyControllerFactory factory) {
		DiscographySwingView view = new DiscographySwingView();
		view.setController(factory.create(view));
		return view;
	}

}
