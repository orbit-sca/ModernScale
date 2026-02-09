package com.brice.components.layout

import com.raquo.laminar.api.L._

object Layout {

  def apply(content: HtmlElement): HtmlElement =
    div(
      NavBar(),
      content,
      Footer()
    )
}