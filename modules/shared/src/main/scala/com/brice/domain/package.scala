package com.brice.domain

import zio.json.*

/** Project category - distinguishes between development and analytics work */
enum ProjectCategory derives JsonCodec:
  case Development
  case Analytics

object ProjectCategory:
  given ordering: Ordering[ProjectCategory] = Ordering.by(_.ordinal)

/** Project status indicating current state of development */
enum ProjectStatus derives JsonCodec:
  case Completed
  case InProgress
  case Planned

object ProjectStatus:
  given ordering: Ordering[ProjectStatus] = Ordering.by(_.ordinal)

/**
 * Domain model for portfolio projects.
 *
 * Represents both development projects (with architecture notes, code samples)
 * and analytics projects (with embedded dashboards, key insights).
 */
final case class Project(
  id: String,
  title: String,
  summary: String,
  category: ProjectCategory,
  status: ProjectStatus,
  techStack: List[String],
  order: Int,
  githubUrl: Option[String] = None,
  liveUrl: Option[String] = None,
  imageUrl: Option[String] = None,
  embedUrl: Option[String] = None,           // For analytics dashboards
  architectureNotes: Option[String] = None,  // For development projects
  keyInsights: List[String] = List.empty     // For analytics projects
)

object Project:
  given JsonCodec[Project] = DeriveJsonCodec.gen[Project]

/**
 * Filter criteria for querying projects.
 * All fields are optional - omitted fields match all values.
 */
final case class ProjectFilter(
  category: Option[ProjectCategory] = None,
  status: Option[ProjectStatus] = None
)

object ProjectFilter:
  given JsonCodec[ProjectFilter] = DeriveJsonCodec.gen[ProjectFilter]

  val all: ProjectFilter = ProjectFilter()
  def byCategory(cat: ProjectCategory): ProjectFilter = ProjectFilter(category = Some(cat))
  def byStatus(st: ProjectStatus): ProjectFilter = ProjectFilter(status = Some(st))

/** Course difficulty level */
enum CourseLevel derives JsonCodec:
  case Beginner
  case Intermediate
  case Advanced

object CourseLevel:
  given ordering: Ordering[CourseLevel] = Ordering.by(_.ordinal)

/**
 * Domain model for online courses.
 * Courses are displayed on the Learn page and link to Teachable for content delivery.
 */
final case class Course(
  id: String,
  title: String,
  subtitle: String,
  description: String,
  level: CourseLevel,
  price: Double,
  originalPrice: Option[Double] = None,       // For showing discounts
  currency: String = "USD",
  teachableUrl: String,                       // Link to Teachable course
  imageUrl: Option[String] = None,
  topics: List[String] = List.empty,          // What you'll learn
  duration: Option[String] = None,            // e.g., "4 hours", "6 weeks"
  lessons: Option[Int] = None,                // Number of lessons
  order: Int = 0
)

object Course:
  given JsonCodec[Course] = DeriveJsonCodec.gen[Course]
