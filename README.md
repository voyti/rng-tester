rng-tester
==========

Simple  RNGs tester made by me for a university project.

It uses IRng interface to determine how a RNG should look like, and a new RNG, as long as it implements it, may be added in the Runner like this:

<code> rngs.add(new myRng()); </code>

The code is not nice sometimes due to running against the clock (the way reports are generated in the TesterFrame), but the application itself is pretty cool and verbose.

I wanted it to describe in detail why given RNG is suspicious/broken/ok, not just print raw statistics. Thanks to this approach, running this app may teach a thing or two about how RNGs work and how to build a good one.
