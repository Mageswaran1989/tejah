import sbt.Keys._
import sbt._
import sbtassembly._
import AssemblyKeys._

object BuildSettings {

  val Name = "tejah"
  val Version = "0.0.1"
  // You can use either version of Scala. We default to 2.11.7:
  val ScalaVersion = "2.10.5"
  val ScalaVersions = Seq("2.11.7", "2.10.5")


  lazy val buildSettings = Defaults.coreDefaultSettings ++ Seq (
    name          := Name,
    version       := Version,
    scalaVersion  := ScalaVersion,
    crossScalaVersions := ScalaVersions,
    organization  := "com.aja",
    description   := "Accomplish Joyfull Adventures",
    scalacOptions := Seq("-deprecation", "-unchecked", "-encoding", "utf8", "-Xlint")
  )
}

object Resolvers {
  val typesafe      = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  val sonatype      = "Sonatype Release" at "https://oss.sonatype.org/content/repositories/releases"
  val mvnrepository = "MVN Repo" at "http://mvnrepository.com/artifact"
  val allResolvers  = Seq(typesafe, sonatype, mvnrepository)
}

object Dependency {
  object Version {
    val Spark        = "1.6.1"
    val Twitter      = "3.0.3"
    val Breeze       = "0.11.2"
    val Akka         = "2.4.0"
  }

  val cli                   = "commons-cli"           % "commons-cli"       % "1.2"  withSources()
  //                                                  %% means use scala version
  val sparkCore             = "org.apache.spark"      %% "spark-core"       % Version.Spark  % "provided" withSources()
  val sparkMLLib            = "org.apache.spark"      %% "spark-mllib"      % Version.Spark   % "provided"withSources()
  val sparkStreaming        = "org.apache.spark"      %% "spark-streaming"  % Version.Spark  withSources()
  val sparkStreamingTwitter = "org.apache.spark"      %% "spark-streaming-twitter" % Version.Spark  withSources()
  val sparkSQL              = "org.apache.spark"      %% "spark-sql"        % Version.Spark % "provided" withSources()
  val sparkGrapx            = "org.apache.spark"      %% "spark-graphx"     % Version.Spark % "provided" withSources()
  val sparkHive             = "org.apache.spark"      %% "spark-hive"       % Version.Spark % "provided" withSources()
  val sparkRepl             = "org.apache.spark"      %% "spark-repl"       % Version.Spark  withSources()

  val twitterCoreAddon      = "org.twitter4j"         % "twitter4j-core"    % Version.Spark  withSources()
  val twitterStreamAddon    = "org.twitter4j"         % "twitter4j-stream"  % Version.Spark  withSources()
  val gsonLib               = "com.google.code.gson"  % "gson"              % "2.3" withSources()
}

object Dependencies {
  import Dependency._

  val tej =
    Seq(cli, sparkCore, sparkMLLib, sparkStreaming, sparkStreamingTwitter, sparkSQL, sparkGrapx, sparkHive,
      twitterCoreAddon, twitterStreamAddon, gsonLib)
}

object TejahSparkBuild extends Build {
  import BuildSettings._

  val excludeSigFilesRE = """META-INF/.*\.(SF|DSA|RSA)""".r

  lazy val activatorspark = Project(
    id = "tejah-workspace",
    base = file("."),
    settings = buildSettings ++ Seq(
      shellPrompt := { state => "(%s)> ".format(Project.extract(state).currentProject.id) },

      unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "examples" / "scala",
      unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "examples" / "java",

      resolvers := Resolvers.allResolvers,

      // For the Hadoop variants to work, we must rebuild the package before
      // running, so we make it a dependency of run.
      (run in Compile) <<= (run in Compile) dependsOn (packageBin in Compile),
      libraryDependencies ++= Dependencies.tej,

      // Must run the examples and tests in separate JVMs to avoid mysterious
      // scala.reflect.internal.MissingRequirementError errors. (TODO)
      // fork := true,
      // Must run Spark tests sequentially because they compete for port 4040!
      parallelExecution in Test := false,

      assemblyMergeStrategy in assembly := {
        case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
        case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
        case PathList("org", "apache", xs @ _*) => MergeStrategy.last
        case PathList("com", "google", xs @ _*) => MergeStrategy.last
        case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
        case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
        case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
        case "about.html" => MergeStrategy.rename
        case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
        case "META-INF/mailcap" => MergeStrategy.last
        case "META-INF/mimetypes.default" => MergeStrategy.last
        case "plugin.properties" => MergeStrategy.last
        case "log4j.properties" => MergeStrategy.last
        case x =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      },
//     mainClass in assembly := Some("org.aja.tejah.PrintTweetsInfo"),
       mainClass in assembly := Some("org.aja.tejah.CollectTweets")

    ))

}

