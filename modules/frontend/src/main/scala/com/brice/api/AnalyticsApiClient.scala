package com.brice.api

import com.brice.domain.*
import org.scalajs.dom
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import zio.json.*

/** API client for blockchain analytics endpoints */
object AnalyticsApiClient:

  /** Fetch all transactions with optional filtering */
  def listTransactions(
    network: Option[BlockchainNetwork] = None,
    limit: Option[Int] = Some(100)
  ): Future[Either[AnalyticsError, List[TransactionRow]]] =
    val params = List(
      network.map(n => s"network=${n.toString}"),
      limit.map(l => s"limit=$l")
    ).flatten.mkString("&")

    val url = if params.isEmpty then "/analytics/transactions" else s"/analytics/transactions?$params"

    dom.fetch(url)
      .toFuture
      .flatMap(_.text().toFuture)
      .map { body =>
        body.fromJson[List[TransactionRow]] match
          case Right(transactions) => Right(transactions)
          case Left(error) => Left(AnalyticsError(s"Failed to parse transactions: $error"))
      }
      .recover { case error =>
        Left(AnalyticsError("Network error", Some(error.getMessage)))
      }

  /** Fetch analytics statistics */
  def getStats(): Future[Either[AnalyticsError, AnalyticsStats]] =
    dom.fetch("/analytics/stats")
      .toFuture
      .flatMap(_.text().toFuture)
      .map { body =>
        body.fromJson[AnalyticsStats] match
          case Right(stats) => Right(stats)
          case Left(error) => Left(AnalyticsError(s"Failed to parse stats: $error"))
      }
      .recover { case error =>
        Left(AnalyticsError("Network error", Some(error.getMessage)))
      }
