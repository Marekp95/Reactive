package actors

import akka.actor.{Actor, PoisonPill, Timers}
import akka.event.LoggingReceive
import events.CheckoutEvents.PaymentReceived
import events.PaymentServiceEvents.{DoPayment, PaymentConfirmed}

class PaymentService extends Actor with Timers {
  override def receive: Receive = LoggingReceive {
    case DoPayment =>
      sender ! PaymentConfirmed
      context.parent ! PaymentReceived
      self ! PoisonPill
  }
}

object PaymentService {
  def apply: PaymentService = new PaymentService()
}
