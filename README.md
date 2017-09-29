### Compile
`sbt clean assembly`

### Start Spark
`cd ~/spark-2.1.1-bin-hadoop2.7`

`sbin/start-master.sh`

`sbin/start-slave.sh spark://localhost:7077`

### Submit job
`~/spark-2.1.1-bin-hadoop2.7/bin/spark-submit --class Tweets2Spark --master local[*] "target/scala-2.11/Tweets2Spark-assembly-1.0.jar"`
