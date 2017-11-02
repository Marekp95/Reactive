package actors

import _root_.events.CartManagerEvents.{CheckoutCanceled, CheckoutClosed}
import _root_.events.CheckoutEvents.{DeliveryMethodSelected, PaymentReceived, PaymentSelected}
import _root_.events.CustomerEvents.PaymentServiceStarted
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest._

import scala.concurrent.duration._

class CheckoutSpec extends TestKit(ActorSystem())
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender with Matchers {

  override def afterAll(): Unit = {
    system.terminate
  }

  "Checkout actor" must {
    "close checkout" in {
      val proxy = TestProbe()
      val parent = system.actorOf(Props(new Actor {
        private val checkoutActor = context.actorOf(Props[Checkout])

        def receive = {
          case x if sender == checkoutActor => proxy.ref forward x
          case x => checkoutActor forward x
        }
      }))
      proxy.send(parent, DeliveryMethodSelected)
      proxy.send(parent, PaymentSelected)
      proxy.expectMsgType[PaymentServiceStarted]
      proxy.send(parent, PaymentReceived)
      proxy.expectMsgType[CheckoutClosed]
    }

    "terminate checkout after payment method selection timeout" in {
      val proxy = TestProbe()
      val parent = system.actorOf(Props(new Actor {
        private val checkoutActor = context.actorOf(Props[Checkout])

        def receive = {
          case x if sender == checkoutActor => proxy.ref forward x
          case x => checkoutActor forward x
        }
      }))
      proxy.send(parent, DeliveryMethodSelected)
      proxy.expectMsgType[CheckoutCanceled](15.seconds)
    }

    "terminate checkout after delivery method selection timeout" in {
      val proxy = TestProbe()
      system.actorOf(Props(new Actor {
        private val checkoutActor = context.actorOf(Props[Checkout])

        def receive = {
          case x if sender == checkoutActor => proxy.ref forward x
          case x => checkoutActor forward x
        }
      }))
      proxy.expectMsgType[CheckoutCanceled](15.seconds)
    }
  }
}
