#!/usr/bin/env bash
spark-submit \
  --class org.aja.tejah.PrintTweetsInfo \
  --master spark://localhost:6066 \
  --deploy-mode cluster \
  --num-executors 2 \
  --executor-memory 1G \
  --total-executor-cores 3 \
  --packages "org.apache.spark:spark-streaming-twitter_2.10:1.6.1" \
  --jars /opt/tejah/target/scala-2.10/tejah_2.10-0.0.1.jar \
  /opt/tejah/target/scala-2.10/tejah_2.10-0.0.1.jar \
  --consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw --consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3  --accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE --accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5
