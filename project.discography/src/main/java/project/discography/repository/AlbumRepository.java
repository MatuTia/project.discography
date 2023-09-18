package project.discography.repository;

import java.util.List;

import project.discography.model.Album;

public interface AlbumRepository {

	void deleteAlbumsOfMusician(String musician);

	List<Album> findAllAlbumsOfMusician(String musician);

	Album findAlbumById(String id);

	void saveAlbum(Album toSave);

	void deleteAlbum(String id);

}
