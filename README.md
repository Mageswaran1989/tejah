# tejah
Twitter data analysis with Spark Streaming, Hive and GraphX in Ubuntu

#Setup:
1. Use the script/hadoop_setup.md  to setup HDFS filesystem in your Ubuntu
2. 

#How to run:
1. Start Hadoop
- start-dfs.sh
2. Start Spark
3. Start the application
4. Start Zeppelin
   ./bin/zeppelin-daemon.sh start
   localhost:8080



#Important Links:
- http://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/FileSystemShell.html

#Story:
1. Let one spark application continuously download the tweets and store as per below requirements:
- A table with features:
- Use Neo4j to create graph database with features:
2. Run same or a new spark application to analysis the received data and create a dynamic model
- Sentiment analysis
Cheerfulness, Negative, Anger, Analytical, Confident, Tentative, Openness, Agreeableness, Conscientiousness 
3. Find an optimal way to use Hive to analysis the collected data
4. 