package project.discography.controller;

import project.discography.model.Musician;
import project.discography.repository.AlbumRepository;
import project.discography.repository.MusicianRepository;
import project.discography.view.DiscographyView;

public class DiscographyController {

	private DiscographyView view;
	private MusicianRepository musicianRepository;
	private AlbumRepository albumRepository;

	public DiscographyController(DiscographyView view, MusicianRepository musicianRepository,
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
		if (musicianRepository.findMusicianById(toDelete.getId()) == null) {
			albumRepository.deleteAlbumsOfMusician(toDelete.getId());
			view.showErrorMusicianNotFound("Not exist a musician with id " + toDelete.getId(), toDelete);
			return;
		}
		musicianRepository.deleteMusician(toDelete.getId());
		albumRepository.deleteAlbumsOfMusician(toDelete.getId());
		view.musicianRemoved(toDelete);
	}

}
