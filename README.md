# JLOX

All credits goes to  [Robert (? Bob ?) Nystrom](https://twitter.com/munificentbob).

He made a wonderful tutorial  to crate your own language here: http://craftinginterpreters.com/
It is gold content, super clearly explained.


## Specialties


I wrote it in kotlin instead of java to learn a bit of kotlin.

Kotlin prove to be super efficient since `match` was perfect to spare some char in endless if else statements.
Non nullability by default seems great but I guess I didn't know enough to use it during the parser: I had to 
force it to be non null because nothing indicate kotlin that all remaining parsee option can't be null.

My next Language is going to be written in rust.