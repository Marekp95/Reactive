package actors

import java.net.URI

import _root_.events.CartManagerEvents.{AddItem, RemoveItem, StartCheckout}
import _root_.events.CustomerEvents.{CartEmpty, CheckOutStarted}
import actors.CartManager.Item
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.scalatest._

import scala.concurrent.duration._

class CartManagerSpec extends TestKit(ActorSystem())
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender with Matchers {

  override def afterAll(): Unit = {
    system.terminate
  }

  "Cart actor" must {
    "initial state" in {
      val cartActor = TestActorRef[CartManager]
      assert(cartActor.underlyingActor.shoppingCart.getItems.isEmpty)
    }

    "add Item" in {
      val cartActor = TestActorRef[CartManager]
      cartActor ! AddItem(Item(new URI("7"), "7", BigDecimal(1.0), 1))
      assert(cartActor.underlyingActor.shoppingCart.getItems.size == 1)
    }

    "remove not present item" in {
      val cartActor = TestActorRef[CartManager]
      cartActor ! AddItem(Item(new URI("7"), "7", BigDecimal(1.0), 1))
      assert(cartActor.underlyingActor.shoppingCart.getItems.size == 1)
      cartActor ! RemoveItem(Item(new URI("13"), "13", BigDecimal(1.0), 1))
      assert(cartActor.underlyingActor.shoppingCart.getItems.size == 1)
    }

    "remove item" in {
      val cartActor = TestActorRef[CartManager]
      cartActor ! AddItem(Item(new URI("7"), "7", BigDecimal(1.0), 1))
      assert(cartActor.underlyingActor.shoppingCart.getItems.size == 1)
      cartActor ! RemoveItem(Item(new URI("7"), "7", BigDecimal(1.0), 1))
      assert(cartActor.underlyingActor.shoppingCart.getItems.isEmpty)
    }

    "checkout started response" in {
      val cartActor = system.actorOf(Props[CartManager])
      cartActor ! AddItem(Item(new URI("7"), "7", BigDecimal(1.0), 1))
      expectNoMessage(1.second)
      cartActor ! StartCheckout
      expectMsgType[CheckOutStarted]
    }

    "cart expired" in {
      val proxy = TestProbe()
      val parent = system.actorOf(Props(new Actor {
        private val cartActor = context.actorOf(Props[CartManager])

        def receive = {
          case x if sender == cartActor => proxy.ref forward x
          case x => cartActor forward x
        }
      }))
      proxy.send(parent, AddItem(Item(new URI("7"), "7", BigDecimal(1.0), 1)))
      proxy.expectMsgType[CartEmpty](15.seconds)
    }

    "cart empty" in {
      val proxy = TestProbe()
      val parent = system.actorOf(Props(new Actor {
        private val cartActor = context.actorOf(Props[CartManager])

        def receive = {
          case x if sender == cartActor => proxy.ref forward x
          case x => cartActor forward x
        }
      }))
      proxy.send(parent, AddItem(Item(new URI("7"), "7", BigDecimal(1.0), 1)))
      proxy.send(parent, RemoveItem(Item(new URI("7"), "7", BigDecimal(1.0), 1)))
      proxy.expectMsgType[CartEmpty]
    }
  }
}
