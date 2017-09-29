import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.util.Try

object Tweets2Spark {
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Tweets2Spark")
    val streamingContext = new StreamingContext(conf, Seconds(20))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers"    -> "192.168.0.105:9092",
      "key.deserializer"     -> classOf[StringDeserializer],
      "value.deserializer"   -> classOf[StringDeserializer],
      "group.id"             -> "simpleGroup",
      "auto.offset.reset"    -> "latest",
      "enable.auto.commit"   -> (false: java.lang.Boolean)
    )

    val topics = Array("MikesTweets")
    
    val stream = KafkaUtils.createDirectStream[String, String] (
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.foreachRDD { rdd => 
      
      val words = rdd.flatMap { record =>
          implicit val formats = DefaultFormats
          try {
            val tweet = parse(record.value().toString).extract[Tweet]
            tweet.text.split(" ")
          } catch {
            case _ : Throwable => Array[String]()
          }
      }
      
      val counts = words.map(word => (word, 1)).reduceByKey { _ + _ }
      
      counts.foreach { 
        case (word, count) if count > 10 => println(s"${word} : ${count}")
        case _ =>
      }
    }

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}
