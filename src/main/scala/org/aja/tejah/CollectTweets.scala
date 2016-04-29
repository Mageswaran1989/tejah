package org.aja.tejah

import java.sql.Time
import java.text.SimpleDateFormat

import com.google.gson.Gson
import org.apache.spark.sql.{AnalysisException, SQLContext, SaveMode}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.{Success, Try}

/**
 * Created by mageswaran on 8/4/16.
 */

//--consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw
//--consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3
//--accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE
//--accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5

//http://tech.marksblogg.com/billion-nyc-taxi-rides-spark-emr.html
//http://kaushikbaruah.com/posts/apache-spark-installation-6-steps/

case class Tweet(createdAt: Time, inReplyToScreenName: String, hashtagEntities: Array[String],
                 screenName: String, location: String, friendsCount: Long)

object CollectTweets {

  private var gson = new Gson()
  private val checkpointDir = "hdfs://localhost:54310/checkPoint"

  def main(args: Array[String]) {

    def createStreamingContext(): StreamingContext = {
      val sc = SparkContext.getOrCreate()
      new StreamingContext(sc, Seconds(3))
    }

    if(args.length < 3) {
      println("Usage : " + this.getClass.getSimpleName + " --consumerKey <key> --consumerSecret <key> " +
        "--accessToken <key> --accessTokenSecret <key>")
      System.exit(1)
    }

    CredentialParser.parseCommandLineWithTwitterCredentials(args)
    val conf = new SparkConf().
      setAppName(this.getClass.getSimpleName)

    val sc = new SparkContext(conf)
    val ssc = StreamingContext.getOrCreate("checkpointDir", createStreamingContext)

    sc.setLogLevel("ERROR")

    //16/04/16 20:40:14 ERROR ReceiverTracker: Deregistered receiver for stream 0: Registered unsuccessfully because Driver refused to start receiver 0
    System.setProperty("https.protocols", "TLSv1.1");

    val twitterStream = TwitterUtils.createStream(ssc, CredentialParser.getAuth)
      .map(gson.toJson(_))

    val formatter = new SimpleDateFormat

    twitterStream.foreachRDD(rdd => {
      // Get the singleton instance of SQLContext
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)

      val df = sqlContext.read.json(rdd)
      df.registerTempTable("tweets")

      try {
        println("Total of " + rdd.count + " tweets received in this batch ")

        println(df.select(df("createdAt"), df("inReplyToScreenName"), df("hashtagEntities.text").as("hashtag"),
          df("text"), df("user.screenName"), df("user.location"), df("user.friendsCount")).show())

       // df.write.mode(SaveMode.Append).parquet("hdfs://localhost:54310/parquest/tweets.parquet")
      }
      catch {
        case e: AnalysisException =>
          println(">>>>>>>>>> Something wrong with incoming tweets" + e.toString)
      }
    })

    sys.ShutdownHookThread {
      println("Gracefully stopping Spark Streaming Application")
      ssc.stop(true, true)
      println("Application stopped")
    }
    ssc.start()
    ssc.awaitTermination()
  }
}
