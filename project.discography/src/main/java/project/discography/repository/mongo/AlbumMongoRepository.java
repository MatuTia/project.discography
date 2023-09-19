package project.discography.repository.mongo;

import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import project.discography.model.Album;
import project.discography.repository.AlbumRepository;

public class AlbumMongoRepository implements AlbumRepository {

	private static final String MUSICIAN = "musician";

	private MongoCollection<Document> collection;

	public AlbumMongoRepository(MongoClient client, String databaseName, String collectionName) {
		collection = client.getDatabase(databaseName).getCollection(collectionName);
	}

	@Override
	public void deleteAlbumsOfMusician(String musician) {
		collection.deleteMany(Filters.eq(MUSICIAN, musician));
	}

	@Override
	public List<Album> findAllAlbumsOfMusician(String musician) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Album findAlbumById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveAlbum(Album toSave) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAlbum(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAlbum(String id, Album updated) {
		// TODO Auto-generated method stub

	}

}
