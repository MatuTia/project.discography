package project.discography.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import project.discography.model.Musician;
import project.discography.repository.MusicianRepository;

public class MusicianMongoRepository implements MusicianRepository {

	private static final String ID = "id";
	private static final String NAME = "name";

	private MongoCollection<Document> collection;

	public MusicianMongoRepository(MongoClient client, String databaseName, String collectionName) {
		collection = client.getDatabase(databaseName).getCollection(collectionName);
	}

	@Override
	public List<Musician> findAllMusicians() {
		return StreamSupport.stream(collection.find().spliterator(), false).map(this::fromDocumentToMusician)
				.collect(Collectors.toList());
	}

	@Override
	public Musician findMusicianById(String id) {
		Document document = collection.find(Filters.eq(ID, id)).first();
		if (document == null)
			return null;
		return fromDocumentToMusician(document);
	}

	@Override
	public void saveMusician(Musician toSave) {
		 collection.insertOne(new Document().append(ID, toSave.getId()).append(NAME, toSave.getName()));
	}

	@Override
	public void deleteMusician(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMusician(String id, Musician updated) {
		// TODO Auto-generated method stub

	}

	private Musician fromDocumentToMusician(Document document) {
		return new Musician(document.getString(ID), document.getString(NAME));
	}

}
