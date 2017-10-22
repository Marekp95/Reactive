package actors.v2

import actors.v2.Cart.{Empty, InCheckout, NonEmpty, State}
import akka.actor.{FSM, Props, Timers}
import events.CartEvents._

import scala.collection.mutable
import scala.concurrent.duration._

class Cart[T] extends FSM[State, mutable.HashSet[T]] with Timers {

  startWith(Empty, new mutable.HashSet[T]())

  when(Empty) {
    case Event(ItemAdded(item: T), items) =>
      restartTimer()
      goto(NonEmpty) using items.+=(item)
  }

  when(NonEmpty) {
    case Event(ItemAdded(item: T), items) =>
      restartTimer()
      stay using items.+=(item)
    case Event(ItemRemoved(item: T), items) if !items.contains(item) =>
      // log error or sth
      stay
    case Event(ItemRemoved(item: T), items) if items.size > 1 =>
      restartTimer()
      stay using items.-=(item)
    case Event(ItemRemoved(item: T), items) =>
      goto(Empty) using items.-=(item)
    case Event(CartTimeExpired, items) =>
      items.clear()
      goto(Empty) using items
    case Event(StartCheckout, items) =>
      context.actorOf(Props[Checkout[T]])
      goto(InCheckout) using items
  }

  when(InCheckout) {
    case Event(CheckoutClosed, items) =>
      items.clear()
      goto(Empty) using items
    case Event(CheckoutCanceled, items) =>
      restartTimer()
      goto(NonEmpty) using items
  }

  def restartTimer() {
    timers.cancelAll()
    timers.startSingleTimer(CartExpirationKey, CartTimeExpired, 1.second)
  }

  initialize()
}

object Cart {
  def apply: Cart[Int] = new Cart()

  sealed trait State

  case object Empty extends State

  case object NonEmpty extends State

  case object InCheckout extends State

}
