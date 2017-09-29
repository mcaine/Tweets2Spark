name := "Tweets2Spark"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.1" % Provided
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.1.1" % Provided
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.1.1"

//run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run)).evaluated

mainClass in assembly := Some("Tweets2Spark")

assemblyMergeStrategy in assembly := {
  case PathList(xs @ _*) if xs.last == "UnusedStubClass.class" => MergeStrategy.discard
  case x => {
    val old = (assemblyMergeStrategy in assembly).value
    old(x)
   }
}
