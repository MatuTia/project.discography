package project.discography.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import project.discography.controller.DiscographyController;
import project.discography.repository.AlbumRepository;
import project.discography.repository.MusicianRepository;
import project.discography.repository.mongo.AlbumMongoRepository;
import project.discography.repository.mongo.MusicianMongoRepository;
import project.discography.view.swing.DiscographySwingView;

public class DiscographyAppDefaultModule extends AbstractModule {

	String databaseName = "discography";
	String albumCollectionName = "album";
	String musicianCollectionName = "musician";

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(DatabaseName.class).toInstance(databaseName);
		bind(String.class).annotatedWith(MusicianCollectionName.class).toInstance(musicianCollectionName);
		bind(String.class).annotatedWith(AlbumCollectionName.class).toInstance(albumCollectionName);

		bind(MusicianRepository.class).to(MusicianMongoRepository.class);
		bind(AlbumRepository.class).to(AlbumMongoRepository.class);

		install(new FactoryModuleBuilder().implement(DiscographyController.class, DiscographyController.class)
				.build(DiscographyControllerFactory.class));
	}

	@Provides
	DiscographySwingView view(DiscographyControllerFactory factory) {
		DiscographySwingView view = new DiscographySwingView();
		view.setController(factory.create(view));
		return view;
	}

}
