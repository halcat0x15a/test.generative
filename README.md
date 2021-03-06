clojure.test.generative
========================================

Test data generation and execution harness. Very early days.
This API will change. You have been warned.


Releases and Dependency Information
========================================

Latest stable release: 0.1.4

* [All Released Versions](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.clojure%22%20AND%20a%3A%22test.generative%22)

* [Development Snapshot Versions](https://oss.sonatype.org/index.html#nexus-search;gav~org.clojure~test.generative~~~)

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

    [org.clojure/test.generative "0.1.4"]

[Maven](http://maven.apache.org/) dependency information:

    <dependency>
      <groupId>org.clojure</groupId>
      <artifactId>test.generative</artifactId>
      <version>0.1.4</version>
    </dependency>


Example Usages
========================================

A defspec consists of a name, a function to be tested, an input spec,
and a validator:

    (defspec integers-closed-over-addition
      (fn [a b] (+' a b))                    ;; input fn
      [^long a ^long b]                     ;; input spec
      (assert (integer? %)))                ;; 0 or more validator forms

To generate test data, see the fns in the generators namespace. Note
that these functions shadow a bunch of clojure.core names.

You can run clojure.test and clojure.test.generative tests together
from the c.t.g. runner:

    (require '[clojure.test.generative.runner :as runner])
    (runner/-main "src/test/clojure" "src/examples/clojure")

Assertion support is currently minimal. There is an `is` macro,
similar to clojure.test's, that provides rudimentaty contextual
reporting.  You can also use plain assertions, or clojure.test
validation forms such as `is` and `are`, or any other forms that throw
exception on failure. No contextual reporting for these yet.

You can configure the runner with Java system properties:

<table>
  <tr>
    <th>Java Property</th><th>Interpretation</th>
  </tr>
  <tr>
    <td>clojure.test.generative.threads</td><td>Number of concurrent threads</td>
  </tr>
  <tr>
    <td>clojure.test.generative.msec</td><td>Desired test run duration</td>
  </tr>
  <tr>
    <td>clojure.test.generative.handlers</td><td>Comma-delimited list of handlers</td>
  </tr>
</table>

The default handler prints test run to stdout, but others could
e.g. put test events in a database.

Developer Information
========================================

* [GitHub project](https://github.com/clojure/test.generative)

* [Bug Tracker](http://dev.clojure.org/jira/browse/TGEN)

* [Continuous Integration](http://build.clojure.org/job/test.generative/)

* [Compatibility Test Matrix](http://build.clojure.org/job/test.generative-test-matrix/)

Change Log
====================

* Release 0.1.5 (in development)
  * Can now run tests under clojure.test
  * Tests produce data events that can be consumed by arbitrary reporting tools
  * Example reporting integration with logback. 
  * Added `is` macro with more detailed reporting than `clojure.test/is`
  * Removed collection based input generators. Input generators must be fns.
  * Removed duplicate input check. Tests can be called multiple times with same input. 
* Release 0.1.4 on 2012.01.03
  * Initial version


Copyright and License
========================================

Copyright (c) 2012 Rich Hickey. All rights reserved.  The use and distribution terms for this software are covered by the Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can be found in the file epl-v10.html at the root of this distribution. By using this software in any fashion, you are agreeing to be bound bythe terms of this license.  You must not remove this notice, or any other, from this software.
