package com.brice.http

import zio.*
import zio.http.*
import zio.json.*
import com.brice.domain.*
import com.brice.services.{CsvExportService, TransactionIngestionService}
import com.brice.repositories.TransactionRepository

/**
 * HTTP routes for blockchain analytics data.
 *
 * Endpoints:
 * - GET /analytics/transactions.csv - Export all transactions as CSV for Excel
 * - GET /analytics/transactions - Get transactions as JSON
 * - POST /analytics/ingest - Trigger transaction ingestion (admin only)
 * - GET /analytics/stats - Get analytics statistics
 */
object AnalyticsRoutes:

  type AnalyticsEnv = CsvExportService & TransactionRepository & TransactionIngestionService

  def apply(): Routes[AnalyticsEnv, Response] =
    Routes(
      // CSV export endpoint for Excel Power Query
      Method.GET / "analytics" / "transactions.csv" -> handler {
        for
          csvService <- ZIO.service[CsvExportService]
        yield Response(
          status = Status.Ok,
          headers = Headers(
            Header.ContentType(MediaType.text.csv),
            Header.ContentDisposition.attachment("transactions.csv"),
            Header.Custom("Cache-Control", "no-cache")
          ),
          body = Body.fromStream(csvService.exportToCsvStream())
        )
      },

      // JSON endpoint for programmatic access
      Method.GET / "analytics" / "transactions" -> handler { (req: Request) =>
        val limit = req.url.queryParams
          .get("limit")
          .flatMap(_.headOption)
          .flatMap(s => scala.util.Try(s.toInt).toOption)
          .getOrElse(50)

        (for
          repo <- ZIO.service[TransactionRepository]
          transactions <- repo.findRecent(limit)
        yield Response.json(transactions.toJson))
          .mapError(err => Response.internalServerError(err.getMessage))
      },

      // Get analytics statistics
      Method.GET / "analytics" / "stats" -> handler {
        (for
          repo <- ZIO.service[TransactionRepository]
          totalCount <- repo.count()
          transactions <- repo.findAll()

          // Calculate stats
          totalVolumeUsd = transactions.map(_.amountUsd).sum
          avgTransactionUsd = if totalCount > 0 then totalVolumeUsd / totalCount else BigDecimal(0)
          uniqueChains = transactions.map(_.chain).distinct
          uniqueAddresses = transactions.map(_.fromAddress).distinct.length

          // Date range
          earliestDate = transactions.map(_.date).minOption.getOrElse(java.time.LocalDate.now())
          latestDate = transactions.map(_.date).maxOption.getOrElse(java.time.LocalDate.now())

          // Chain breakdown
          chainStats = transactions.groupBy(_.chain).map { case (chain, txs) =>
            ChainStats(chain, txs.length, txs.map(_.amountUsd).sum)
          }.toList

          stats = AnalyticsStats(
            totalTransactions = totalCount,
            totalVolumeUsd = totalVolumeUsd,
            avgTransactionUsd = avgTransactionUsd,
            uniqueChains = uniqueChains,
            uniqueAddresses = uniqueAddresses,
            earliestDate = earliestDate,
            latestDate = latestDate,
            chainBreakdown = chainStats
          )
        yield Response.json(stats.toJson))
          .mapError(err => Response.internalServerError(err.getMessage))
      },

      // Trigger ingestion (for admin/manual triggering)
      Method.POST / "analytics" / "ingest" -> handler { (req: Request) =>
        (for
          body <- req.body.asString
            .mapError(err => Response.internalServerError(s"Failed to read body: ${err.getMessage}"))
          _ <- ZIO.logInfo(s"Received ingestion request body: $body")
          parsed <- ZIO.fromEither(body.fromJson[IngestionConfig])
            .tapError(err => ZIO.logError(s"JSON parsing error: $err"))
            .mapError(err => Response.badRequest(s"Invalid config: $err"))
          _ <- ZIO.logInfo(s"Parsed config: $parsed")
          service <- ZIO.service[TransactionIngestionService]
          result <- service.ingestTransactions(parsed)
            .tapError(err => ZIO.logError(s"Ingestion error: ${err.getMessage}"))
            .mapError(err => Response.internalServerError(err.getMessage))
          _ <- ZIO.logInfo(s"Ingestion result: $result")
        yield Response.json(result.toJson))
          .catchAll(resp => ZIO.succeed(resp))
          .provideSomeLayer[AnalyticsEnv](ZLayer.environment[AnalyticsEnv])
      }
    )

