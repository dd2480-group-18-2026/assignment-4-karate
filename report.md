# Report for assignment 4

## Project

Name: karate

URL: https://github.com/karatelabs/karate

Karate is a framework used for test automation. It combines API testing, mocking, performance testing and UI testing in one tool.

## Onboarding experience

We used the same project as for assignment 3. Below is the onboarding experience from the assignment 3 report:

   a. Did you have to install a lot of additional tools to build the software?
      
      The only additional tools I needed to build the software was an IDE, JDK, Maven and Git. 

   b. Were those tools well documented?
      
      Yes, The documentation clearly stated how the additional tools were to be installed, these are well documented tools and have clear and thorough documentation. Though they did not give a link to the official documentation for the tools.

   c. Were other components installed automatically by the build script?

      Yes, the components were installed automatically by Maven during the build process

   d. Did the build conclude automatically without errors?

      Building was successful

   e. How well do examples and tests run on your system(s)?

      Core tests all pass 610/610

Worth noting is that the issue also came with a tiny project used for reproducing the bug. This was very easy to set up and built successfully more or less directly.

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

Title: Mismatched Step Titles and Missing Tags in Reports After Retrying Scenarios

URL: https://github.com/karatelabs/karate/issues/2489

#### Summary

A user observed that when retrying scenarios (karate tests) created from a scenario outline containing multiple examples tables, some of the updated tests were duplicated and some disappeared in the report. Additionally, the tags on examples tables disappeared after retrying. 

#### Scope

In order to fix the duplication and disappearing tests, the `Scenario` class needed to be amended with a new field `exampleTableIndex`, which gives information about which of several examples table a Scenario originates from. Previously only a field called `exampleIndex` existed, which only indicated which row within a specific `ExamplesTable` a `Scenario` was created from. This variable was also renamed to `exampleRowIndex`. 

In order to resolve the issue, the new variable had to be set to a correct value when creating `Scenario` instances in the `ScenarioOutline` class. Furthermore, the Scenario method `isEqualTo` had to be updated to use the new variable, as otherwise different scenarios could erroneously be considered equal. The `compareTo` method in `ScenarioResult` also had to be updated, as well as methods to parse scenario results to and from JSON.  

To resolve the tags part of the issue, the first part of the issue first had to be resolved. With this fix in place, what remained was modifying `fromKarateJson` in `ScenarioResult` to set tags from both ScenarioOutline and the examplesTable corresponding to the `Scenario`. Previously this method ignored `ExamplesTable` tags, so parsing a ScenarioResult from JSON always removed them, causing the issue. This required usage of `exampleTableIndex` to index the correct `ExamplesTable`, which is why the first part had to be done before fixing this. 

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

![UML class diagram for affected classes and some additional ones for context](assignment/uml/diagram_before.png)

The UML diagram shows the structure of the classes that were involved in fixing the issue. For context, some classes
not affected or affected very little are also shown, to illustrate how the core classes involved are created and interact. 

The most important part is that ScenarioOutline contains one or more `ExamplesTable`, which it uses to generate Scenarios. `Scenario` was the class most affected by the changes, as we needed to add a new field, `exampleTableIndex`, renaming the original `exampleIndex` to `exampleRowIndex`. This change required adjustments to many other methods, such as initially setting it in `ScenarioOutline`, or adjusting `ScenarioResult` methods for comparing and creating from JSON. The Suite class is included as its `retryScenario` and `updateResults` methods were the cause of the issue, but it did not have to be changed, as we fixed the methods it relied on. After we fixed the first requirement, the tags issue only required changes in `ScenarioResult:fromKarateJson`. 

The diagram shows the structure before the changes, but the only differences that would be noticeable afterwards is that
`getExampleTableIndex` and `setExampleTableIndex` would be present in the `Scenario` class. Most changes are internally in the methods and would not affect the structure of the diagram, but some methods like `toScenario` would have `exampleTableIndex` as a new parameter. 

## Overall experience

### Lessons learned

The realization that definitely was cemented was how difficult it can be understand the underlying problem causing an issue. The issue we chose actually came with quite a thorough description and an initial discussion between the reporter and the core maintainer. Despite this, we still had to spend many hours investigating the code to feel sure we actually understood what was wrong. Another lesson we take from this assignment is how much work is done besides coding when resolving an issue, even for those that are quite small.

### Essence

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