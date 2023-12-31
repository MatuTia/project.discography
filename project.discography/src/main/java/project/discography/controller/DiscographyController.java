package project.discography.controller;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import project.discography.model.Album;
import project.discography.model.Musician;
import project.discography.repository.AlbumRepository;
import project.discography.repository.MusicianRepository;
import project.discography.view.DiscographyView;

public class DiscographyController {

	private DiscographyView view;
	private MusicianRepository musicianRepository;
	private AlbumRepository albumRepository;

	@Inject
	public DiscographyController(@Assisted DiscographyView view, MusicianRepository musicianRepository,
			AlbumRepository albumRepository) {
		this.view = view;
		this.musicianRepository = musicianRepository;
		this.albumRepository = albumRepository;
	}

	public void allMusicians() {
		view.showAllMusicians(musicianRepository.findAllMusicians());
	}

	public void newMusician(Musician newMusician) {
		Musician existingMusician = musicianRepository.findMusicianById(newMusician.getId());
		if (existingMusician != null) {
			view.showErrorDuplicateMusicianId("Already exist a musician with id " + newMusician.getId(),
					existingMusician);
			return;
		}
		musicianRepository.saveMusician(newMusician);
		view.musicianAdded(newMusician);
	}

	public void deleteMusician(Musician toDelete) {
		if (existMusician(toDelete)) {
			musicianRepository.deleteMusician(toDelete.getId());
			albumRepository.deleteAlbumsOfMusician(toDelete.getId());
			view.musicianRemoved(toDelete);
		}
	}

	public void updateMusician(Musician toUpdate, Musician updated) {
		if (existMusician(toUpdate)) {
			musicianRepository.updateMusician(toUpdate.getId(), updated);
			view.musicianUpdated(toUpdate, updated);
		}
	}

	public void musicianDiscography(Musician musician) {
		if (existMusician(musician)) {
			view.showAllAlbums(albumRepository.findAllAlbumsOfMusician(musician.getId()));
		}
	}

	public void newAlbum(Musician musician, Album newAlbum) {
		if (existMusician(musician)) {
			Album existingAlbum = albumRepository.findAlbumById(newAlbum.getId());
			if (existingAlbum != null) {
				view.showErrorDuplicateAlbumId("Already exist an album with id " + newAlbum.getId(), existingAlbum);
				return;
			}
			albumRepository.saveAlbum(newAlbum);
			view.albumAdded(newAlbum);
		}
	}

	public void deleteAlbum(Musician musician, Album toDelete) {
		if (existMusician(musician) && existAlbum(toDelete)) {
			albumRepository.deleteAlbum(toDelete.getId());
			view.albumRemoved(toDelete);
		}
	}

	public void updateAlbum(Musician musician, Album toUpdate, Album updated) {
		if (existMusician(musician) && existAlbum(toUpdate)) {
			albumRepository.updateAlbum(toUpdate.getId(), updated);
			view.albumUpdated(toUpdate, updated);
		}
	}

	private boolean existMusician(Musician musician) {
		if (musicianRepository.findMusicianById(musician.getId()) == null) {
			albumRepository.deleteAlbumsOfMusician(musician.getId());
			view.showErrorMusicianNotFound("Not exist a musician with id " + musician.getId(), musician);
			return false;
		}
		return true;
	}

	private boolean existAlbum(Album album) {
		if (albumRepository.findAlbumById(album.getId()) == null) {
			view.showErrorAlbumNotFound("Not exist an album with id " + album.getId(), album);
			return false;
		}
		return true;
	}

}
