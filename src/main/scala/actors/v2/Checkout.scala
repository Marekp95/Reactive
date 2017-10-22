package actors.v2

import actors.v2.Checkout.{ProcessingPayment, SelectingDelivery, SelectingPaymentMethod, State}
import akka.actor.{FSM, PoisonPill, Timers}
import events.CartEvents
import events.CheckoutEvents._

import scala.collection.mutable
import scala.concurrent.duration._

class Checkout[T] extends FSM[State, mutable.HashSet[T]] with Timers {

  startWith(SelectingDelivery, {
    restartCheckoutTimer()
    mutable.HashSet[T]()
  })

  when(SelectingDelivery) {
    case Event(DeliveryMethodSelected, items) =>
      goto(SelectingPaymentMethod) using items
    case Event(CheckoutTimeExpired, _) =>
      context.parent ! CartEvents.CheckoutClosed
      self ! PoisonPill
      stay
    case Event(Cancelled, _) =>
      context.parent ! CartEvents.CheckoutClosed
      self ! PoisonPill
      stay
  }

  when(SelectingPaymentMethod) {
    case Event(CheckoutTimeExpired, _) =>
      context.parent ! CartEvents.CheckoutClosed
      self ! PoisonPill
      stay
    case Event(Cancelled, _) =>
      context.parent ! CartEvents.CheckoutClosed
      self ! PoisonPill
      stay
    case Event(PaymentSelected, items) =>
      goto(ProcessingPayment) using items
  }

  when(ProcessingPayment) {
    case Event(PaymentTimeExpired, _) =>
      context.parent ! CartEvents.CheckoutClosed
      self ! PoisonPill
      stay
    case Event(Cancelled, _) =>
      context.parent ! CartEvents.CheckoutClosed
      self ! PoisonPill
      stay
    case Event(PaymentReceived, _) =>
      context.parent ! CartEvents.CheckoutClosed
      self ! PoisonPill
      stay
  }

  def restartCheckoutTimer() {
    timers.cancelAll()
    timers.startSingleTimer(CheckoutTimerExpirationKey, CheckoutTimeExpired, 1.second)
  }

  def restartPaymentTimer() {
    timers.cancelAll()
    timers.startSingleTimer(PaymentTimerExpirationKey, PaymentTimeExpired, 1.second)
  }

  initialize()
}

object Checkout {
  def apply: Cart[Int] = new Cart()

  sealed trait State

  case object SelectingDelivery extends State

  case object SelectingPaymentMethod extends State

  case object ProcessingPayment extends State

}