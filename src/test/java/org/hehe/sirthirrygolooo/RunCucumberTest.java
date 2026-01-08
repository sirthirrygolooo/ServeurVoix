package org.hehe.sirthirrygolooo;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "org.hehe.sirthirrygolooo.steps"
)
public class RunCucumberTest {
}