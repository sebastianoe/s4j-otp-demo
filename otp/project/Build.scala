import sbt.{ Project => SbtProject, Settings => SbtSettings, _ }
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "otp"
  val appVersion      = "1.0-SNAPSHOT"
  val repoPath        = (new java.io.File(".").getCanonicalPath) + "/webapp-repo.json"
  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    cache,
    "mysql" % "mysql-connector-java" % "5.1.18",
    "joda-time" % "joda-time" % "2.3"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here    
    javacOptions in Compile ++= Seq("-sqlrepo", repoPath),
    commands ++= Seq(timeCommand)
  )

  val timeCommand: Command = Command.single("timeit") { (state, arg) => 
    val toDelete = List("CustomerController.class", "InvoiceController.class", "ProductController.class", "SalesOrderController.class")
    val times = scala.collection.mutable.ListBuffer[Long]()
    val totalCount = arg.toInt
    val warmupRounds = 5

    for (i <- 0 until totalCount + warmupRounds) {
      // delete files
      for {
        files <- Option(new File("target/scala-2.10/classes/controllers/").listFiles)
        file <- files if toDelete.contains(file.getName)
      } file.delete()

      // measure compilation time
      val startTime: Long = System.nanoTime()
      SbtProject.runTask(compile in Compile, state)
      val endTime: Long = System.nanoTime()
      
      if (i >= warmupRounds) {
        println((i - warmupRounds) + "/" + totalCount)
        times += ((endTime - startTime) / 1000000)
      }
    }

    println("Times in ms")
    for (time <- times) {
      println(time)
    }
    
    state
  }
  
}
