package actors

import akka.actor.{Actor, PoisonPill, Props, Timers}
import akka.event.LoggingReceive
import events.CartEvents
import events.CheckoutEvents._
import events.CustomerEvents.PaymentServiceStarted

import scala.concurrent.duration._

class Checkout[T] extends Actor with Timers {

  override def receive: Receive = {
    restartCheckoutTimer()
    selectingDelivery()
  }

  def selectingDelivery(): Receive = LoggingReceive {
    case DeliveryMethodSelected =>
      context become selectingPaymentMethod()
      restartCheckoutTimer()
    case CheckoutTimeExpired | Cancelled =>
      context.parent ! CartEvents.CheckoutCanceled()
      self ! PoisonPill
  }

  def selectingPaymentMethod(): Receive = LoggingReceive {
    case CheckoutTimeExpired | Cancelled =>
      context.parent ! CartEvents.CheckoutCanceled()
      self ! PoisonPill
    case PaymentSelected =>
      val paymentService = context.actorOf(Props[PaymentService])
      sender ! PaymentServiceStarted(paymentService)
      context become processingPayment()
      restartPaymentTimer()
  }

  def processingPayment(): Receive = LoggingReceive {
    case PaymentTimeExpired | Cancelled =>
      context.parent ! CartEvents.CheckoutCanceled()
      self ! PoisonPill
    case PaymentReceived =>
      context.parent ! CartEvents.CheckoutClosed()
      self ! PoisonPill
  }

  def restartCheckoutTimer() {
    timers.cancelAll()
    timers.startSingleTimer(CheckoutTimerExpirationKey, CheckoutTimeExpired, 1.second)
  }

  def restartPaymentTimer() {
    timers.cancelAll()
    timers.startSingleTimer(PaymentTimerExpirationKey, PaymentTimeExpired, 1.second)
  }
}

object Checkout {
  def apply: Cart[Int] = new Cart()
}