package actors

import actors.Checkout._
import akka.actor.{PoisonPill, Props, Timers}
import akka.event.LoggingReceive
import akka.persistence.PersistentActor
import messages.CartManagerMessages
import messages.CheckoutMessages._
import messages.CustomerMessages.PaymentServiceStarted

import scala.concurrent.duration._

class Checkout(id: String) extends PersistentActor with Timers {
  def this() = this(System.currentTimeMillis().toString)

  override def persistenceId: String = "checkout-" + id

  override def receiveCommand: Receive = {
    timers.startSingleTimer(CheckoutTimerExpirationKey, CheckoutTimeExpired, 10.seconds)
    selectingDelivery()
  }

  def selectingDelivery(): Receive = LoggingReceive {
    case DeliveryMethodSelected =>
      context become selectingPaymentMethod()
      restartCheckoutTimer()
    case CheckoutTimeExpired | Cancelled =>
      context.parent ! CartManagerMessages.CheckoutCanceled()
      self ! PoisonPill
  }

  def selectingPaymentMethod(): Receive = LoggingReceive {
    case CheckoutTimeExpired | Cancelled =>
      context.parent ! CartManagerMessages.CheckoutCanceled()
      self ! PoisonPill
    case PaymentSelected =>
      val paymentService = context.actorOf(Props[PaymentService])
      sender ! PaymentServiceStarted(paymentService)
      context become processingPayment()
      restartPaymentTimer()
  }

  def processingPayment(): Receive = LoggingReceive {
    case PaymentTimeExpired | Cancelled =>
      context.parent ! CartManagerMessages.CheckoutCanceled()
      self ! PoisonPill
    case PaymentReceived =>
      context.parent ! CartManagerMessages.CheckoutClosed()
      self ! PoisonPill
  }

  def restartCheckoutTimer() {
    persist(SetTimerEvent(CheckoutTimerExpirationKey(), CheckoutTimeExpired())) { _ =>
      timers.startSingleTimer(CheckoutTimerExpirationKey(), CheckoutTimeExpired(), 10.seconds)
    }
  }

  def restartPaymentTimer() {
    persist(SetTimerEvent(PaymentTimerExpirationKey(), PaymentTimeExpired())) { _ =>
      timers.startSingleTimer(PaymentTimerExpirationKey(), PaymentTimeExpired(), 10.seconds)
    }
  }

  override def receiveRecover: Receive = {
    case CheckoutChangeEvent(state) => setState(state)
    case SetTimerEvent(key, message) => timers.startSingleTimer(key, message, 10.seconds)
  }

  def setState(state: State): Unit = state match {
    case SelectingDelivery => context become selectingDelivery()
    case SelectingPaymentMethod => context become selectingPaymentMethod()
    case ProcessingPayment => context become processingPayment()
  }
}

object Checkout {
  def apply: Checkout = new Checkout(System.currentTimeMillis().toString)

  sealed trait State

  case object SelectingDelivery extends State

  case object SelectingPaymentMethod extends State

  case object ProcessingPayment extends State

  case class CheckoutChangeEvent(newState: State)

  case class SetTimerEvent(key: Key, message: Message)

}