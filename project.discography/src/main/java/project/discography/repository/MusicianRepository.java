package project.discography.repository;

import java.util.List;

import project.discography.model.Musician;

public interface MusicianRepository {

	List<Musician> findAllMusicians();

}
