Usage Instructions for the S4J Benchmark Environment
====================================================

Getting Started
---------------

0. Check out the s4j repository and `cd` into it.  
   `$ git clone s4j@code.hpi.uni-potsdam.de:s4j.git`  
   `$ cd s4j`

1. Make sure to have the [Play Framework][play] installed, so that the `$ play` command is available. This is the basic web framework that is used.
2. Make sure to have [MySQL][mysql] installed.
3. Create a test DB, e.g. named *tuk* and fill it with the provided fixtures. You can use the `fixtures.sql`.
4. Enter the correct database connection details into the `./otp/project/webapp-repo.json` file. There are already some default values.
5. `$ source own_jdk/activate`. This should load an isolated JDK environment containing a `java` and the modified `javac` executable. These executables will in the following be used by the play framework.
6. `$ play run`. Note that this might take some time at the first time.
7. In your browser, open `http://localhost:9000`. This might take a few moments for the first time due to the ad-hoc compilation of the source files.


Switching Versions
------------------
Initially, the head of the repository should point to the JDBC branch.
Use the scripts `checkout_s4j.sh`, `checkout_jdbc.sh`, or `checkout_hibernate.sh` (*tbd*) to switch between the branches.

Note that the project should be compiled with the JDBC code at first (in JDBC state, `$ play compile`). This is necessary due to an issue with the framework's internal compilation process. After a successful compilation, you can checkout the S4J code. This will compile successfully (and with additional status messages on the console), as only the changed controller classes are affected.

[play]: http://www.playframework.com/
[mysql]: http://dev.mysql.com/downloads/mysql/