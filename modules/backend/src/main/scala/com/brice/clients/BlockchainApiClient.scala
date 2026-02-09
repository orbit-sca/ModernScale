package com.brice.clients

import zio.*
import zio.http.*
import zio.json.*
import com.brice.domain.{BlockchainNetwork, RawTransaction}

/**
 * Client for fetching blockchain transaction data from Etherscan-style APIs.
 *
 * Design principles:
 * - Pure functional, effect-safe with ZIO
 * - Explicit error handling
 * - Rate limiting and retry logic
 * - Testable via ZLayer
 */
trait BlockchainApiClient:
  /** Fetch all normal transactions for a wallet address */
  def fetchTransactions(
    network: BlockchainNetwork,
    address: String,
    apiKey: String,
    startBlock: Option[Long] = None,
    endBlock: Option[Long] = None
  ): Task[List[RawTransaction]]

object BlockchainApiClient:

  /** Live implementation using HTTP client */
  final class Live(client: Client) extends BlockchainApiClient:

    // Etherscan V2 API uses a unified endpoint for all networks
    private val apiEndpointV2 = "https://api.etherscan.io/v2/api"

    // Chain ID mapping for Etherscan V2 API
    private def chainId(network: BlockchainNetwork): Int = network match
      case BlockchainNetwork.Ethereum => 1
      case BlockchainNetwork.Base     => 8453
      case BlockchainNetwork.Polygon  => 137
      case BlockchainNetwork.Arbitrum => 42161
      case BlockchainNetwork.Optimism => 10

    override def fetchTransactions(
      network: BlockchainNetwork,
      address: String,
      apiKey: String,
      startBlock: Option[Long],
      endBlock: Option[Long]
    ): Task[List[RawTransaction]] =
      val networkChainId = chainId(network)
      val startBlockParam = startBlock.map(_.toString).getOrElse("0")
      val endBlockParam = endBlock.map(_.toString).getOrElse("99999999")

      val url = s"$apiEndpointV2?chainid=$networkChainId&module=account&action=txlist&address=$address" +
        s"&startblock=$startBlockParam&endblock=$endBlockParam" +
        s"&sort=asc&apikey=$apiKey"

      ZIO.logInfo(s"Fetching transactions from ${network.displayName} for $address") *>
      ZIO.scoped {
        client
          .request(Request.get(URL.decode(url).toOption.get))
          .flatMap { response =>
            response.body.asString.flatMap { bodyStr =>
              // Try parsing as success response first
              ZIO.fromEither(bodyStr.fromJson[EtherscanSuccessResponse])
                .map(_.result) // Extract transactions list
                .orElse {
                  // If that fails, try parsing as error response
                  ZIO.fromEither(bodyStr.fromJson[EtherscanErrorResponse])
                    .flatMap { errorResponse =>
                      ZIO.fail(new Exception(s"API error: ${errorResponse.message} - ${errorResponse.result}"))
                    }
                }
                .mapError { err =>
                  new Exception(s"Failed to parse response: $err")
                }
            }
          }
      }.tapError(err => ZIO.logError(s"Failed to fetch transactions: ${err.getMessage}"))
        .retry(Schedule.exponential(1.second) && Schedule.recurs(3))

  end Live

  /** Etherscan API success response */
  private final case class EtherscanSuccessResponse(
    status: String,
    message: String,
    result: List[RawTransaction]
  )

  private object EtherscanSuccessResponse:
    given JsonDecoder[EtherscanSuccessResponse] = DeriveJsonDecoder.gen[EtherscanSuccessResponse]

  /** Etherscan API error response */
  private final case class EtherscanErrorResponse(
    status: String,
    message: String,
    result: String
  )

  private object EtherscanErrorResponse:
    given JsonDecoder[EtherscanErrorResponse] = DeriveJsonDecoder.gen[EtherscanErrorResponse]

  /** ZLayer for live implementation - requires Client */
  val live: ZLayer[Client, Nothing, BlockchainApiClient] =
    ZLayer.fromFunction(Live.apply)

  /** Accessor methods */
  def fetchTransactions(
    network: BlockchainNetwork,
    address: String,
    apiKey: String,
    startBlock: Option[Long] = None,
    endBlock: Option[Long] = None
  ): ZIO[BlockchainApiClient, Throwable, List[RawTransaction]] =
    ZIO.serviceWithZIO[BlockchainApiClient](
      _.fetchTransactions(network, address, apiKey, startBlock, endBlock)
    )
