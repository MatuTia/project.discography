package project.discography.repository;

import java.util.List;

import project.discography.model.Musician;

public interface MusicianRepository {

	List<Musician> findAllMusicians();

	Musician findMusicianById(String id);

	void saveMusician(Musician toSave);

	void deleteMusician(String id);

	void updateMusician(String id, Musician updated);

}
