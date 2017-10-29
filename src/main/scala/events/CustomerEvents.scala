package events

import akka.actor.ActorRef

object CustomerEvents {

  sealed trait Event

  case class PaymentServiceStarted(paymentServiceActor: ActorRef) extends Event

  case class CheckOutStarted(checkoutActor: ActorRef) extends Event

  case class CartEmpty() extends Event

}
