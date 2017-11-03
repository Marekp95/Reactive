package actors

import akka.actor.{Actor, Timers}
import akka.event.LoggingReceive
import messages.CartManagerMessages.CheckoutClosed
import messages.CheckoutMessages.{DeliveryMethodSelected, PaymentSelected}
import messages.CustomerMessages.{CartEmpty, CheckOutStarted, PaymentServiceStarted}
import messages.PaymentServiceMessages.{DoPayment, PaymentConfirmed}

class Customer extends Actor with Timers {
  override def receive: Receive = LoggingReceive {
    case CartEmpty =>
    case CheckOutStarted(checkoutActor) =>
      checkoutActor ! DeliveryMethodSelected
      checkoutActor ! PaymentSelected
    case CheckoutClosed =>
    case PaymentServiceStarted(paymentService) =>
      paymentService ! DoPayment
    case PaymentConfirmed =>
  }
}

object Customer {
  def apply: Customer = new Customer()
}
