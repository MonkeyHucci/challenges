# challenges
https://github.com/QuodAI/challenges/issues/3

# how to run
java -cp lib/gson-2.8.6.jar;lib/joda-time-2.10.5.jar;lib/json-20190722.jar;bin ai.quod.challenge.HealthScoreCalculator [datetime_start] [datetime_end]

# Technical decisions
What frameworks/libraries did you use? What are the benefits of those libraries?
->
gson: serialize and deserialize Java objects to (and from) JSON
joda-time: the de facto standard date and time library for Java prior to Java SE 8
json: JSON encoders/decoders in Java

How would you improve your code for performance?
-> Use mutilthread

What code would you refactor for readability and maintainability?
-> All. (I haven't had much time to do this).
