package project.discography.view;

import java.util.List;

import project.discography.model.Album;
import project.discography.model.Musician;

public interface DiscographyView {

	void showAllMusicians(List<Musician> musicians);

	void musicianAdded(Musician added);

	void showErrorDuplicateMusicianId(String message, Musician existingMusician);

	void musicianRemoved(Musician removed);

	void showErrorMusicianNotFound(String message, Musician selectedMusician);

	void musicianUpdated(Musician toUpdate, Musician updated);

	void showAllAlbums(List<Album> albums);

	void albumAdded(Album added);

	void showErrorDuplicateAlbumId(String message, Album existingAlbum);

	void albumRemoved(Album removed);

	void showErrorAlbumNotFound(String message, Album selectedAlbum);

	void albumUpdated(Album toUpdate, Album updated);

}
