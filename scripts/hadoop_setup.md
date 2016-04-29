1.   Install latest java version
2.   Adding dedicated hadoop user
       $ sudo addgroup hadoop
       $ sudo adduser --ingroup hadoop tejah
       $ sudo adduser tejah sudo
3.   $ sudo apt-get install ssh
4.   $ su tejah
5.   $ ssh-keygen -t rsa -P ""
6.   $ cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys
7.   $ ssh localhost
8.   Download and unzip latest hadoop in folder
9.   $ cd /path/to/hadoop-x.y.z/
10.  $ sudo mv * /usr/local/hadoop 
11.  $ sudo chown -R tejah:hadoop /usr/local/hadoop
12.  Hadoop configuration
       $ vim ~/.bashrc
       Copy below to end of the bashrc file:
#HADOOP VARIABLES START
export JAVA_HOME=/usr/lib/jvm/java-8-oracle/jre/
export HADOOP_INSTALL=/usr/local/hadoop
export PATH=$PATH:$HADOOP_INSTALL/bin
export PATH=$PATH:$HADOOP_INSTALL/sbin
export HADOOP_MAPRED_HOME=$HADOOP_INSTALL
export HADOOP_COMMON_HOME=$HADOOP_INSTALL
export HADOOP_HDFS_HOME=$HADOOP_INSTALL
export YARN_HOME=$HADOOP_INSTALL
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_INSTALL/lib/native
export HADOOP_OPTS="-Djava.library.path=$HADOOP_INSTALL/lib"
#HADOOP VARIABLES END

13.  $ source ~/.bashrc
14.  $ vim  /usr/local/hadoop/etc/hadoop/hadoop-env.sh
       export JAVA_HOME=/usr/lib/jvm/java-8-oracle/jre/
15.  $ sudo mkdir -p /app/hadoop/tmp
16.  $ sudo chown hduser:hadoop /app/hadoop/tmp
17.  hdfs-site.xml, provides default behaviors for the HDFS client.
     $ vi /usr/local/hadoop/etc/hadoop/core-site.xml
<configuration>
  <property>
   <name>hadoop.tmp.dir</name>
   <value>/app/hadoop/tmp</value>
   <description>A base for other temporary directories.</description>
 </property>
 <property>
  <name>fs.defaultFS</name>
  <value>hdfs://localhost:9000</value>
 </property>
 <property>
  <name>fs.default.name</name>
  <value>hdfs://localhost:54310</value>
  <description>The name of the default file system.  A URI whose
  scheme and authority determine the FileSystem implementation.  The
  uri's scheme determines the config property (fs.SCHEME.impl) naming
  the FileSystem implementation class.  The uri's authority is used to
  determine the host, port, etc. for a filesystem.</description>
 </property>
</configuration>

18.  $ cp /usr/local/hadoop/etc/hadoop/mapred-site.xml.template /usr/local/hadoop/etc/hadoop/mapred-site.xml 
     Add followinf line to the above file:
<configuration>
 <property>
  <name>mapred.job.tracker</name>
  <value>localhost:54311</value>
  <description>The host and port that the MapReduce job tracker runs
  at.  If "local", then jobs are run in-process as a single map
  and reduce task.
  </description>
 </property>
</configuration>

19.  Namenode and DataNode directories:
     $ sudo mkdir -p /opt/tejah/data/hdfs/namenode
     $ sudo mkdir -p /opt/tejah/data/hdfs/datanode
     $ sudo chown -R tejah:hadoop /opt/tejah/data/hdfs

20. core-site.xml, sets the default filesystem name.
    vim /usr/local/hadoop/etc/hadoop/hdfs-site.xml
    And copy below lines into it: 
<configuration>
 <property>
  <name>dfs.replication</name>
  <value>1</value>
  <description>Default block replication.
  The actual number of replications can be specified when the file is created.
  The default is used if replication is not specified in create time.
  </description>
 </property>
 <property>
   <name>dfs.namenode.name.dir</name>
   <value>file:/opt/tejah/data/hdfs/namenode</value>
 </property>
 <property>
   <name>dfs.datanode.data.dir</name>
   <value>file:/opt/tejah/data/hdfs/datanode</value>
 </property>
</configuration>

21.  Starting the Hadoop
     $ cd /usr/local/hadoop/sbin
     $ su tejah
     $ start-all.sh 
     $ jps  #To check hadoop is up and running
22.  http://localhost:50070/
23.  $ vim /etc/ssh/sshd_config and add "Port 9000"
     $ sudo service ssh restart
24.  $ groupadd supergroup
     $ usermod -a -G supergroup your_user_name 

