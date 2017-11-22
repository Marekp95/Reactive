package actors.remote

import akka.actor.{Actor, PoisonPill}
import akka.event.LoggingReceive
import database.ProductDatabase
import messages.ProductCatalogMessages.{SearchQuery, SearchQueryResponse}

class Worker(productDatabase: ProductDatabase) extends Actor {
  override def receive: Receive = LoggingReceive {
    case SearchQuery(parameters) =>
      sender() ! SearchQueryResponse(productDatabase.processRequest(parameters))
      self ! PoisonPill
  }
}

object Worker {
  def apply(productDatabase: ProductDatabase): Worker = new Worker(productDatabase)
}
