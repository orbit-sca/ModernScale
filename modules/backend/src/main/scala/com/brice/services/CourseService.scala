package com.brice.services

import zio.*
import com.brice.domain.*

/** Service for managing courses */
trait CourseService:
  def listCourses: Task[List[Course]]
  def getCourse(id: String): Task[Option[Course]]

object CourseService:

  /** In-memory implementation with course catalog */
  final class InMemory extends CourseService:

    private val courses: List[Course] = List(
      Course(
        id = "scala-fullstack-ai",
        title = "Build Full-Stack Scala Applications with AI",
        subtitle = "A practical, end-to-end workflow for modern Scala development",
        description = """Master the workflow of building production-ready Scala applications using AI as a productivity tool.
          |This course teaches real-world development practices with Scala, ZIO, Laminar, and Tapir while leveraging
          |Claude Code for syntax assistance and rapid prototyping. Learn structured, functional programming patterns
          |and build complete systems—not AI gimmicks, but actual engineering workflows.""".stripMargin,
        level = CourseLevel.Intermediate,
        price = 149.00,
        originalPrice = None,
        teachableUrl = "#",
        topics = List(
          "Building full-stack applications with Scala.js and ZIO",
          "Type-safe APIs with Tapir",
          "Reactive UIs with Laminar",
          "AI-assisted development workflows with Claude Code",
          "Functional programming patterns in production",
          "End-to-end project structure and architecture"
        ),
        duration = Some("6 hours"),
        lessons = Some(20),
        order = 1
      ),
      Course(
        id = "blockchain-literacy",
        title = "Blockchain Literacy",
        subtitle = "Foundational knowledge for understanding Web3",
        description = """A beginner-friendly course designed to build strong foundational understanding of blockchain,
          |crypto, and Web3. Learn the origins and history of blockchain technology, understand core concepts and
          |mental models, and explore the foundational systems that shaped the space. This is not about trading or
          |speculation—it's about gaining the literacy needed to confidently navigate Web3 conversations, products,
          |and ecosystems.""".stripMargin,
        level = CourseLevel.Beginner,
        price = 79.00,
        originalPrice = Some(129.00),
        teachableUrl = "#",
        topics = List(
          "Origins and history of blockchain technology",
          "Understanding distributed ledgers and consensus",
          "Key figures and foundational systems in Web3",
          "Mental models for thinking about decentralization",
          "Core blockchain concepts explained simply",
          "Navigating the Web3 ecosystem with confidence"
        ),
        duration = Some("4 hours"),
        lessons = Some(15),
        order = 2
      )
    )

    override def listCourses: Task[List[Course]] =
      ZIO.succeed(courses.sortBy(_.order))

    override def getCourse(id: String): Task[Option[Course]] =
      ZIO.succeed(courses.find(_.id == id))

  end InMemory

  /** Create an in-memory course service layer */
  val inMemory: ULayer[CourseService] =
    ZLayer.succeed(new InMemory)

  /** List all courses */
  def listCourses: ZIO[CourseService, Throwable, List[Course]] =
    ZIO.serviceWithZIO[CourseService](_.listCourses)

  /** Get a course by ID */
  def getCourse(id: String): ZIO[CourseService, Throwable, Option[Course]] =
    ZIO.serviceWithZIO[CourseService](_.getCourse(id))
