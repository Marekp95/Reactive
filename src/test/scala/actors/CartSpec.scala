package actors

import _root_.events.CartEvents.{AddItem, RemoveItem, StartCheckout}
import _root_.events.CustomerEvents.{CartEmpty, CheckOutStarted}
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.scalatest._

import scala.concurrent.duration._

class CartSpec extends TestKit(ActorSystem())
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender with Matchers {

  override def afterAll(): Unit = {
    system.terminate
  }

  "Cart actor" must {
    "initial state" in {
      val cartActor = TestActorRef[Cart[Int]]
      assert(cartActor.underlyingActor.items.isEmpty)
    }

    "add Item" in {
      val cartActor = TestActorRef[Cart[Int]]
      cartActor ! AddItem(7)
      assert(cartActor.underlyingActor.items.size == 1)
    }

    "remove not present item" in {
      val cartActor = TestActorRef[Cart[Int]]
      cartActor ! AddItem(7)
      assert(cartActor.underlyingActor.items.size == 1)
      cartActor ! RemoveItem(13)
      assert(cartActor.underlyingActor.items.size == 1)
    }

    "remove item" in {
      val cartActor = TestActorRef[Cart[Int]]
      cartActor ! AddItem(7)
      assert(cartActor.underlyingActor.items.size == 1)
      cartActor ! RemoveItem(7)
      assert(cartActor.underlyingActor.items.isEmpty)
    }

    "checkout started response" in {
      val cartActor = system.actorOf(Props[Cart[Int]])
      cartActor ! AddItem(7)
      expectNoMessage(1.second)
      cartActor ! StartCheckout
      expectMsgType[CheckOutStarted]
    }

    "cart expired" in {
      val proxy = TestProbe()
      val parent = system.actorOf(Props(new Actor {
        private val cartActor = context.actorOf(Props[Cart[Int]])

        def receive = {
          case x if sender == cartActor => proxy.ref forward x
          case x => cartActor forward x
        }
      }))
      proxy.send(parent, AddItem(7))
      proxy.expectMsgType[CartEmpty](15.seconds)
    }

    "cart empty" in {
      val proxy = TestProbe()
      val parent = system.actorOf(Props(new Actor {
        private val cartActor = context.actorOf(Props[Cart[Int]])

        def receive = {
          case x if sender == cartActor => proxy.ref forward x
          case x => cartActor forward x
        }
      }))
      proxy.send(parent, AddItem(7))
      proxy.send(parent, RemoveItem(7))
      proxy.expectMsgType[CartEmpty]
    }
  }
}
