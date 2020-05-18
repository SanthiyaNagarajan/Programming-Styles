# Style_28 - Actors
Description from [Exercises in Programming Style](http://www.amazon.com/Exercises-Programming-Style-Cristina-Videira/dp/1482227371/)
* The larger problem is decomposed into things that make sense for the problem domain.
* Each thing has a queue meant for other things to place messages in it.
* Each thing is a capsule of data that exposes only its ability to receive
messages via the queue.
* Each thing has its own thread of execution independent of the others.
