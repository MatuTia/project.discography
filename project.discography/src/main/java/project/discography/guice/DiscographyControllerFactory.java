package project.discography.guice;

import project.discography.controller.DiscographyController;
import project.discography.view.DiscographyView;

public interface DiscographyControllerFactory {

	DiscographyController create(DiscographyView view);

}
