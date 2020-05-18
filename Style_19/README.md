# Style_19 - Plugins
Description from [Exercises in Programming Style](http://www.amazon.com/Exercises-Programming-Style-Cristina-Videira/dp/1482227371/)
* The problem is decomposed using some form of abstraction (procedures, functions, objects, etc.).
* All or some of those abstractions are physically encapsulated into their own, usually pre-compiled, packages. Main program and each of the packages are compiled independently. These packages are loaded dynamically by the main program, usually in the beginning (but not necessarily)
* Main program uses functions/objects from the dynamically-loaded packages, without knowing which exact implementations will be used. New implementations can be used without having to adapt or recompile the main program.
* Existence of an external specification of which packages to load. This can be done by a configuration file, path conventions, user input or other mechanisms for external specification of code to be loaded at runtime.
