package com.brice.routing

import com.raquo.laminar.api.L.{*, given}
import com.raquo.waypoint.*
import org.scalajs.dom

sealed trait Page
case object HomePage extends Page
case object DevelopmentPage extends Page
case object AnalyticsPage extends Page
case object LearnPage extends Page
case object ScalePage extends Page
case object AboutPage extends Page
case object ContactPage extends Page
case object NotFoundPage extends Page

object AppRouter {

  // Define routes using Waypoint's pattern syntax
  val homeRoute = Route.static(HomePage, root / endOfSegments)
  val developmentRoute = Route.static(DevelopmentPage, root / "development" / endOfSegments)
  val analyticsRoute = Route.static(AnalyticsPage, root / "analytics" / endOfSegments)
  val learnRoute = Route.static(LearnPage, root / "learn" / endOfSegments)
  val scaleRoute = Route.static(ScalePage, root / "scale" / endOfSegments)
  val aboutRoute = Route.static(AboutPage, root / "about" / endOfSegments)
  val contactRoute = Route.static(ContactPage, root / "contact" / endOfSegments)

  // Catch-all route for 404 - must be last in the routes list
  val notFoundRoute = Route[NotFoundPage.type, List[String]](
    encode = _ => List.empty,
    decode = _ => NotFoundPage,
    pattern = root / remainingSegments
  )

  // Create the router with proper configuration
  // Note: notFoundRoute must be LAST as routes are matched in order
  val router = new Router[Page](
    routes = List(homeRoute, developmentRoute, analyticsRoute, learnRoute, scaleRoute, aboutRoute, contactRoute, notFoundRoute),
    getPageTitle = {
      case HomePage => "ModernScale - Build & Scale Modern Systems"
      case DevelopmentPage => "Development - ModernScale"
      case AnalyticsPage => "Analytics - ModernScale"
      case LearnPage => "Build - ModernScale"
      case ScalePage => "Scale - ModernScale"
      case AboutPage => "About - ModernScale"
      case ContactPage => "Contact - ModernScale"
      case NotFoundPage => "Page Not Found - ModernScale"
    },
    serializePage = page => page match {
      case HomePage => "/"
      case DevelopmentPage => "/development"
      case AnalyticsPage => "/analytics"
      case LearnPage => "/learn"
      case ScalePage => "/scale"
      case AboutPage => "/about"
      case ContactPage => "/contact"
      case NotFoundPage => "/404"
    },
    deserializePage = url => {
      // Simple URL-based routing - check the path
      val path = url.split('?').head
      if (path == "/" || path.isEmpty) HomePage
      else if (path == "/development") DevelopmentPage
      else if (path == "/analytics") AnalyticsPage
      else if (path == "/learn") LearnPage
      else if (path == "/scale") ScalePage
      else if (path == "/about") AboutPage
      else if (path == "/contact") ContactPage
      else NotFoundPage // Show 404 for unknown routes
    }
  )(
    popStateEvents = windowEvents(_.onPopState),
    owner = unsafeWindowOwner
  )

  def navigateTo(page: Page): Unit = {
    router.pushState(page)
    dom.window.scrollTo(0, 0)
  }
}
