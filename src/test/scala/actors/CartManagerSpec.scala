package actors

import java.net.URI

import actors.CartManager.{Cart, Item}
import akka.actor.{Actor, PoisonPill, Props}
import akka.testkit.{TestActorRef, TestProbe}
import messages.CartManagerMessages.{AddItem, RemoveItem, StartCheckout}
import messages.CustomerMessages.{CartEmpty, CheckOutStarted}

import scala.concurrent.duration._

class CartManagerSpec extends CommonSpec {

  "Cart actor" must {
    "initial state" in {
      val cartActor = TestActorRef[CartManager]
      assert(cartActor.underlyingActor.shoppingCart.getItems.isEmpty)
      cartActor ! PoisonPill
    }

    "add Item" in {
      var cart = Cart.empty
      cart = cart.addItem(Item(new URI("7"), "7", "", 1, BigDecimal(1.0)))
      assert(cart.getItems.size == 1)
    }

    "remove not present item" in {
      var cart = Cart.empty
      cart = cart.addItem(Item(new URI("7"), "7", "", 1, BigDecimal(1.0)))
      assert(cart.getItems.size == 1)
      cart = cart.removeItem(Item(new URI("13"), "13", "", 1, BigDecimal(1.0)), 1)
      assert(cart.getItems.size == 1)
    }

    "remove item" in {
      var cart = Cart.empty
      cart = cart.addItem(Item(new URI("7"), "7", "", 1, BigDecimal(1.0)))
      assert(cart.getItems.size == 1)
      cart = cart.removeItem(Item(new URI("7"), "7", "", 1, BigDecimal(1.0)), 1)
      assert(cart.getItems.isEmpty)
    }

    "checkout started response" in {
      val cartActor = system.actorOf(Props[CartManager])
      cartActor ! AddItem(Item(new URI("7"), "7", "", 1, BigDecimal(1.0)))
      expectNoMessage(1.second)
      cartActor ! StartCheckout
      expectMsgType[CheckOutStarted](15.seconds)
      cartActor ! PoisonPill
    }

    "cart expired" in {
      val proxy = TestProbe()
      val parent = system.actorOf(Props(new Actor {
        private val cartActor = context.actorOf(Props(new CartManager(System.currentTimeMillis().toString)))

        override def receive = {
          case x if sender == cartActor => proxy.ref forward x
          case x => cartActor forward x
        }
      }))
      proxy.send(parent, AddItem(Item(new URI("7"), "7", "", 1, BigDecimal(1.0))))
      proxy.expectMsgType[CartEmpty](15.seconds)
    }

    "cart empty" in {
      val proxy = TestProbe()
      val parent = system.actorOf(Props(new Actor {
        private val cartActor = context.actorOf(Props(new CartManager(System.currentTimeMillis().toString)))

        override def receive = {
          case x if sender == cartActor => proxy.ref forward x
          case x => cartActor forward x
        }
      }))
      proxy.send(parent, AddItem(Item(new URI("7"), "7", "", 1, BigDecimal(1.0))))
      proxy.send(parent, RemoveItem(Item(new URI("7"), "7", "", 1, BigDecimal(1.0))))
      proxy.expectMsgType[CartEmpty](15.seconds)
    }
  }
}
