package project.discography.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import project.discography.model.Album;
import project.discography.repository.AlbumRepository;

public class AlbumMongoRepository implements AlbumRepository {

	private static final String ID = "id";
	private static final String TITLE = "title";
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
		return StreamSupport.stream(collection.find(Filters.eq(MUSICIAN, musician)).spliterator(), false)
				.map(this::fromDocumentToAlbum).collect(Collectors.toList());
	}

	@Override
	public Album findAlbumById(String id) {
		Document document = collection.find(Filters.eq(ID, id)).first();
		if (document == null)
			return null;
		return fromDocumentToAlbum(document);
	}

	@Override
	public void saveAlbum(Album toSave) {
		collection.insertOne(new Document().append(ID, toSave.getId()).append(TITLE, toSave.getTitle())
				.append(MUSICIAN, toSave.getMusician()));
	}

	@Override
	public void deleteAlbum(String id) {
		 collection.deleteOne(Filters.eq(ID, id));
	}

	@Override
	public void updateAlbum(String id, Album updated) {
		// TODO Auto-generated method stub

	}

	private Album fromDocumentToAlbum(Document document) {
		return new Album(document.getString(ID), document.getString(TITLE), document.getString(MUSICIAN));
	}

}
