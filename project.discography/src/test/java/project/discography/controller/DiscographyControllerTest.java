package project.discography.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

}
