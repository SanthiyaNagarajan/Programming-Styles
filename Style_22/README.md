# Style_22 - Passive Aggressive
Description from [Exercises in Programming Style](http://www.amazon.com/Exercises-Programming-Style-Cristina-Videira/dp/1482227371/)
* Every single procedure and function checks the sanity of its arguments and refuses to continue when the arguments are unreasonable, jumping out of the function.
* When calling out other functions, program functions only check for errors if they are in a position to react meaningfully.
* Exception handling occurs at higher levels of function call chains, wherever it is meaningful to do so.
