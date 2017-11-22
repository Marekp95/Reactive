package actors.remote

import java.util.stream.IntStream

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.LoggingReceive
import database.ProductDatabase
import messages.ProductCatalogMessages.{SearchQuery, SearchQueryResponse}

class ProductCatalog(productDatabase: ProductDatabase) extends Actor {
  override def receive: Receive = LoggingReceive {
    case SearchQuery(parameters) =>
      context.actorOf(Props(Worker(productDatabase))).forward(SearchQuery(parameters))
  }
}

object ProductCatalog {
  def apply: ProductCatalog = new ProductCatalog(new ProductDatabase())

  def main(args: Array[String]): Unit = {
    val remoteSystem = ActorSystem()
    val productCatalog = remoteSystem.actorOf(Props(new ProductCatalog(new ProductDatabase)))
    val client = remoteSystem.actorOf(Props(new Actor {
      override def receive: Receive = {
        case SearchQuery(parameters) => productCatalog ! SearchQuery(parameters)
        case SearchQueryResponse(response) => println(response)
      }
    }))
    IntStream.range(0, 10000).forEach(_ =>
      client ! SearchQuery(List("Ale", "Bigfoot"))
    )
  }
}