package actors

import akka.actor.{PoisonPill, Timers}
import akka.event.LoggingReceive
import akka.persistence.PersistentActor
import messages.CheckoutMessages.PaymentReceived
import messages.PaymentServiceMessages.{DoPayment, PaymentConfirmed}

class PaymentService extends PersistentActor with Timers {
  override def persistenceId: String = "payment-service-007"

  override def receiveCommand: Receive = LoggingReceive {
    case DoPayment =>
      sender ! PaymentConfirmed
      context.parent ! PaymentReceived
      self ! PoisonPill
  }

  override def receiveRecover: Receive = {
    case _ =>
  }
}

object PaymentService {
  def apply: PaymentService = new PaymentService()
}
