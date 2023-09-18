package project.discography.view;

import java.util.List;

import project.discography.model.Musician;

public interface DiscographyView {

	void showAllMusicians(List<Musician> musicians);

	void musicianAdded(Musician added);

	void showErrorDuplicateMusicianId(String message, Musician existingMusician);

}
