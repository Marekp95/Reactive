package actors.remote

import java.util.stream.IntStream

import akka.actor.{Actor, ActorSystem, Props, Terminated}
import akka.event.LoggingReceive
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import database.ProductDatabase
import messages.ProductCatalogMessages.{SearchQuery, SearchQueryResponse}

class ProductCatalog(productDatabase: ProductDatabase) extends Actor {

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props(Worker(productDatabase)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Receive = LoggingReceive {
    case SearchQuery(parameters) =>
      router.route(SearchQuery(parameters), context.sender())
    case Terminated(a) ⇒
      router = router.removeRoutee(a)
      val r = context.actorOf(Props(Worker(productDatabase)))
      context watch r
      router = router.addRoutee(r)
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