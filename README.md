Webapp Demo System for the SQL4Java Project
====================================================

Introduction
------------
tbd.

Getting Started
---------------

0. Check out this repository and `cd` into it.  
   `$ cd s4j-otp-demo`

1. Make sure to have the [Play Framework][play] installed, so that the `$ play` command is available. This is the basic web framework that is used.
2. Make sure to have [MySQL][mysql] installed.
3. Create a test DB, e.g. named *tuk* and fill it with the provided fixtures. You can use the `fixtures.sql`.
4. Enter the correct database connection details into the `./otp/project/webapp-repo.json` file. There are already some default values.
5. `$ source own_jdk/activate`. This should load an isolated JDK environment containing a `java` and the modified `javac` executable. These executables will in the following be used by the play framework.
6. `$ play run`. Note that this might take some time at the first time.
7. In your browser, open `http://localhost:9000`. This might take a few moments for the first time due to the ad-hoc compilation of the source files.


License
-------
The MIT License (MIT)

Copyright (c) 2014 Sebastian Oergel

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

3rd Party Components
---–----------------
This demo webapp contains at least parts of the following frameworks and tools:

* Twitter [Bootstrap][bootstrap]
* Twitter [hogan.js][hogan]
* Twitter [typeahead.js][typeahead]
* [jQuery][jquery]

[play]: http://www.playframework.com/
[mysql]: http://dev.mysql.com/downloads/mysql/
[bootstrap]: http://getbootstrap.com/
[hogan]: http://twitter.github.io/hogan.js/
[typeahead]: http://twitter.github.io/typeahead.js/
[jquery]: http://jquery.com/
