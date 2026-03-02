Feature:

Scenario Outline: Test behaviour with multiple examples tables
* print "example <Example>"
* match true == true

@tag1
Examples:
| Example |
|  1      |

@tag2
Examples:
| Example |
|  2      |
