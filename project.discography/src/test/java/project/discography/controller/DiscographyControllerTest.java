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

}
