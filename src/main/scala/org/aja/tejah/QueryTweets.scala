package org.aja.tejah

/**
 * Created by mageswaran on 10/4/16.
 */
import java.sql.Time
import java.text.SimpleDateFormat

import com.google.gson.Gson
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.AnalysisException
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.{AnalysisException, SQLContext, SaveMode}
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.{Success, Try}

/**
 * Created by mageswaran on 8/4/16.
 */

//http://tech.marksblogg.com/billion-nyc-taxi-rides-spark-emr.html

object QueryTweets {

  private var gson = new Gson()

  def main(args: Array[String]) {

    if(args.length < 3) {
      println("Usage : " + this.getClass.getSimpleName + " --consumerKey <key> --consumerSecret <key> " +
        "--accessToken <key> --accessTokenSecret <key>")
      System.exit(1)
    }

    CredentialParser.parseCommandLineWithTwitterCredentials(args)
    val conf = new SparkConf().
      setAppName(this.getClass.getSimpleName).
      setMaster("local[4]")

    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(30))

    sc.setLogLevel("ERROR")


    //     Get the singleton instance of SQLContext
    val sqlContext = SQLContext.getOrCreate(sc)

    val tweetTable = sqlContext.read.option("mergeSchema", "false").parquet("hdfs://localhost:54310/tweets_table/")
    tweetTable.registerTempTable("tweetTable")

    println("------Tweet table Schema---")
    tweetTable.printSchema()

    println("-------------------------> ", tweetTable.count())

    tweetTable.select("id").show(20)
  }
}
