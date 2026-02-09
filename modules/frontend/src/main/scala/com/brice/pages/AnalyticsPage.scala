package com.brice.pages

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.components.sections.ProjectCard
import com.brice.api.{ProjectApiClient, AnalyticsApiClient}
import com.brice.domain.*
import scala.concurrent.ExecutionContext.Implicits.global

/** Combined Analytics page - skills, Excel work, and live blockchain dashboard */
object AnalyticsPage:

  def apply(): HtmlElement =
    // State for projects and loading
    val projectsVar = Var[List[Project]](List.empty)
    val isLoading = Var(true)
    val errorMessage = Var(Option.empty[String])

    // State for blockchain data
    val transactionsVar = Var[List[TransactionRow]](List.empty)
    val statsVar = Var(Option.empty[AnalyticsStats])
    val blockchainLoading = Var(true)
    val blockchainError = Var(Option.empty[String])

    // Fetch projects on mount
    val fetchProjects = Observer[Unit] { _ =>
      isLoading.set(true)
      errorMessage.set(None)

      ProjectApiClient.listAnalyticsProjects().foreach {
        case Right(projects) =>
          projectsVar.set(projects)
          isLoading.set(false)
        case Left(error) =>
          errorMessage.set(Some(error.error))
          isLoading.set(false)
      }
    }

    // Fetch blockchain data
    val fetchBlockchainData = Observer[Unit] { _ =>
      blockchainLoading.set(true)
      blockchainError.set(None)

      AnalyticsApiClient.listTransactions(limit = Some(20)).foreach {
        case Right(txs) =>
          transactionsVar.set(txs)
          blockchainLoading.set(false)
        case Left(error) =>
          blockchainError.set(Some(error.error))
          blockchainLoading.set(false)
      }

      AnalyticsApiClient.getStats().foreach {
        case Right(stats) => statsVar.set(Some(stats))
        case Left(_) => ()
      }
    }

    div(
      cls := "analytics-page",
      styleAttr := s"min-height: 100vh; background: ${Colors.accent}; color: ${Colors.text};",

      // Trigger both fetches on mount
      onMountCallback { _ =>
        fetchProjects.onNext(())
        fetchBlockchainData.onNext(())
      },

      // Background grid
      div(
        styleAttr := s"""
          position: absolute;
          inset: 0;
          background-image:
            linear-gradient(rgba(217, 38, 38, 0.02) 1px, transparent 1px),
            linear-gradient(90deg, rgba(217, 38, 38, 0.02) 1px, transparent 1px);
          background-size: 64px 64px;
          pointer-events: none;
        """
      ),

      // Glowing orb decoration (left side for visual variety from Development page)
      div(
        styleAttr := s"""
          position: absolute;
          top: 0;
          left: 0;
          width: 800px;
          height: 800px;
          background: radial-gradient(circle, rgba(217, 38, 38, 0.1) 0%, transparent 70%);
          pointer-events: none;
        """
      ),

      // Content
      div(
        styleAttr := s"""
          position: relative;
          z-index: 10;
          padding-top: 128px;
          padding-bottom: 96px;
          padding-left: ${Spacing.lg};
          padding-right: ${Spacing.lg};
        """,

        div(
          styleAttr := s"max-width: ${Layout.containerMaxWidth}; margin: 0 auto;",

          // Header
          div(
            styleAttr := s"margin-bottom: ${Spacing.xxl};",

            // Badge
            div(
              styleAttr := s"""
                display: inline-flex;
                align-items: center;
                gap: ${Spacing.xs};
                padding: ${Spacing.xs} ${Spacing.md};
                background: rgba(255, 255, 255, 0.05);
                border: 1px solid rgba(217, 38, 38, 0.3);
                border-radius: ${BorderRadius.pill};
                margin-bottom: ${Spacing.lg};
              """,
              span(
                styleAttr := s"""
                  font-family: ${Typography.monoFamily};
                  font-size: 11px;
                  color: ${Colors.textLight};
                  text-transform: uppercase;
                  letter-spacing: 0.15em;
                """,
                "Analytics"
              )
            ),

            // Title
            h1(
              styleAttr := s"""
                font-size: clamp(40px, 8vw, ${Typography.FontSize.hero});
                font-weight: ${Typography.FontWeight.regular};
                color: ${Colors.text};
                letter-spacing: -0.02em;
                margin-bottom: ${Spacing.lg};
              """,
              "Data & Insights",
              span(styleAttr := s"color: ${Colors.primary};", ".")
            ),

            // Description
            p(
              styleAttr := s"""
                font-size: ${Typography.FontSize.lg};
                color: ${Colors.textLight};
                max-width: 700px;
                line-height: 1.7;
              """,
              "Building dashboards and reports that drive decisions. ",
              "Excel, SQL, and Python for data analysis — currently pursuing B.S. Data Analytics at WGU."
            )
          ),

          // Projects list
          div(
            styleAttr := s"display: grid; gap: ${Spacing.xl};",

            // Loading state
            child <-- isLoading.signal.map { loading =>
              if loading then
                div(
                  styleAttr := "text-align: center; padding: 80px 0;",
                  div(
                    styleAttr := s"""
                      display: inline-block;
                      width: 32px;
                      height: 32px;
                      border: 2px solid rgba(217, 38, 38, 0.2);
                      border-top-color: ${Colors.primary};
                      border-radius: 50%;
                      animation: spin 1s linear infinite;
                    """
                  )
                )
              else emptyNode
            },

            // Error state
            child <-- errorMessage.signal.map {
              case Some(error) =>
                div(
                  styleAttr := s"""
                    text-align: center;
                    padding: 80px 0;
                    color: ${Colors.textLight};
                  """,
                  chartIconLarge,
                  p(
                    styleAttr := s"font-size: ${Typography.FontSize.lg}; margin-top: ${Spacing.md};",
                    s"Error loading projects: $error"
                  )
                )
              case None => emptyNode
            },

            // Projects
            children <-- projectsVar.signal.combineWith(isLoading.signal).map { case (projects, loading) =>
              if !loading && projects.isEmpty then
                List(
                  div(
                    styleAttr := s"""
                      text-align: center;
                      padding: 80px 0;
                    """,
                    chartIconLarge,
                    p(
                      styleAttr := s"font-size: ${Typography.FontSize.lg}; color: ${Colors.textLight}; margin-top: ${Spacing.md};",
                      "No analytics projects yet."
                    )
                  )
                )
              else
                projects.zipWithIndex.map { case (project, index) =>
                  ProjectCard(project, index)
                }
            }
          ),

          // Live Blockchain Dashboard Section
          blockchainDashboardSection(statsVar, transactionsVar, blockchainLoading, blockchainError)
        )
      )
    )

  // ========== Live Blockchain Dashboard Section ==========

  private def blockchainDashboardSection(
    statsVar: Var[Option[AnalyticsStats]],
    transactionsVar: Var[List[TransactionRow]],
    isLoading: Var[Boolean],
    errorMessage: Var[Option[String]]
  ): HtmlElement =
    div(
      styleAttr := s"margin-top: ${Spacing.xxxl};",

      // Section header
      h2(
        styleAttr := s"""
          font-size: ${Typography.FontSize.xxxl};
          font-weight: ${Typography.FontWeight.medium};
          color: ${Colors.text};
          margin-bottom: ${Spacing.lg};
        """,
        "Live Database Query",
        span(styleAttr := s"color: ${Colors.primary};", ".")
      ),

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.lg};
          color: ${Colors.textLight};
          margin-bottom: ${Spacing.xxl};
          max-width: 700px;
        """,
        "Same dataset as the Excel analysis above - querying Vitalik Buterin's transaction data directly from the PostgreSQL database via ZIO HTTP backend. Showing most recent 20 transactions."
      ),

      // Stats cards
      child <-- statsVar.signal.map {
        case Some(stats) => statsCardsRow(stats)
        case None => emptyNode
      },

      // Transactions section
      div(
        styleAttr := s"margin-top: ${Spacing.xxl};",

        h3(
          styleAttr := s"""
            font-size: ${Typography.FontSize.xl};
            font-weight: ${Typography.FontWeight.medium};
            color: ${Colors.text};
            margin-bottom: ${Spacing.lg};
          """,
          "Recent Transactions"
        ),

        // Loading/Error/Transactions
        div(
          styleAttr := s"display: grid; gap: ${Spacing.lg};",

          child <-- isLoading.signal.map { loading =>
            if loading then loadingSpinner() else emptyNode
          },

          child <-- errorMessage.signal.map {
            case Some(error) => errorDisplay(error)
            case None => emptyNode
          },

          children <-- transactionsVar.signal.combineWith(isLoading.signal).map {
            case (txs, loading) =>
              if !loading && txs.isEmpty then
                List(emptyState())
              else
                txs.zipWithIndex.map { case (tx, index) =>
                  transactionCard(tx, index)
                }
          }
        )
      )
    )

  private def statsCardsRow(stats: AnalyticsStats): HtmlElement =
    div(
      styleAttr := s"""
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(min(100%, 250px), 1fr));
        gap: ${Spacing.lg};
        margin-bottom: ${Spacing.xxl};
      """,

      statCard("Total Transactions", stats.totalTransactions.toString, "All networks"),
      statCard("Total Volume", f"$$${stats.totalVolumeUsd}%.0f", "USD equivalent"),
      statCard("Average Transaction", f"$$${stats.avgTransactionUsd}%.2f", "Mean value"),
      statCard("Date Range", s"${stats.earliestDate.getYear} - ${stats.latestDate.getYear}", s"${stats.chainBreakdown.length} chains")
    )

  private def statCard(title: String, value: String, subtitle: String): HtmlElement =
    div(
      styleAttr := s"""
        padding: ${Spacing.lg};
        background: ${Colors.cardBackground};
        border: 1px solid ${Colors.glassBorder};
        border-radius: ${BorderRadius.lg};
        transition: transform 0.2s ease, border-color 0.2s ease;
        animation: fadeInUp 0.6s ease both;
      """,

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.sm};
          color: ${Colors.textLight};
          margin: 0 0 ${Spacing.sm} 0;
          text-transform: uppercase;
          letter-spacing: 0.05em;
        """,
        title
      ),

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.xxl};
          font-weight: ${Typography.FontWeight.medium};
          color: ${Colors.text};
          margin: 0 0 ${Spacing.xs} 0;
        """,
        value
      ),

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.xs};
          color: ${Colors.textLight};
          margin: 0;
        """,
        subtitle
      )
    )

  private def transactionCard(tx: TransactionRow, index: Int): HtmlElement =
    div(
      styleAttr := s"""
        padding: ${Spacing.lg};
        background: ${Colors.cardBackground};
        border: 1px solid ${Colors.glassBorder};
        border-radius: ${BorderRadius.lg};
        transition: all 0.2s ease;
        animation: fadeInUp 0.6s ease ${index * 0.03}s both;
      """,

      // Row 1: Hash + Chain
      div(
        styleAttr := "display: flex; justify-content: space-between; align-items: start; margin-bottom: 12px;",

        span(
          styleAttr := s"""
            font-family: ${Typography.monoFamily};
            font-size: 12px;
            color: ${Colors.textLight};
            word-break: break-all;
          """,
          tx.txHash.take(16) + "..."
        ),

        span(
          styleAttr := s"""
            background: ${Colors.primary};
            color: white;
            padding: 4px 12px;
            border-radius: ${BorderRadius.sm};
            font-size: 12px;
            font-weight: ${Typography.FontWeight.medium};
            white-space: nowrap;
          """,
          tx.chain
        )
      ),

      // Row 2: Amount + USD
      div(
        styleAttr := "margin-bottom: 8px;",

        span(
          styleAttr := s"""
            color: ${Colors.text};
            font-size: ${Typography.FontSize.lg};
            font-weight: ${Typography.FontWeight.medium};
          """,
          f"${tx.amountNative}%.4f ETH"
        ),

        span(
          styleAttr := s"""
            color: ${Colors.textLight};
            margin-left: ${Spacing.sm};
            font-size: ${Typography.FontSize.base};
          """,
          f"≈ $$${tx.amountUsd}%.2f"
        )
      ),

      // Row 3: Date + Gas
      div(
        styleAttr := s"display: flex; gap: ${Spacing.md}; font-size: ${Typography.FontSize.sm}; color: ${Colors.textLight};",

        span(tx.date.toString),

        tx.gasUsed.map { gas =>
          span(s"Gas: $gas")
        }.getOrElse(emptyNode),

        tx.gasPriceGwei.map { price =>
          span(f"$price%.2f Gwei")
        }.getOrElse(emptyNode)
      )
    )

  private def loadingSpinner(): HtmlElement =
    div(
      styleAttr := "text-align: center; padding: 80px 0;",
      div(
        styleAttr := s"""
          display: inline-block;
          width: 32px;
          height: 32px;
          border: 2px solid rgba(217, 38, 38, 0.2);
          border-top-color: ${Colors.primary};
          border-radius: 50%;
          animation: spin 1s linear infinite;
        """
      )
    )

  private def errorDisplay(error: String): HtmlElement =
    div(
      styleAttr := s"""
        text-align: center;
        padding: 80px 0;
        color: ${Colors.textLight};
      """,
      p(
        styleAttr := s"font-size: ${Typography.FontSize.lg};",
        s"Error: $error"
      )
    )

  private def emptyState(): HtmlElement =
    div(
      styleAttr := s"""
        text-align: center;
        padding: 80px 0;
        color: ${Colors.textLight};
      """,
      chartIconLarge,
      p(
        styleAttr := s"font-size: ${Typography.FontSize.lg}; margin-top: ${Spacing.md};",
        "No transactions found"
      )
    )

  // Icons
  private def chartIconLarge: SvgElement =
    svg.svg(
      svg.width := "64",
      svg.height := "64",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := Colors.lightGray,
      svg.strokeWidth := "1",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.style := "margin: 0 auto; display: block;",
      svg.line(svg.x1 := "18", svg.y1 := "20", svg.x2 := "18", svg.y2 := "10"),
      svg.line(svg.x1 := "12", svg.y1 := "20", svg.x2 := "12", svg.y2 := "4"),
      svg.line(svg.x1 := "6", svg.y1 := "20", svg.x2 := "6", svg.y2 := "14")
    )
