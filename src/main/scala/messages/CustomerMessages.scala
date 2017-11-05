package messages

import akka.actor.ActorRef

object CustomerMessages {

  sealed trait Message

  case class PaymentServiceStarted(paymentServiceActor: ActorRef) extends Message

  case class CheckOutStarted(checkoutActor: ActorRef) extends Message

  case class CartEmpty() extends Message

  case object Start extends Message

  case object Continue extends Message

}
