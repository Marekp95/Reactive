package actors

import akka.actor.{Actor, Timers}
import akka.event.LoggingReceive
import events.CartEvents.CheckoutClosed
import events.CheckoutEvents.{DeliveryMethodSelected, PaymentSelected}
import events.CustomerEvents.{CartEmpty, CheckOutStarted, PaymentServiceStarted}
import events.PaymentServiceEvents.{DoPayment, PaymentConfirmed}

class Customer extends Actor with Timers {
  override def receive: Receive = LoggingReceive {
    case CartEmpty => {
    }
    case CheckOutStarted(checkoutActor) =>
      checkoutActor ! DeliveryMethodSelected
      checkoutActor ! PaymentSelected
    case CheckoutClosed => {
    }
    case PaymentServiceStarted(paymentService) =>
      paymentService ! DoPayment
    case PaymentConfirmed => {
    }
  }
}

object Customer {
  def apply: Customer = new Customer()
}
