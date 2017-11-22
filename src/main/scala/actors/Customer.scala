package actors

import java.net.URI

import actors.CartManager.Item
import actors.PaymentServer.PayPal
import akka.actor.{Actor, Props, Timers}
import akka.event.LoggingReceive
import messages.CartManagerMessages.{AddItem, CheckoutClosed, RemoveItem, StartCheckout}
import messages.CheckoutMessages.{DeliveryMethodSelected, PaymentSelected}
import messages.CustomerMessages._
import messages.PaymentServiceMessages.{DoPayment, PaymentConfirmed}
import messages.ProductCatalogMessages.{SearchQuery, SearchQueryResponse}

class Customer extends Actor with Timers {
  override def receive: Receive = LoggingReceive {
    case Start =>
      val cartManager = context.actorOf(Props[CartManager])
      cartManager ! AddItem(Item(new URI("7"), "7", "", 1, BigDecimal(1.0)))
      cartManager ! AddItem(Item(new URI("11"), "11", "", 1, BigDecimal(1.0)))
      cartManager ! AddItem(Item(new URI("13"), "13", "", 1, BigDecimal(1.0)))
      cartManager ! RemoveItem(Item(new URI("11"), "11", "", 1, BigDecimal(1.0)))
      cartManager ! StartCheckout
    case CartEmpty() =>
    case CheckOutStarted(checkoutActor) =>
      checkoutActor ! DeliveryMethodSelected
      //      System.exit(0)
      checkoutActor ! PaymentSelected
    case CheckoutClosed() =>
    case PaymentServiceStarted(paymentService) =>
      paymentService ! DoPayment(PayPal)
    case PaymentConfirmed =>
    case Continue =>
      val cartManager = context.actorOf(Props[CartManager])
      val checkoutActor = context.actorOf(Props[Checkout])
      checkoutActor ! PaymentSelected
    case SearchQuery(parameters) =>
      val productCatalog = context.actorSelection("akka.tcp://SSystem@127.0.0.1:2552/user/catalog")
      productCatalog ! SearchQuery(parameters)
    case SearchQueryResponse(response) =>
      val cartManager = context.actorOf(Props[CartManager])
      cartManager ! AddItem(response.head)
  }
}

object Customer {
  def apply: Customer = new Customer()
}
