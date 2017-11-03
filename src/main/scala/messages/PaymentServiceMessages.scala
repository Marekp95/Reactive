package messages

object PaymentServiceMessages {

  sealed trait Message

  case class DoPayment() extends Message

  case object PaymentConfirmed extends Message

  case object PaymentReceived extends Message

}
