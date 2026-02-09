package com.brice.services

import zio.*
import zio.stream.*
import com.brice.domain.TransactionRow
import com.brice.repositories.TransactionRepository

/**
 * Service for exporting transaction data to CSV format for Excel Power Query.
 *
 * Design principles:
 * - Streaming output for large datasets
 * - Stable column ordering
 * - Excel-friendly formatting
 * - UTF-8 BOM for Excel compatibility
 */
trait CsvExportService:
  /** Generate CSV content as a stream */
  def exportToCsvStream(): Stream[Throwable, Byte]

  /** Generate CSV content as a string (for smaller datasets) */
  def exportToCsvString(): Task[String]

object CsvExportService:

  final class Live(repository: TransactionRepository) extends CsvExportService:

    /** UTF-8 BOM (Byte Order Mark) for Excel compatibility */
    private val UTF8_BOM = Array[Byte](0xEF.toByte, 0xBB.toByte, 0xBF.toByte)

    override def exportToCsvStream(): Stream[Throwable, Byte] =
      val headerStream = ZStream.fromIterable(UTF8_BOM) ++
        ZStream.fromIterable(s"${TransactionRow.csvHeader}\n".getBytes("UTF-8"))

      val dataStream = ZStream.fromZIO(repository.findAll())
        .flatMap(ZStream.fromIterable)
        .map(tx => s"${tx.toCsv}\n")
        .flatMap(line => ZStream.fromIterable(line.getBytes("UTF-8")))

      headerStream ++ dataStream

    override def exportToCsvString(): Task[String] =
      for
        transactions <- repository.findAll()
        header = TransactionRow.csvHeader
        rows = transactions.map(_.toCsv)
        csv = (header :: rows).mkString("\n")
      yield csv

  end Live

  /** ZLayer for live implementation */
  val live: ZLayer[TransactionRepository, Nothing, CsvExportService] =
    ZLayer.fromFunction(Live.apply)

  /** Accessor methods */
  def exportToCsvStream(): ZStream[CsvExportService, Throwable, Byte] =
    ZStream.serviceWithStream[CsvExportService](_.exportToCsvStream())

  def exportToCsvString(): ZIO[CsvExportService, Throwable, String] =
    ZIO.serviceWithZIO[CsvExportService](_.exportToCsvString())
