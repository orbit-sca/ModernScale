package com.brice.services

import zio.*
import com.brice.domain.*

/** Service for managing portfolio projects */
trait ProjectService:
  def listProjects(filter: ProjectFilter): Task[List[Project]]
  def getProject(id: String): Task[Option[Project]]
  def listByCategory(category: ProjectCategory): Task[List[Project]]

object ProjectService:

  /** In-memory implementation with sample portfolio data */
  final class InMemory extends ProjectService:

    // Real projects from GitHub - verified and demonstrable
    private val projects: List[Project] = List(
      // Development Projects
      Project(
        id = "kemberco",
        title = "Kember.co Full-Stack Platform",
        summary = "Production-ready full-stack web application demonstrating modern Scala development. Features ZIO 2.0 effects, type-safe API definitions with Tapir, and reactive UI with Laminar.",
        category = ProjectCategory.Development,
        status = ProjectStatus.Completed,
        techStack = List("Scala", "ZIO 2.0", "Tapir", "Laminar", "PostgreSQL", "Quill"),
        order = 1,
        githubUrl = Some("https://github.com/orbit-sca/kemberco"),
        liveUrl = Some("https://kember.co"),
        architectureNotes = Some("Three-module architecture: shared cross-compiled code, JVM backend with Flyway migrations, ScalaJS frontend with Vite build orchestration.")
      ),
      Project(
        id = "scalia-os",
        title = "ScaliaOS Agent Framework",
        summary = "Scala-Julia bridge for AI agent orchestration. Implements registry-factory-executor pattern for multi-agent support including LLM conversational agents and blockchain trading agents.",
        category = ProjectCategory.Development,
        status = ProjectStatus.InProgress,
        techStack = List("Scala", "Julia", "ZIO", "Tapir", "REST API"),
        order = 2,
        githubUrl = Some("https://github.com/orbit-sca/ScaliaOS"),
        architectureNotes = Some("Type-safe routing with agent orchestration. Production-ready error handling and timeout management. Modular architecture with clear domain boundaries.")
      ),
      Project(
        id = "modernscale-portfolio",
        title = "ModernScale Portfolio",
        summary = "This portfolio site - a dark-themed, data-inspired showcase built with functional Scala. Features type-safe routing, reactive components, and ZIO HTTP backend.",
        category = ProjectCategory.Development,
        status = ProjectStatus.Completed,
        techStack = List("Scala.js", "Laminar", "ZIO", "Tapir", "Vite"),
        order = 3,
        liveUrl = Some("https://modernscale.dev"),
        architectureNotes = Some("Monorepo structure with shared domain models. Tapir endpoints compiled to both server routes and client API calls.")
      ),

      // Analytics Projects
      Project(
        id = "ethereum-activity-excel",
        title = "Ethereum Activity Analysis",
        summary = "Comprehensive blockchain transaction analysis using Excel Power Query, Pivot Tables, and advanced formulas. Analyzing 7,984+ transactions from Vitalik Buterin's wallet ($197M+ volume) with automated data refresh and interactive visualizations.",
        category = ProjectCategory.Analytics,
        status = ProjectStatus.Completed,
        techStack = List("Excel", "Power Query", "Pivot Tables", "Data Modeling", "Etherscan API"),
        order = 4,
        embedUrl = Some("https://1drv.ms/x/c/1b1bdc2923b95b74/IQTmjL42IfkjR4q5fOegmIgyAcAWxrza8kIebcdq9vKsybw"),
        liveUrl = Some("https://1drv.ms/x/c/1b1bdc2923b95b74/IQTmjL42IfkjR4q5fOegmIgyAb9tjuRicgZSlxfi_Uzbdm8"),
        keyInsights = List(
          "Automated data ingestion from Etherscan API using Power Query",
          "Interactive pivot tables analyzing transaction patterns across 2015-2024",
          "Advanced formulas calculating volume trends, gas usage, and time-series analysis",
          "Visualizations showing $197M+ in transaction volume with dynamic filtering"
        )
      ),
      Project(
        id = "facility-usage-analysis",
        title = "Facility Usage Analytics",
        summary = "Dashboard and reporting system built during Air Force Civilian Service. Analyzed property usage data and created visual reports for leadership decision-making.",
        category = ProjectCategory.Analytics,
        status = ProjectStatus.Completed,
        techStack = List("Excel", "Data Visualization", "Reporting"),
        order = 1,
        keyInsights = List(
          "Collected and analyzed property usage patterns across multiple facilities",
          "Created dashboards for leadership visibility into operations",
          "Audited equipment data improving reporting accuracy"
        )
      ),
      Project(
        id = "real-estate-document-analysis",
        title = "Real Estate Document Analysis",
        summary = "High-volume document review and quality assurance for mortgage compliance operations. Analyzing legal instruments, identifying discrepancies, and ensuring data accuracy across complex financial documentation.",
        category = ProjectCategory.Analytics,
        status = ProjectStatus.InProgress,
        techStack = List("Document Analysis", "Quality Assurance", "Excel", "Data Validation"),
        order = 2,
        keyInsights = List(
          "Processing 100+ mortgage documents and security instruments weekly",
          "Conducting discrepancy analysis across multi-party financial transactions",
          "Ensuring compliance and data accuracy in high-stakes real estate operations"
        )
      ),
      Project(
        id = "wgu-data-analytics",
        title = "WGU Data Analytics Coursework",
        summary = "Academic project work for B.S. Data Analytics degree. Applying statistical analysis, data mining, and visualization techniques to solve real-world business problems.",
        category = ProjectCategory.Analytics,
        status = ProjectStatus.InProgress,
        techStack = List("Python", "R", "SQL", "Tableau", "Statistical Analysis"),
        order = 3,
        keyInsights = List(
          "Currently pursuing B.S. Data Analytics at Western Governors University",
          "Hands-on coursework in predictive modeling and data mining",
          "Building toward M.S. Data Intelligence at USF (Expected 2028)"
        )
      )
    )

    override def listProjects(filter: ProjectFilter): Task[List[Project]] =
      ZIO.succeed {
        projects
          .filter(p => filter.category.forall(_ == p.category))
          .filter(p => filter.status.forall(_ == p.status))
          .sortBy(_.order)
      }

    override def getProject(id: String): Task[Option[Project]] =
      ZIO.succeed(projects.find(_.id == id))

    override def listByCategory(category: ProjectCategory): Task[List[Project]] =
      listProjects(ProjectFilter.byCategory(category))

  end InMemory

  /** Create an in-memory project service layer */
  val inMemory: ULayer[ProjectService] =
    ZLayer.succeed(new InMemory)

  /** List projects with optional filtering */
  def listProjects(filter: ProjectFilter): ZIO[ProjectService, Throwable, List[Project]] =
    ZIO.serviceWithZIO[ProjectService](_.listProjects(filter))

  /** Get a project by ID */
  def getProject(id: String): ZIO[ProjectService, Throwable, Option[Project]] =
    ZIO.serviceWithZIO[ProjectService](_.getProject(id))

  /** List projects by category */
  def listByCategory(category: ProjectCategory): ZIO[ProjectService, Throwable, List[Project]] =
    ZIO.serviceWithZIO[ProjectService](_.listByCategory(category))
