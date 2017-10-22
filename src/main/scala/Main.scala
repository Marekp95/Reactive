import actors.v2.{Cart, Checkout}
import akka.actor.{ActorSystem, Props}
import events.CartEvents.{ItemAdded, ItemRemoved, StartCheckout}
import events.CheckoutEvents.{DeliveryMethodSelected, PaymentReceived, PaymentSelected}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main {

  def main(args: Array[String]): Unit = {
    // test function
    val system = ActorSystem()
    val cartActor = system.actorOf(Props[Cart[Int]])
    val checkoutActor = system.actorOf(Props[Checkout[Int]])

    cartActor ! ItemAdded(7)
    Thread.sleep(1000)
    cartActor ! ItemAdded(4)
    cartActor ! ItemAdded(9)
    cartActor ! ItemRemoved(9)
    cartActor ! ItemRemoved(4)
    cartActor ! ItemAdded(4)
    cartActor ! StartCheckout

    checkoutActor ! DeliveryMethodSelected
    checkoutActor ! PaymentSelected
    checkoutActor ! PaymentReceived
    Thread.sleep(100)
    checkoutActor ! PaymentReceived

    Await.result(system.whenTerminated, Duration.Inf)
  }
}
