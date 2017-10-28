package events

object PaymentServiceEvents {

  sealed trait Event

  case class DoPayment() extends Event

  case object PaymentConfirmed extends Event

  case object PaymentReceived extends Event

}
