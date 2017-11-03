import java.net.URI

import actors.CartManager
import actors.CartManager.Item
import akka.actor.{ActorSystem, Props}
import messages.CartManagerMessages.{AddItem, RemoveItem}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main {

  def main(args: Array[String]): Unit = {
    // test function
    val system = ActorSystem()
    val cartManager = system.actorOf(Props[CartManager])
    cartManager ! AddItem(Item(new URI("7"), "7", BigDecimal(1.0), 1))
    cartManager ! AddItem(Item(new URI("11"), "11", BigDecimal(1.0), 1))
    cartManager ! AddItem(Item(new URI("13"), "13", BigDecimal(1.0), 1))
    cartManager ! RemoveItem(Item(new URI("11"), "11", BigDecimal(1.0), 1))
    //    cartManager ! StartCheckout
    Await.result(system.whenTerminated, Duration.Inf)
  }
}
