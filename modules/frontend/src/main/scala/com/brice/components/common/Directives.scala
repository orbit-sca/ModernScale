package com.brice.components.common

import com.raquo.laminar.api.L._
import org.scalajs.dom
import scala.scalajs.js

object Directives {

  /** Animate element on scroll using IntersectionObserver */
  def animateOnScroll(
                       delaySeconds: Double = 0,
                       threshold: Double = 0.1,
                       initialYOffsetPx: Int = 20
                     ): Modifier[HtmlElement] =
    onMountUnmountCallback(
      mount = (ctx: MountContext[HtmlElement]) => {
        val el = ctx.thisNode.ref

        // Initial styles
        el.style.opacity = "0"
        el.style.transform = s"translateY(${initialYOffsetPx}px)"
        el.style.transition = "opacity 0.6s ease, transform 0.6s ease"

        val thresholdParam = threshold // avoid ambiguity

        lazy val observer: dom.IntersectionObserver = new dom.IntersectionObserver(
          (entries: js.Array[dom.IntersectionObserverEntry], _: dom.IntersectionObserver) =>
            entries.foreach { entry =>
              if (entry.isIntersecting) {
                dom.window.setTimeout(() => {
                  el.style.opacity = "1"
                  el.style.transform = "translateY(0)"
                }, (delaySeconds * 1000).toInt)
                observer.unobserve(el)
              }
            },
          new dom.IntersectionObserverInit {
            this.threshold = js.Array(thresholdParam)
          }
        )

        observer.observe(el)

        // Cleanup
        (): Unit
      },
      unmount = (_: HtmlElement) => ()
    )

  /** Simple hover lift effect */
  def hoverLift(liftPx: Int = 8, transition: String = "0.3s ease"): Modifier[HtmlElement] =
    onMountUnmountCallback(
      mount = (ctx: MountContext[HtmlElement]) => {
        val elRef = ctx.thisNode.ref
        val originalTransform = elRef.style.transform
        elRef.style.transition = s"transform $transition, box-shadow $transition"

        val mouseEnter: js.Function1[dom.MouseEvent, Unit] = (_: dom.MouseEvent) =>
          elRef.style.transform = s"$originalTransform translateY(-$liftPx}px)"
        val mouseLeave: js.Function1[dom.MouseEvent, Unit] = (_: dom.MouseEvent) =>
          elRef.style.transform = originalTransform

        elRef.addEventListener("mouseenter", mouseEnter)
        elRef.addEventListener("mouseleave", mouseLeave)

        // Cleanup
        (): Unit
      },
      unmount = (_: HtmlElement) => ()
    )

  /** Apply background color */
  def backgroundColor(color: String): Modifier[HtmlElement] =
    onMountUnmountCallback(
      mount = (ctx: MountContext[HtmlElement]) => {
        ctx.thisNode.ref.style.backgroundColor = color
      },
      unmount = (_: HtmlElement) => ()
    )

}






