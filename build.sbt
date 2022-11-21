import Dependencies._

ThisBuild / organization := "com.nyx"
ThisBuild / scalaVersion := "3.2.0"

ThisBuild / scalacOptions ++=
  Seq(
    "-deprecation",
    "-explain",
    "-feature",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Yexplicit-nulls", // experimental (I've seen it cause issues with circe)
    "-Ykind-projector",
    "-Ysafe-init", // experimental (I've seen it cause issues with circe)
  ) ++ Seq("-rewrite", "-indent") ++ Seq("-source", "future-migration")

lazy val `zio-todo` =
  project
    .in(file("."))
    .settings(name := "zio-todo")
    .settings(commonSettings)
    .settings(dependencies)

enablePlugins(FlywayPlugin)

flywayUrl := sys.env.get("DB_URL").getOrElse("")
flywayUser := sys.env.get("DB_USERNAME").getOrElse("")
flywayPassword :=sys.env.get("DB_PASSWORD").getOrElse("")

lazy val commonSettings = {
  lazy val commonScalacOptions = Seq(
    Compile / console / scalacOptions --= Seq(
      "-Wunused:_",
      "-Xfatal-warnings",
    ),
    Test / console / scalacOptions :=
      (Compile / console / scalacOptions).value,
  )

  lazy val otherCommonSettings = Seq(
    update / evictionWarningOptions := EvictionWarningOptions.empty
  )

  Seq(
    commonScalacOptions,
    otherCommonSettings,
  ).reduceLeft(_ ++ _)
}

lazy val dependencies = Seq(
  libraryDependencies ++= Seq(
    // https://github.com/zio/zio-quill
    "mysql" % "mysql-connector-java" % "8.0.31",
    // https://github.com/getquill/quill
     "io.getquill" %% "quill-zio" % "4.6.0",
     "io.getquill" %% "quill-jdbc-zio" % "4.6.0",
  ),
  libraryDependencies ++= Seq(
    org.scalatest.scalatest,
    org.scalatestplus.`scalacheck-1-16`,
    "io.d11" %% "zhttp-test" % "2.0.0-RC7",
  ).map(_ % Test),
)
