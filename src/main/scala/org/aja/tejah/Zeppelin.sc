import org.apache.spark.sql.SQLContext

val sqlContext = SQLContext.getOrCreate(sc)

val tweetTable = sqlContext.read.option("mergeSchema", "false").parquet("hdfs://localhost:54310/tweets_table/")
tweetTable.registerTempTable("tweetTable")

sqlContext.sql("select id from tweetTable limit 20").show()