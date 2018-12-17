name := "CirceTrial"

version := "1.0"

scalaVersion := "2.12.1"

// This is for using annotation
resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

libraryDependencies += "io.circe" %% "circe-parser" % "0.10.0"
libraryDependencies += "io.circe" %% "circe-generic" % "0.10.0"