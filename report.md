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

## Requirements for the new feature or requirements affected by functionality being refactored

Optional (point 3): trace tests to requirements.

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

## Context

As mentioned above, the issue consists of two subissues: 
1. the result of one example being overwritten by the result of another
2. the examples table tags disappearing

### Part 1

The first one is spawned from the fact that the existing design does not really support rerunning examples from a scenario outline where the examples come from different examples tables. This is not supported as there is no way of identifying from which table the example comes from and the examples themselves are indexed relative to their position in the table, making the current reference to an example ambiguous when there are multiple tables. To resolve this issue, we added an index for the examples table. When the scenario outline with its examples is then expanded to scenarios for each example, each created scenario will contain an `exampleRowIndex` as well as a `examplesTableIndex`. In doing so, the `isEqualTo` method of the `Scenario` class can then be updated to also consider from which examples table the scenario is created. This is the method that is used when deciding which result to update after an example scenario has been rerun.


### Part 2

For the second part of the issue, the problem is quite subtle. Whenever the a feature is run, the results are stored in a report in JSON format. In this report the results are stored as scenario result no matter if the feature contained pure scenarios or a scenario outline with multiple examples. When for example calling `getScenarioResults` this JSON report is read into memory and parsed into a `FeatureResult` object containing a list of `ScenarioResult` objects which are the objects that are returned by the method. The method creating the `FeatureResult` and `ScenarioResult` objects is `fromKarateJson`. However, this method does not just parse the JSON report, but also the feature file.

Currently, if the section of the feature being parsed is a scenario outline, the tags of the examples tables are excluded. When running the feature initially the feature file is parsed differently. This is why the example tags show up in the report (both JSON and HTML) when the feature examples are only run once. So, by simply amending the case, when the feature section is a scenario outline, to include the tags that are for the correct examples table (filtering on examplesTableIndex from the JSON report), the tags persist even after rerunning.