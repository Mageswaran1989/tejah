#Standalone Clusters
##Reference Url
- http://spark.apache.org/docs/latest/spark-standalone.html

##Ubuntu Environment
###JDK
- Run scripts/openjdk.sh (or)
- Run scripts/sun_jav_install.sh
###Scala
- Run scripts/scala_install.sh
- Run scripts/sbt_install.sh
###Maven
- Run scripts/maven_install.sh
###Building the Spark
Assuming the spark is download at /opt/spark
First, to avoid the notorious 'permgen' error increase the amount memory available to the JVM:
$ export MAVEN_OPTS="-Xmx1300M -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"
$ build/mvn -Phive -Phive-thriftserver -Pyarn -Dhadoop.version=2.7.0 -Dyarn.version=2.7.0 -DskipTests clean package

###Configuring the local cluster:
vim  /opt/spark/conf/spark-env.sh
export SPARK_LOCAL_IP="localhost" #, to set the IP address Spark binds to on this node
export SPARK_EXECUTOR_INSTANCES=2 #, Number of executors to start (Default: 2)
export SPARK_EXECUTOR_CORES=1 #, Number of cores for the executors (Default: 1).
export SPARK_EXECUTOR_MEMORY=1G #, Memory per Executor (e.g. 1000M, 2G) (Default: 1G)
export SPARK_DRIVER_MEMORY=1G #, Memory for Driver (e.g. 1000M, 2G) (Default: 1G)
export SPARK_MASTER_IP="localhost" #, to bind the master to a different IP address or hostname
export SPARK_WORKER_CORES=2 #, to set the number of cores to use on this machine
export SPARK_WORKER_MEMORY=2g #, to set how much total memory workers have to give executors (e.g. 1000m, 2g)
export SPARK_WORKER_INSTANCES=2 #, to set the number of worker processes per node

#Notes
Avoid using localhost, instead use full IP address
$ cd /opt/spark/
$ ./sbin/start-master.sh
$  check Spark Master UI:  http://localhost:8080/
$ ./sbin/start-slave.sh spark://localhost:7077
$ MASTER=spark://localhost:7077 ./bin/spark-shell

/* throwing darts and examining coordinates */
val NUM_SAMPLES = 100000
val count = sc.parallelize(1 to NUM_SAMPLES).map{i =>
  val x = Math.random * 2 - 1
  val y = Math.random * 2 - 1
  if (x * x + y * y < 1) 1.0 else 0.0
}.reduce(_ + _)

println("Pi is roughly " + 4 * count / NUM_SAMPLES) 

#Debug Notes:
$bin/spark-submit --version
Check all dependencies are met
