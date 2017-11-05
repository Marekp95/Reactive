package actors

import _root_.messages.CartManagerMessages.{CheckoutCanceled, CheckoutClosed}
import _root_.messages.CheckoutMessages.{DeliveryMethodSelected, PaymentReceived, PaymentSelected}
import _root_.messages.CustomerMessages.PaymentServiceStarted
import akka.actor.{Actor, Props}
import akka.testkit.TestProbe

import scala.concurrent.duration._

class CheckoutSpec extends CommonSpec {

  "Checkout actor" must {
    "close checkout" in {
      val proxy = TestProbe()
      val parent = system.actorOf(Props(new Actor {
        private val checkoutActor = context.actorOf(Props(new Checkout(System.currentTimeMillis().toString)))

        override def receive = {
          case x if sender == checkoutActor => proxy.ref forward x
          case x => checkoutActor forward x
        }
      }))
      proxy.send(parent, DeliveryMethodSelected)
      proxy.send(parent, PaymentSelected)
      proxy.expectMsgType[PaymentServiceStarted]
      proxy.send(parent, PaymentReceived)
      proxy.expectMsgType[CheckoutClosed](15.seconds)
    }

    "terminate checkout after payment method selection timeout" in {
      val proxy = TestProbe()
      val parent = system.actorOf(Props(new Actor {
        private val checkoutActor = context.actorOf(Props(new Checkout(System.currentTimeMillis().toString)))

        override def receive = {
          case x if sender == checkoutActor => proxy.ref forward x
          case x => checkoutActor forward x
        }
      }))
      proxy.send(parent, DeliveryMethodSelected)
      proxy.expectMsgType[CheckoutCanceled](15.seconds)
    }

    "terminate checkout after delivery method selection timeout" in {
      val proxy = TestProbe()
      val parent = system.actorOf(Props(new Actor {
        private val checkoutActor = context.actorOf(Props(new Checkout(System.currentTimeMillis().toString)))

        override def receive = {
          case x if sender == checkoutActor => proxy.ref forward x
          case x => checkoutActor forward x
        }
      }))
      proxy.expectMsgType[CheckoutCanceled](15.seconds)
    }
  }
}
