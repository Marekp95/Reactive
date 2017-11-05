package actors

import java.net.URI

import actors.CartManager.Item
import akka.actor.{Actor, Props, Timers}
import akka.event.LoggingReceive
import messages.CartManagerMessages.{AddItem, CheckoutClosed, RemoveItem, StartCheckout}
import messages.CheckoutMessages.{DeliveryMethodSelected, PaymentSelected}
import messages.CustomerMessages._
import messages.PaymentServiceMessages.{DoPayment, PaymentConfirmed}

class Customer extends Actor with Timers {
  override def receive: Receive = LoggingReceive {
    case Start =>
      val cartManager = context.actorOf(Props[CartManager])
      cartManager ! AddItem(Item(new URI("7"), "7", BigDecimal(1.0), 1))
      cartManager ! AddItem(Item(new URI("11"), "11", BigDecimal(1.0), 1))
      cartManager ! AddItem(Item(new URI("13"), "13", BigDecimal(1.0), 1))
      cartManager ! RemoveItem(Item(new URI("11"), "11", BigDecimal(1.0), 1))
      cartManager ! StartCheckout
    case CartEmpty =>
    case CheckOutStarted(checkoutActor) =>
      checkoutActor ! DeliveryMethodSelected
//      System.exit(0)
      checkoutActor ! PaymentSelected
    case CheckoutClosed =>
    case PaymentServiceStarted(paymentService) =>
      paymentService ! DoPayment
    case PaymentConfirmed =>
    case Continue =>
      val cartManager = context.actorOf(Props[CartManager])
      val checkoutActor = context.actorOf(Props[Checkout])
      checkoutActor ! PaymentSelected
  }
}

object Customer {
  def apply: Customer = new Customer()
}
