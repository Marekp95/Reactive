package messages

object CheckoutMessages {

  sealed trait Message

  case class DeliveryMethodSelected() extends Message

  case object Cancelled extends Message

  case class CheckoutTimeExpired() extends Message

  case class PaymentTimeExpired() extends Message

  case class PaymentSelected() extends Message

  case class PaymentReceived() extends Message

  sealed trait Key

  case class CheckoutTimerExpirationKey() extends Key

  case class PaymentTimerExpirationKey() extends Key

}
