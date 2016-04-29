#!/usr/bin/env bash
spark-submit \
  --driver-memory 512M \
  --executor-memory 1G \
  --executor-cores 1 \
  --class org.aja.tejah.CollectTweets \
  --master spark://localhost:7077 \
  --deploy-mode cluster \
  --packages "org.apache.spark:spark-streaming-twitter_2.10:1.6.1" \
  --jars /opt/tejah/target/scala-2.10/tejah-assembly-0.0.1.jar \
  /opt/tejah/target/scala-2.10/tejah-assembly-0.0.1.jar \
  --consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw --consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3  --accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE --accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5

