package com.intuit.karate.core.retry;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.core.Scenario;
import com.intuit.karate.core.ScenarioResult;
import com.intuit.karate.core.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pthomas3
 */
class RetryTest {
    
    static final Logger logger = LoggerFactory.getLogger(RetryTest.class);   

    @Test
    void testParallel() {
        Results results = Runner.path("classpath:com/intuit/karate/core/retry/test.feature")
                .reportDir("target/retry-test")
                .parallel(1);
        assertEquals(1, results.getFailCount());
        List<ScenarioResult> failed = results.getScenarioResults().filter(sr -> sr.isFailed()).collect(Collectors.toList());
        assertEquals(1, failed.size());
        Scenario scenario = failed.get(0).getScenario();
        Step step = scenario.getSteps().get(0);
        assertEquals("assert value != 1", step.getText());
        step.setText("assert value == 1");
        ScenarioResult sr = results.getSuite().retryScenario(scenario);
        assertFalse(sr.isFailed());
        results = results.getSuite().updateResults(sr);
        assertEquals(0, results.getFailCount());
    }

    @Test
    void testSetup() {
        System.setProperty("CURRENT_VALUE", "a");
        Results results = Runner.path("classpath:com/intuit/karate/core/retry/retry-with-setup.feature").parallel(1);

        System.setProperty("CURRENT_VALUE", "b");
        for (ScenarioResult scenarioResult : results.getScenarioResults().collect(Collectors.toList())) {
            if (scenarioResult.isFailed()) {
                ScenarioResult retryScenarioResult = results.getSuite().retryScenario(scenarioResult.getScenario());
                results = results.getSuite().updateResults(retryScenarioResult);
            }
        }

        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

	@Test
	void testMultipleExamplesTables() {
		Results results = Runner.path("classpath:com/intuit/karate/core/retry/test-multiple-examples-tables.feature")
                .parallel(1);
		List<String> stepText = new ArrayList<String>();

		for (ScenarioResult scenarioResult : results.getScenarioResults().collect(Collectors.toList())) {
			ScenarioResult retryScenarioResult = results.getSuite().retryScenario(scenarioResult.getScenario());
			results.getSuite().updateResults(retryScenarioResult);
		}
		for (ScenarioResult scenarioResult : results.getScenarioResults().collect(Collectors.toList())) {
			stepText.add(scenarioResult.getScenario().getSteps().get(0).getText());
		}
		assertEquals("print \"example 1\"", stepText.get(0));
		assertEquals("print \"example 2\"", stepText.get(1));
	}

	@Test
	void testTags() {
        Results results = Runner.path("classpath:com/intuit/karate/core/retry/test-multiple-examples-tables.feature")
		.parallel(1);
		List<String> tagTexts = new ArrayList<String>();

		for (ScenarioResult scenarioResult : results.getScenarioResults().collect(Collectors.toList())) {
            ScenarioResult retryScenarioResult = results.getSuite().retryScenario(scenarioResult.getScenario());
            results = results.getSuite().updateResults(retryScenarioResult);
        }
		for (ScenarioResult scenarioResult : results.getScenarioResults().collect(Collectors.toList())) {
			assertNotNull(scenarioResult.getScenario().getTags());
			tagTexts.add(scenarioResult.getScenario().getTags().get(0).getText());
		}
		assertEquals("tag1", tagTexts.get(0));
		assertEquals("tag2", tagTexts.get(1));
	}
}
