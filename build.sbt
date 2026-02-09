import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.7.4"
ThisBuild / scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature"
)

ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

// Dependency versions
val zioVersion        = "2.0.19"
val tapirVersion      = "1.9.11"
val zioLoggingVersion = "2.1.8"
val zioConfigVersion  = "3.0.7"
val sttpVersion       = "3.8.8"
val javaMailVersion   = "1.6.2"
val stripeVersion     = "24.3.0"
val laminarVersion    = "17.0.0"
val waypointVersion   = "8.0.0"

// Backend-only dependencies
val backendDependencies = Seq(
  "com.softwaremill.sttp.tapir"   %% "tapir-sttp-client"                 % tapirVersion,
  "com.softwaremill.sttp.client3" %% "zio"                               % sttpVersion,
  "com.softwaremill.sttp.tapir"   %% "tapir-zio"                         % tapirVersion,
  "com.softwaremill.sttp.tapir"   %% "tapir-zio-http-server"             % tapirVersion,
  "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-bundle"           % tapirVersion,
  "com.softwaremill.sttp.tapir"   %% "tapir-sttp-stub-server"            % tapirVersion % "test",
  "dev.zio"                       %% "zio"                               % zioVersion,
  "dev.zio"                       %% "zio-json"                          % "0.4.2",
  "dev.zio"                       %% "zio-logging"                       % zioLoggingVersion,
  "dev.zio"                       %% "zio-logging-slf4j"                 % zioLoggingVersion,
  "ch.qos.logback"                 % "logback-classic"                   % "1.4.4",
  "dev.zio"                       %% "zio-test"                          % zioVersion,
  "dev.zio"                       %% "zio-test-junit"                    % zioVersion   % "test",
  "dev.zio"                       %% "zio-test-sbt"                      % zioVersion   % "test",
  "dev.zio"                       %% "zio-test-magnolia"                 % zioVersion   % "test",
  "dev.zio"                       %% "zio-mock"                          % "1.0.0-RC9"  % "test",
  "dev.zio"                       %% "zio-config"                        % zioConfigVersion,
  "dev.zio"                       %% "zio-config-magnolia"               % zioConfigVersion,
  "dev.zio"                       %% "zio-config-typesafe"               % zioConfigVersion,
  "io.getquill"                   %% "quill-jdbc-zio"                    % "4.7.3",
  "org.postgresql"                 % "postgresql"                        % "42.5.0",
  "org.flywaydb"                   % "flyway-core"                       % "9.7.0",
  "io.github.scottweaver"         %% "zio-2-0-testcontainers-postgresql" % "0.9.0",
  "dev.zio"                       %% "zio-prelude"                       % "1.0.0-RC16",
  "com.auth0"                      % "java-jwt"                          % "4.2.1",
  "com.sun.mail"                   % "javax.mail"                        % javaMailVersion,
  "com.stripe"                     % "stripe-java"                       % stripeVersion
)

// Shared module - cross-compiled for JVM and JS
lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/shared"))
  .settings(
    name := "brice-shared",
    libraryDependencies ++= Seq(
      "dev.zio"                       %%% "zio"          % zioVersion,
      "dev.zio"                       %%% "zio-json"     % "0.4.2",
      "com.softwaremill.sttp.tapir"   %%% "tapir-core"   % tapirVersion,
      "com.softwaremill.sttp.tapir"   %%% "tapir-json-zio" % tapirVersion
    )
  )

lazy val sharedJVM = shared.jvm
lazy val sharedJS  = shared.js

// Backend module - JVM only
lazy val backend = (project in file("modules/backend"))
  .enablePlugins(JavaAppPackaging, FlywayPlugin)
  .settings(
    name := "brice-backend",
    libraryDependencies ++= backendDependencies,
    Compile / mainClass := Some("com.brice.Application"),
    // Assembly settings for fat JAR
    assembly / assemblyJarName := "brice-backend.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case "reference.conf" => MergeStrategy.concat
      case x => MergeStrategy.first
    },
    // Flyway settings
    flywayUrl := sys.env.getOrElse("DATABASE_URL", "jdbc:postgresql://localhost:5432/modernscale"),
    flywayUser := sys.env.getOrElse("DATABASE_USER", "postgres"),
    flywayPassword := sys.env.getOrElse("DATABASE_PASSWORD", "postgres"),
    flywayLocations := Seq("filesystem:modules/backend/src/main/resources/db/migration")
  )
  .dependsOn(sharedJVM)

// Frontend module - ScalaJS only
lazy val frontend = (project in file("modules/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "brice-frontend",
    libraryDependencies ++= Seq(
      "com.raquo"                     %%% "laminar"      % laminarVersion,
      "com.raquo"                     %%% "waypoint"     % waypointVersion,
      "com.softwaremill.sttp.tapir"   %%% "tapir-sttp-client" % tapirVersion,
      "com.softwaremill.sttp.client3" %%% "core"         % sttpVersion
    ),
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("com.brice")))
    },
    Compile / fastLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "dist",
    Compile / fullLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "dist"
  )
  .dependsOn(sharedJS)

// Root project
lazy val root = (project in file("."))
  .settings(
    name := "brice-solutions"
  )
  .aggregate(backend, frontend, sharedJVM, sharedJS)
