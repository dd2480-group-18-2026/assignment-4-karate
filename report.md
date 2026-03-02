# Report for assignment 4

## Project

Name: karate

URL: https://github.com/karatelabs/karate

Karate is a framework used for test automation. It combines API testing, mocking, performance testing and UI testing in one tool.

## Onboarding experience

Did you choose a new project or continue on the previous one?

If you changed the project, how did your experience differ from before?

## Effort spent

For each team member, how much time was spent in

1. plenary discussions/meetings;

2. discussions within parts of the group;

3. reading documentation;

4. configuration and setup;

5. analyzing code/output;

6. writing documentation;

7. writing code;

8. running code?

For setting up tools and libraries (step 4), enumerate all dependencies
you took care of and where you spent your time, if that time exceeds
30 minutes.

## Overview of issue(s) and work done.

Title:

URL:

Summary in one or two sentences

Scope (functionality and code affected).

## Requirements for the new feature

### 1. Report scenario outline example results once

When executing a feature containing a scenario outline with multiple example tables, each example is reported as a scenario in the report. When retrying a subset of these scenarios (the reason for doing this might be that they have failed), the successful scenarios should remain in the report and the failed ones should be updated with the new results. In other words, each example in the scenario outline should be shown exactly once in the report, with the latest result.

#### Test

To test this requirement [testMultipleExampleTablesTitles()](https://github.com/dd2480-group-18-2026/assignment-4-karate/blob/59f9e0cb6be7721c2ac0d00b53c6448511fa75b2/karate-core/src/test/java/com/intuit/karate/core/retry/RetryTest.java#L61) was implemented in the RetryTest class. It checks that retrying scenarios does not affect the order or content of scenarios generated from tests when using multiple examples tables. The test fails before our patch, and passes after including it, as expected.

### 2. Persist example tags

When executing a feature containing a scenario outline with at least one example, any potential tags on the example table are shown in the report. When rerunning any example within that feature, the example table tags should persist in the report.

#### Test

To test this requirement [testMultipleExampleTablesTags()](https://github.com/dd2480-group-18-2026/assignment-4-karate/blob/59f9e0cb6be7721c2ac0d00b53c6448511fa75b2/karate-core/src/test/java/com/intuit/karate/core/retry/RetryTest.java#L78) was implemented in the RetryTest class. It checks that retrying scenarios when using multiple examples tables does not remove or alter tags. This test fails before includng our patch, and passes afterwards, as expected.

## Code changes

### Patch

The changes can be seen by using the git diff command. The command below automatically excludes every file regarding the report:
```bash
git diff master issue-15-code-changes -- . ':(exclude)report.md' ':(exclude)assignment/'
```

Optional (point 4): the patch is clean. Indeed, one can check that the patch is clean by adding the `--check` option to the command above (before the `--`) to have git compute the empty lines of the patch.

## Test results

The current version of Karate fails both of our new tests, resulting in two failures out of 612 test ([logs](assignment/logs/before.txt)).

Our new patch addresses these issues, and the code now passes all the tests in the test suite ([logs](assignment/logs/after.txt)).

## UML class diagram and its description

### Key changes/classes affected

Optional (point 1): Architectural overview.

Optional (point 2): relation to design pattern(s).

## Overall experience

### Lessons learned

The realization that definitely was cemented was how difficult it can be understand the underlying problem causing an issue. The issue we chose actually came with quite a thorough description and an initial discussion between the reporter and the core maintainer. Despite this, we still had to spend many hours investigating the code to feel sure we actually understood what was wrong. Another lesson we take from this assignment is how much work is done besides coding when resolving an issue, even for those that are quite small.

### SEMAT

For this lab we consider ourselves to be still be at the **In Place** stage in terms of way-of-working. This is because there are still quite a few deviations from agreed upon standards. However, in terms of the team alpha we are at the **Performing** stage as we are continuously finish the work on time and rarely have to backtrack. As this is the last assignment, our team will reach the **Adjourned** stage after it is handed in. To reach the next stage of way-of-working, we would have needed to get more practice with the established methods to make them come more naturally.

Another alpha of importance for this assignment is *Stakeholders*. For this assignment we have the person who created the issue, the core maintainer and the TA/examiner. Although their demands are mostly aligned, one could argue that the need for some of the documentation required by the TA/examiner is in conflict with the rest of the task which the two people involved in the issue wish to get done. Additionally, we cannot really evaluate the stakeholders beyond the **Represented** stage as the TA/examiner has not been in contact with the other stakeholders and neither have we. Therefore, we cannot fully check off points such as "The collaboration approach among the stakeholder representatives has been agreed" or "The stakeholder representatives provide feedback and take part in decision making in a timely manner". In hindsight, it might have been a good idea to actually reach out to the core maintainer as he generally does seems quite involved and responsive. This could have given us more guidance in understanding the requirements of the issue.

Some of the additional tasks that are not directly connected to the issue do, however, actually guide us in identifying the requirements and make it easier to show what has been done. An example of this demanding the changes to be put into context. In that way, they contribute to both the *Work* and *Requirements* alphas.

## Context

As mentioned above, the issue consists of two subissues: 
1. the result of one example being overwritten by the result of another
2. the examples table tags disappearing

### Part 1

The first one is spawned from the fact that the existing design does not really support rerunning examples from a scenario outline where the examples come from different examples tables. This is not supported as there is no way of identifying from which table the example comes from and the examples themselves are indexed relative to their position in the table, making the current reference to an example ambiguous when there are multiple tables. To resolve this issue, we added an index for the examples table. When the scenario outline with its examples is then expanded to scenarios for each example, each created scenario will contain an `exampleRowIndex` as well as a `examplesTableIndex`. In doing so, the `isEqualTo` method of the `Scenario` class can then be updated to also consider from which examples table the scenario is created. This is the method that is used when deciding which result to update after an example scenario has been rerun.


### Part 2

For the second part of the issue, the problem is quite subtle. Whenever the a feature is run, the results are stored in a report in JSON format. In this report the results are stored as scenario result no matter if the feature contained pure scenarios or a scenario outline with multiple examples. When for example calling `getScenarioResults` this JSON report is read into memory and parsed into a `FeatureResult` object containing a list of `ScenarioResult` objects which are the objects that are returned by the method. The method creating the `FeatureResult` and `ScenarioResult` objects is `fromKarateJson`. However, this method does not just parse the JSON report, but also the feature file.

Currently, if the section of the feature being parsed is a scenario outline, the tags of the examples tables are excluded. When running the feature initially the feature file is parsed differently. This is why the example tags show up in the report (both JSON and HTML) when the feature examples are only run once. So, by simply amending the case, when the feature section is a scenario outline, to include the tags that are for the correct examples table (filtering on examplesTableIndex from the JSON report), the tags persist even after rerunning.