package project.discography;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/bdd/resources", plugin = "pretty", dryRun = true)
public class DiscographyAppBDD {

}
