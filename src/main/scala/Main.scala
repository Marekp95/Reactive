import actors.{Cart, Checkout}
import akka.actor.{ActorSystem, Props}
import events.CartEvents.{AddItem, RemoveItem, StartCheckout}
import events.CheckoutEvents.{DeliveryMethodSelected, PaymentReceived, PaymentSelected}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main {

  def main(args: Array[String]): Unit = {
    // test function
    val system = ActorSystem()
    val cartActor = system.actorOf(Props[Cart[Int]])
    val checkoutActor = system.actorOf(Props[Checkout[Int]])

    cartActor ! AddItem(7)
    Thread.sleep(1000)
    cartActor ! AddItem(4)
    cartActor ! AddItem(9)
    cartActor ! RemoveItem(9)
    cartActor ! RemoveItem(4)
    cartActor ! AddItem(4)
    cartActor ! StartCheckout

    checkoutActor ! DeliveryMethodSelected
    checkoutActor ! PaymentSelected
    checkoutActor ! PaymentReceived
    Thread.sleep(100)
    checkoutActor ! PaymentReceived

    Await.result(system.whenTerminated, Duration.Inf)
  }
}
