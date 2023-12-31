package project.discography.controller;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import project.discography.model.Album;
import project.discography.model.Musician;
import project.discography.repository.AlbumRepository;
import project.discography.repository.MusicianRepository;
import project.discography.view.DiscographyView;

public class DiscographyControllerTest {

	@Mock
	private DiscographyView view;

	@Mock
	private MusicianRepository musicianRepository;

	@Mock
	private AlbumRepository albumRepository;

	private MockitoSession mockitoSession;
	private DiscographyController controller;

	@Before
	public void setUp() {
		mockitoSession = Mockito.mockitoSession().initMocks(this).strictness(Strictness.STRICT_STUBS).startMocking();
		controller = new DiscographyController(view, musicianRepository, albumRepository);
	}

	@After
	public void releaseMocks() {
		mockitoSession.finishMocking();
	}

	@Test
	public void testAllMusicians() {
		List<Musician> musicians = Arrays.asList(new Musician("1", "aMusician"));
		when(musicianRepository.findAllMusicians()).thenReturn(musicians);
		controller.allMusicians();
		verify(view).showAllMusicians(musicians);
	}

	@Test
	public void testNewMusicianWhenMusicianDoesNotAlreadyExist() {
		Musician newMusician = new Musician("1", "newMusician");
		when(musicianRepository.findMusicianById("1")).thenReturn(null);
		controller.newMusician(newMusician);
		InOrder order = inOrder(view, musicianRepository);
		order.verify(musicianRepository).saveMusician(newMusician);
		order.verify(view).musicianAdded(newMusician);
	}

	@Test
	public void testNewMusicianWhenMusicianDoesAlreadyExist() {
		Musician newMusician = new Musician("1", "newMusician");
		Musician existingMusician = new Musician("1", "existingMusician");
		when(musicianRepository.findMusicianById("1")).thenReturn(existingMusician);
		controller.newMusician(newMusician);
		verify(view).showErrorDuplicateMusicianId("Already exist a musician with id 1", existingMusician);
		verifyNoMoreInteractions(musicianRepository);
	}

	@Test
	public void testDeleteMusicianWhenMusicianExist() {
		Musician toDelete = new Musician("1", "toDelete");
		when(musicianRepository.findMusicianById("1")).thenReturn(toDelete);
		controller.deleteMusician(toDelete);
		InOrder order = inOrder(view, musicianRepository, albumRepository);
		order.verify(musicianRepository).deleteMusician("1");
		order.verify(albumRepository).deleteAlbumsOfMusician("1");
		order.verify(view).musicianRemoved(toDelete);
	}

	@Test
	public void testDeleteMusicianWhenMusicianDoesNotExist() {
		Musician toDelete = new Musician("1", "toDelete");
		when(musicianRepository.findMusicianById("1")).thenReturn(null);
		controller.deleteMusician(toDelete);
		InOrder order = inOrder(view, albumRepository);
		order.verify(albumRepository).deleteAlbumsOfMusician("1");
		order.verify(view).showErrorMusicianNotFound("Not exist a musician with id 1", toDelete);
		verifyNoMoreInteractions(musicianRepository);
	}

	@Test
	public void testUpdateMusicianWhenMusicianExist() {
		Musician toUpdate = new Musician("1", "toUpdate");
		Musician updated = new Musician("1", "updated");
		when(musicianRepository.findMusicianById("1")).thenReturn(toUpdate);
		controller.updateMusician(toUpdate, updated);
		InOrder order = inOrder(view, musicianRepository);
		order.verify(musicianRepository).updateMusician("1", updated);
		order.verify(view).musicianUpdated(toUpdate, updated);
	}

	@Test
	public void testUpdateMusicianWhenMusicianDoesNotExist() {
		Musician toUpdate = new Musician("1", "toUpdate");
		Musician updated = new Musician("1", "updated");
		when(musicianRepository.findMusicianById("1")).thenReturn(null);
		controller.updateMusician(toUpdate, updated);
		InOrder order = inOrder(view, albumRepository);
		order.verify(albumRepository).deleteAlbumsOfMusician("1");
		order.verify(view).showErrorMusicianNotFound("Not exist a musician with id 1", toUpdate);
		verifyNoMoreInteractions(musicianRepository);
	}

	@Test
	public void testMusicianDiscographyWhenMusicianExist() {
		Musician musician = new Musician("1", "aMusician");
		List<Album> albums = Arrays.asList(new Album("A", "anAlbum", "1"));
		when(musicianRepository.findMusicianById("1")).thenReturn(musician);
		when(albumRepository.findAllAlbumsOfMusician("1")).thenReturn(albums);
		controller.musicianDiscography(musician);
		verify(view).showAllAlbums(albums);
	}

	@Test
	public void testMusicianDiscographyWhenMusicianDoesNotExist() {
		Musician musician = new Musician("1", "aMusician");
		when(musicianRepository.findMusicianById("1")).thenReturn(null);
		controller.musicianDiscography(musician);
		InOrder order = inOrder(view, albumRepository);
		order.verify(albumRepository).deleteAlbumsOfMusician("1");
		order.verify(view).showErrorMusicianNotFound("Not exist a musician with id 1", musician);
		verifyNoMoreInteractions(albumRepository);
	}

	@Test
	public void testNewAlbumWhenMusicianExistAndAlbumDoesNotAlreadyExist() {
		Musician musician = new Musician("1", "aMusician");
		Album newAlbum = new Album("A", "newAlbum", "1");
		when(musicianRepository.findMusicianById("1")).thenReturn(musician);
		when(albumRepository.findAlbumById("A")).thenReturn(null);
		controller.newAlbum(musician, newAlbum);
		InOrder order = inOrder(view, albumRepository);
		order.verify(albumRepository).saveAlbum(newAlbum);
		order.verify(view).albumAdded(newAlbum);
	}

	@Test
	public void testNewAlbumWhenMusicianDoesNotExist() {
		Musician musician = new Musician("1", "aMusician");
		Album newAlbum = new Album("A", "newAlbum", "1");
		when(musicianRepository.findMusicianById("1")).thenReturn(null);
		controller.newAlbum(musician, newAlbum);
		InOrder order = inOrder(view, albumRepository);
		order.verify(albumRepository).deleteAlbumsOfMusician("1");
		order.verify(view).showErrorMusicianNotFound("Not exist a musician with id 1", musician);
		verifyNoMoreInteractions(albumRepository);
	}

	@Test
	public void testNewAlbumWhenMusicianExistAndAlbumDoesAlreadyExist() {
		Musician musician = new Musician("1", "aMusician");
		Album newAlbum = new Album("A", "newAlbum", "1");
		Album existingAlbum = new Album("A", "existingAlbum", "1");
		when(musicianRepository.findMusicianById("1")).thenReturn(musician);
		when(albumRepository.findAlbumById("A")).thenReturn(existingAlbum);
		controller.newAlbum(musician, newAlbum);
		verify(view).showErrorDuplicateAlbumId("Already exist an album with id A", existingAlbum);
		verifyNoMoreInteractions(albumRepository);
	}

	@Test
	public void testDeleteAlbumWhenMusicianExistAndAlbumExist() {
		Musician musician = new Musician("1", "aMusician");
		Album toDelete = new Album("A", "toDelete", "1");
		when(musicianRepository.findMusicianById("1")).thenReturn(musician);
		when(albumRepository.findAlbumById("A")).thenReturn(toDelete);
		controller.deleteAlbum(musician, toDelete);
		InOrder order = inOrder(view, albumRepository);
		order.verify(albumRepository).deleteAlbum("A");
		order.verify(view).albumRemoved(toDelete);
	}

	@Test
	public void testDeleteAlbumWhenMusicianDoesNotExits() {
		Musician musician = new Musician("1", "aMusician");
		Album toDelete = new Album("A", "toDelete", "1");
		when(musicianRepository.findMusicianById("1")).thenReturn(null);
		controller.deleteAlbum(musician, toDelete);
		InOrder order = inOrder(view, albumRepository);
		order.verify(albumRepository).deleteAlbumsOfMusician("1");
		order.verify(view).showErrorMusicianNotFound("Not exist a musician with id 1", musician);
		verifyNoMoreInteractions(albumRepository);
	}

	@Test
	public void testDeleteAlbumWhenMusicianExistAndAlbumDoesNotExist() {
		Musician musician = new Musician("1", "aMusician");
		Album toDelete = new Album("A", "toDelete", "1");
		when(musicianRepository.findMusicianById("1")).thenReturn(musician);
		when(albumRepository.findAlbumById("A")).thenReturn(null);
		controller.deleteAlbum(musician, toDelete);
		verify(view).showErrorAlbumNotFound("Not exist an album with id A", toDelete);
		verifyNoMoreInteractions(albumRepository);
	}

	@Test
	public void testUpdateAlbumWhenMusiciaExistAndAlbumExist() {
		Musician musician = new Musician("1", "aMusician");
		Album toUpdate = new Album("A", "toUpdate", "1");
		Album updated = new Album("A", "updated", "1");
		when(musicianRepository.findMusicianById("1")).thenReturn(musician);
		when(albumRepository.findAlbumById("A")).thenReturn(toUpdate);
		controller.updateAlbum(musician, toUpdate, updated);
		InOrder order = inOrder(view, albumRepository);
		order.verify(albumRepository).updateAlbum("A", updated);
		order.verify(view).albumUpdated(toUpdate, updated);
	}

	@Test
	public void testUpdateAlbumWhenMusicianDoesNotExits() {
		Musician musician = new Musician("1", "aMusician");
		Album toUpdate = new Album("A", "toUpdate", "1");
		Album updated = new Album("A", "updated", "1");
		when(musicianRepository.findMusicianById("1")).thenReturn(null);
		controller.updateAlbum(musician, toUpdate, updated);
		InOrder order = inOrder(view, albumRepository);
		order.verify(albumRepository).deleteAlbumsOfMusician("1");
		order.verify(view).showErrorMusicianNotFound("Not exist a musician with id 1", musician);
		verifyNoMoreInteractions(albumRepository);
	}

	@Test
	public void testUpdateAlbumWhenMusicianExistAndAlbumDoesNotExist() {
		Musician musician = new Musician("1", "aMusician");
		Album toUpdate = new Album("A", "toUpdate", "1");
		Album updated = new Album("A", "updated", "1");
		when(musicianRepository.findMusicianById("1")).thenReturn(musician);
		when(albumRepository.findAlbumById("A")).thenReturn(null);
		controller.updateAlbum(musician, toUpdate, updated);
		verify(view).showErrorAlbumNotFound("Not exist an album with id A", toUpdate);
		verifyNoMoreInteractions(albumRepository);
	}

}
