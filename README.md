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


[play]: http://www.playframework.com/
[mysql]: http://dev.mysql.com/downloads/mysql/