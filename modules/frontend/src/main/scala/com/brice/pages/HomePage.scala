package com.brice.pages

import com.raquo.laminar.api.L._
import com.brice.components.sections.*

object HomePage {

  def apply(): HtmlElement =
    div(
      HeroSection(),
      CategoryCardsSection(),
      ProjectsSection(),
      SkillsSection(),
      ContactCTASection()
    )
}
