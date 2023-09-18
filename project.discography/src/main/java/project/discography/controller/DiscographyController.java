package project.discography.controller;

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

}
