package actors

import java.net.URI

import actors.CartManager.{Cart, Item}
import akka.actor.{Actor, Props, Timers}
import akka.event.LoggingReceive
import events.CartManagerEvents._
import events.CustomerEvents.{CartEmpty, CheckOutStarted}

import scala.collection.immutable.HashMap
import scala.concurrent.duration._

class CartManager(var shoppingCart: Cart) extends Actor with Timers {
  def this() = this(Cart.empty)

  override def receive: Receive = empty()

  def empty(): Receive = LoggingReceive {
    case AddItem(item: Item) =>
      shoppingCart = shoppingCart.addItem(item)
      context become nonEmpty()
      restartTimer()
  }

  def nonEmpty(): Receive = LoggingReceive {
    case AddItem(item: Item) =>
      restartTimer()
      shoppingCart = shoppingCart.addItem(item)
      println(shoppingCart)
    case RemoveItem(item: Item) if item.count > 1 =>
      restartTimer()
      shoppingCart = shoppingCart.removeItem(item, 1)
    case RemoveItem(item: Item) =>
      shoppingCart = shoppingCart.removeItem(item, 1)
      context.parent ! CartEmpty()
      context become empty()
    case CartTimeExpired =>
      shoppingCart = shoppingCart.removeAllItems()
      context.parent ! CartEmpty()
      context become empty()
    case StartCheckout =>
      val checkoutActor = context.actorOf(Props[Checkout])
      sender ! CheckOutStarted(checkoutActor)
      context become inCheckout()
  }

  def inCheckout(): Receive = LoggingReceive {
    case CheckoutClosed =>
      shoppingCart = shoppingCart.removeAllItems()
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

object CartManager {
  def apply: CartManager = new CartManager()

  case class Item(id: URI, name: String, price: BigDecimal, count: Int)

  case class Cart(items: Map[URI, Item]) {
    def addItem(it: Item): Cart = {
      val currentCount = if (items contains it.id) items(it.id).count else 0
      copy(items = items.updated(it.id, it.copy(count = currentCount + it.count)))
    }

    def removeItem(it: Item, cnt: Int): Cart = {
      val currentCount = if (items contains it.id) items(it.id).count else 0
      copy(items = items.updated(it.id, it.copy(count = currentCount - cnt)))
    }

    def removeAllItems(): Cart = copy(items = HashMap())

    def getItems: List[Item] = items.values.filter(it => it.count > 0).toList
  }

  object Cart {
    val empty = Cart(Map.empty)
  }
}
