package com.brice

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import com.brice.components.layout.Layout
import com.brice.routing.{AppRouter, Page}
import com.brice.pages

object Main:

  def main(args: Array[String]): Unit =
    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      appElement()
    )

  def appElement(): Element =
    val currentPageSignal = AppRouter.router.currentPageSignal

    Layout(
      div(
        child <-- currentPageSignal.map {
          case routing.HomePage => pages.HomePage()
          case routing.DevelopmentPage => pages.DevelopmentPage()
          case routing.AnalyticsPage => pages.AnalyticsPage()
          case routing.LearnPage => pages.LearnPage()
          case routing.ScalePage => pages.ScalePage()
          case routing.AboutPage => pages.AboutPage()
          case routing.ContactPage => pages.ContactPage()
          case routing.NotFoundPage => pages.NotFoundPage()
        }
      )
    )
