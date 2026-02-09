package com.brice.http

import com.brice.services.{EmailService, EmailConfig, ProjectService, CourseService}
import com.brice.clients.BlockchainApiClient
import com.brice.services.{PriceService, TransactionIngestionService, CsvExportService}
import com.brice.repositories.TransactionRepository
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.*
import zio.http.*
import scala.io.Source
import scala.util.Try
import java.io.File
import java.nio.file.{Files, Paths}
import zio.Chunk
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import javax.sql.DataSource
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

/** HTTP Server configuration and setup using ZIO best practices */
object HttpServer:

  // Determine if running in production (static files in ./static/) or development (in modules/frontend/)
  private val isProduction: Boolean = new File("static/index.html").exists()
  private val staticDir: String = if isProduction then "static" else "modules/frontend"

  /** Load environment variables from .env file */
  private def loadEnvFile(): Map[String, String] =
    Try {
      val source = Source.fromFile(".env")
      try
        source.getLines()
          .filter(_.contains("="))
          .filterNot(_.trim.startsWith("#"))
          .map { line =>
            val parts = line.split("=", 2)
            parts(0).trim -> parts(1).trim
          }
          .toMap
      finally source.close()
    }.getOrElse(Map.empty)

  /** Get environment variable from .env file or system environment */
  private def getEnv(key: String, default: String, envFile: Map[String, String]): String =
    envFile.getOrElse(key, sys.env.getOrElse(key, default))

  /** Read file content */
  private def readFile(path: String): ZIO[Any, Throwable, String] =
    ZIO.attemptBlocking {
      val source = Source.fromFile(path)
      try source.mkString finally source.close()
    }

  /** Read binary file (for images) */
  private def readBinaryFile(path: String): ZIO[Any, Throwable, Array[Byte]] =
    ZIO.attemptBlocking(Files.readAllBytes(Paths.get(path)))

  /** Get content type from file extension */
  private def getContentType(path: String): MediaType =
    val ext = path.split("\\.").lastOption.getOrElse("").toLowerCase
    ext match
      case "html"          => MediaType.text.html
      case "css"           => MediaType.text.css
      case "js"            => MediaType.application.javascript
      case "json"          => MediaType.application.json
      case "png"           => MediaType.image.png
      case "jpg" | "jpeg"  => MediaType.image.jpeg
      case "gif"           => MediaType.image.gif
      case "svg"           => MediaType.image.`svg+xml`
      case "ico"           => MediaType.image.`x-icon`
      case "woff"          => MediaType.font.woff
      case "woff2"         => MediaType.font.woff2
      case _               => MediaType.application.`octet-stream`

  /** Create HikariCP DataSource layer */
  private def createDataSourceLayer(
    jdbcUrl: String,
    username: String,
    password: String
  ): ZLayer[Any, Throwable, DataSource] =
    ZLayer.scoped {
      ZIO.acquireRelease(
        ZIO.attempt {
          val config = new HikariConfig()
          config.setJdbcUrl(jdbcUrl)
          config.setUsername(username)
          config.setPassword(password)
          config.setMaximumPoolSize(10)
          config.setMinimumIdle(2)
          config.setConnectionTimeout(30000)
          new HikariDataSource(config)
        }
      )(ds => ZIO.succeed(ds.close()))
    }

  /** Static file handler */
  private def serveStaticFile(req: Request): ZIO[Any, Nothing, Response] =
    val path = req.path.toString.stripPrefix("/")

    val effect: ZIO[Any, Throwable, Response] =
      if path.startsWith("dist/") then
        val filePath = path.stripPrefix("dist/")
        readFile(s"$staticDir/dist/$filePath")
          .map(content => Response.text(content).addHeader(Header.ContentType(MediaType.application.javascript)))
      else if path.endsWith(".css") then
        readFile(s"$staticDir/$path")
          .map(content => Response.text(content).addHeader(Header.ContentType(MediaType.text.css)))
      else if path.matches(".*\\.(png|jpg|jpeg|gif|svg|ico|woff|woff2)$") then
        val mediaType = getContentType(path)
        readBinaryFile(s"$staticDir/public/$path")
          .orElse(readBinaryFile(s"$staticDir/$path"))
          .map(bytes => Response(body = Body.fromChunk(Chunk.fromArray(bytes))).addHeader(Header.ContentType(mediaType)))
      else if !path.startsWith("api") then
        // SPA fallback - serve index.html for client-side routing
        readFile(s"$staticDir/index.html")
          .map(content => Response.text(content).addHeader(Header.ContentType(MediaType.text.html)))
      else
        ZIO.fail(new Exception("Not found"))

    effect.orElse(ZIO.succeed(Response.notFound))

  /** Create the HTTP application */
  private def createApp(
    emailService: EmailService,
    projectService: ProjectService,
    courseService: CourseService
  ): HttpApp[AnalyticsRoutes.AnalyticsEnv] =
    // Combine all Tapir endpoints
    val allEndpoints =
      HealthRoutes.routes ++
      ContactRoutes.routes(emailService) ++
      ProjectRoutes.routes(projectService) ++
      CourseRoutes.routes(courseService)

    // Convert Tapir endpoints to HttpApp
    val apiApp = ZioHttpInterpreter().toHttp(allEndpoints)

    // Add analytics routes
    val analyticsApp = AnalyticsRoutes().toHttpApp

    // Combine API apps
    val combinedApiApp = apiApp ++ analyticsApp

    // In production, add static file serving; in development, Vite handles it
    if isProduction then
      val staticApp = Routes(
        Method.GET / trailing -> Handler.fromFunctionZIO[Request](serveStaticFile)
      ).toHttpApp
      combinedApiApp ++ staticApp
    else
      combinedApiApp

  /** Start the HTTP server - main entry point */
  val start: ZIO[Any, Throwable, Unit] =
    for
      // Load configuration
      envFile <- ZIO.succeed(loadEnvFile())
      _ <- ZIO.logInfo(s"Loaded ${envFile.size} environment variables from .env file")
      _ <- ZIO.logInfo(s"Running in ${if isProduction then "PRODUCTION" else "DEVELOPMENT"} mode, static dir: $staticDir")

      // Get port from environment
      port <- ZIO.succeed(getEnv("PORT", "8080", envFile).toInt)

      // Get database configuration
      dbUrl = getEnv("DATABASE_URL", "jdbc:postgresql://localhost:5432/modernscale", envFile)
      dbUser = getEnv("DATABASE_USER", "postgres", envFile)
      dbPassword = getEnv("DATABASE_PASSWORD", "postgres", envFile)
      _ <- ZIO.logInfo(s"Database URL: $dbUrl")

      // Create email configuration
      emailConfig = EmailConfig(
        apiKey = getEnv("RESEND_API_KEY", "", envFile),
        fromEmail = getEnv("EMAIL_FROM", "onboarding@resend.dev", envFile),
        toEmail = getEnv("EMAIL_TO", "build@modernscale.dev", envFile)
      )
      _ <- ZIO.logInfo(s"Email config: from=${emailConfig.fromEmail}, to=${emailConfig.toEmail}, apiKey=${if emailConfig.apiKey.nonEmpty then "[set]" else "[not set]"}")

      // Create services
      emailService = new EmailService.Live(emailConfig)
      projectService = new ProjectService.InMemory
      courseService = new CourseService.InMemory

      // Create application
      app = createApp(emailService, projectService, courseService)

      // Create analytics service layers
      dataSourceLayer = createDataSourceLayer(dbUrl, dbUser, dbPassword)
      quillLayer = Quill.Postgres.fromNamingStrategy(SnakeCase)
      analyticsLayers =
        dataSourceLayer >+>
        quillLayer >+>
        TransactionRepository.live >+>
        Client.default >+>
        BlockchainApiClient.live >+>
        PriceService.inMemory >+>
        TransactionIngestionService.live >+>
        CsvExportService.live

      // Start server with all layers
      _ <- ZIO.logInfo(s"Starting HTTP server on http://localhost:$port")
      _ <- ZIO.logInfo(s"API available at http://localhost:$port/api")
      _ <- ZIO.logInfo(s"Analytics available at http://localhost:$port/analytics")
      _ <- Server.serve(app).provide(
        Server.defaultWithPort(port),
        analyticsLayers
      )
    yield ()
