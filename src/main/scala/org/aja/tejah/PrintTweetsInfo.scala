package org.aja.tejah

import java.text.SimpleDateFormat

import com.google.gson.Gson
import org.apache.spark.sql.{AnalysisException, SaveMode, SQLContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by mageswaran on 15/4/16.
 */

object PrintTweetsInfo {

  private var gson = new Gson()


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
      setAppName(this.getClass.getSimpleName).
    set("spark.eventLog.enabled","true")

    //16/04/16 20:40:14 ERROR ReceiverTracker: Deregistered receiver for stream 0: Registered unsuccessfully because Driver refused to start receiver 0
    System.setProperty("https.protocols", "TLSv1.1");



    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(10)) //StreamingContext.getOrCreate("checkpointDir", createStreamingContext)

    sc.setLogLevel("ERROR")

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

      }
      catch {
        case e: AnalysisException =>
          println("!!!!!!!!!!!!!!!!!!!!!!!!!!!! Something wrong with incoming tweet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + e.toString)
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
