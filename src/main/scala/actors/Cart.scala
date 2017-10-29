package actors

import akka.actor.{Actor, Props, Timers}
import akka.event.LoggingReceive
import events.CartEvents._
import events.CustomerEvents.{CartEmpty, CheckOutStarted}

import scala.collection.mutable
import scala.concurrent.duration._

class Cart[T] extends Actor with Timers {
  val items: mutable.HashSet[T] = mutable.HashSet[T]()

  override def receive: Receive = empty()

  def empty(): Receive = LoggingReceive {
    case AddItem(item: T) =>
      items.+=(item)
      context become nonEmpty()
      restartTimer()
  }

  def nonEmpty(): Receive = LoggingReceive {
    case AddItem(item: T) =>
      restartTimer()
      items.+=(item)
    case RemoveItem(item: T) if !items.contains(item) =>
    // log error or sth
    case RemoveItem(item: T) if items.size > 1 =>
      restartTimer()
      items.-=(item)
    case RemoveItem(item: T) =>
      items.-=(item)
      context.parent ! CartEmpty()
      context become empty()
    case CartTimeExpired =>
      items.clear()
      context.parent ! CartEmpty()
      context become empty()
    case StartCheckout =>
      val checkoutActor = context.actorOf(Props[Checkout[T]])
      sender ! CheckOutStarted(checkoutActor)
      context become inCheckout()
  }

  def inCheckout(): Receive = LoggingReceive {
    case CheckoutClosed =>
      items.clear()
      context.parent ! CartEmpty()
      context become empty()
    case CheckoutCanceled =>
      context.parent ! CartEmpty()
      context become nonEmpty()
      restartTimer()
  }

  def restartTimer() {
    timers.cancelAll()
    timers.startSingleTimer(CartExpirationKey, CartTimeExpired, 1.seconds)
  }
}

object Cart {
  def apply: Cart[Int] = new Cart()
}
