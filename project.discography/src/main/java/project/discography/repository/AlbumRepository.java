package project.discography.repository;

import java.util.List;

import project.discography.model.Album;

public interface AlbumRepository {

	void deleteAlbumsOfMusician(String musician);

	List<Album> findAllAlbumsOfMusician(String musician);

}
