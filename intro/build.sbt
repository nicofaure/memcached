name := "intro"

version := "1.0-SNAPSHOT"

resolvers += "Nexus Staging Rep" at "http://git.ml.com:8081/nexus/content/repositories/PlayPlugins/"

libraryDependencies ++= Seq(
    javaJdbc,
    javaEbean,
    cache,
    "net.spy" % "spymemcached" % "2.12.1"
)



play.Project.playJavaSettings
