package rest

import actors.CartManager.Item
import actors.remote.ProductCatalog
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import database.ProductDatabase
import messages.ProductCatalogMessages.{SearchQuery, SearchQueryResponse}
import org.springframework.stereotype.Service

import scala.concurrent.Await
import scala.concurrent.duration._

trait ProductsService {
  def getProducts(parameters: List[String]): List[Item]
}

@Service
class ProductsServiceImpl extends ProductsService {

  var catalog: ActorRef = _

  {
    val remoteSystem = ActorSystem()
    catalog = remoteSystem.actorOf(Props(new ProductCatalog(new ProductDatabase)), "catalog")
  }

  override def getProducts(parameters: List[String]): List[Item] = {
    implicit val timeout = Timeout(30 seconds)
    val a = catalog ? SearchQuery(parameters)
    Await.result(a, timeout.duration).asInstanceOf[SearchQueryResponse].items
  }
}