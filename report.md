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

(copy your changes or the add git command to show them)

git diff ...

Optional (point 4): the patch is clean.

Optional (point 5): considered for acceptance (passes all automated checks).

## Test results

Overall results with link to a copy or excerpt of the logs (before/after
refactoring).

## UML class diagram and its description

### Key changes/classes affected

Optional (point 1): Architectural overview.

Optional (point 2): relation to design pattern(s).

## Overall experience

What are your main take-aways from this project? What did you learn?

How did you grow as a team, using the Essence standard to evaluate yourself?

Optional (point 6): How would you put your work in context with best software engineering practice?

Optional (point 7): Is there something special you want to mention here?
