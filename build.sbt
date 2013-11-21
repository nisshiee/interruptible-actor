net.virtualvoid.sbt.graph.Plugin.graphSettings

name := "interruptible-actor"

organization := "org.nisshiee"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  // ---------- basic ----------
   "com.typesafe.akka" %% "akka-actor" % "[2.2.0,2.2.3]"
  // ---------- test scope ----------
  ,"org.specs2" %% "specs2" % "2.3.4" % "test"
  ,"com.typesafe.akka" %% "akka-testkit" % "[2.2.0,2.2.3]" % "test"
  ,"org.typelevel" %% "scalaz-specs2" % "0.1.5" % "test"
  ,"junit" % "junit" % "4.11" % "test"
  ,"org.pegdown" % "pegdown" % "1.4.1" % "test"
)

scalacOptions <++= scalaVersion.map { sv =>
  if (sv.startsWith("2.10")) {
    Seq(
      "-deprecation",
      "-language:dynamics",
      "-language:postfixOps",
      "-language:reflectiveCalls",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:reflectiveCalls",
      "-language:experimental.macros",
      "-Xfatal-warnings"
    )
  } else {
    Seq("-deprecation")
  }
}

testOptions in (Test, test) += Tests.Argument("console", "html", "junitxml")

initialCommands in Test := """
import akka.actor._
import akka.testkit._
import org.nisshiee.interruptibleactor._
class Observer extends Actor {
  var log: List[Any] = Nil
  override def receive = { case m => log = m :: log }
}
implicit val system = ActorSystem("test")
val observer = TestActorRef[Observer]
"""

cleanupCommands in Test := """
system.shutdown
"""


// ========== for sonatype oss publish ==========

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/nisshiee/interruptible-actor</url>
  <licenses>
    <license>
      <name>The MIT License (MIT)</name>
      <url>http://opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:nisshiee/interruptible-actor.git</url>
    <connection>scm:git:git@github.com:nisshiee/interruptible-actor.git</connection>
  </scm>
  <developers>
    <developer>
      <id>nisshiee</id>
      <name>Hirokazu Nishioka</name>
      <url>http://nisshiee.github.com/</url>
    </developer>
  </developers>)


// ========== for scaladoc ==========

scalacOptions in (Compile, doc) <++= baseDirectory.map {
  bd => Seq("-sourcepath", bd.getAbsolutePath,
            "-doc-source-url", "https://github.com/nisshiee/interruptible-actor/blob/master€{FILE_PATH}.scala",
            "-implicits", "-diagrams")
}

