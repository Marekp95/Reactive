import actors.{CartManager, Checkout}
import akka.actor.{ActorSystem, Props}
import events.CartManagerEvents.{AddItem, RemoveItem, StartCheckout}
import events.CheckoutEvents.{DeliveryMethodSelected, PaymentReceived, PaymentSelected}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main {

  def main(args: Array[String]): Unit = {
    // test function
    val system = ActorSystem()

    Await.result(system.whenTerminated, Duration.Inf)
  }
}
