package actors.v1

import akka.actor.{Actor, Props, Timers}
import akka.event.LoggingReceive
import events.CartEvents._

import scala.collection.mutable
import scala.concurrent.duration._

class Cart[T] extends Actor with Timers {
  private val items = mutable.HashSet[T]()

  override def receive: Receive = empty()

  def empty(): Receive = LoggingReceive {
    case ItemAdded(item: T) =>
      items.+=(item)
      context become nonEmpty()
      restartTimer()
  }

  def nonEmpty(): Receive = LoggingReceive {
    case ItemAdded(item: T) =>
      restartTimer()
      items.+=(item)
    case ItemRemoved(item: T) if !items.contains(item) =>
      // log error or sth
    case ItemRemoved(item: T) if items.size > 1 =>
      restartTimer()
      items.-=(item)
    case ItemRemoved(item: T) =>
      items.-=(item)
      context become empty()
    case CartTimeExpired =>
      items.clear()
      context become empty()
    case StartCheckout =>
      context.actorOf(Props[Checkout[T]])
      context become inCheckout()
  }

  def inCheckout(): Receive = LoggingReceive {
    case CheckoutClosed =>
      items.clear()
      context become empty()
    case CheckoutCanceled =>
      context become nonEmpty()
      restartTimer()
  }

  def restartTimer() {
    timers.cancelAll()
    timers.startSingleTimer(CartExpirationKey, CartTimeExpired, 100.millis)
  }
}

object Cart {
  def apply: Cart[Int] = new Cart()
}
