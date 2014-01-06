# factorialfun

Demos of stack-consuming recursion and the 'recur special form to calculate factorials.

## Usage Examples

```bash
$ lein run f0 4
$ lein run f1 4
$ lein run f2 4
$ lein run f0 21 # BOOM! Integer overflow.
$ lein run f1 10000 # BOOM! Stack [probably] blows here. If it doesn't, try a bigger number.
$ lein run f2 10000 # Try the same big number that eventually blew your stack when using f1.
$ lein run f3-seq-n 4
$ lein run f3-seq-take 20
```

## License

Copyright © 2014 Matt Oquist <moquist@majen.net>

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
