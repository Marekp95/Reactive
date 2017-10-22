package events

object CheckoutEvents {

  sealed trait Event

  case class DeliveryMethodSelected() extends Event

  case object Cancelled extends Event

  case object CheckoutTimeExpired extends Event

  case object PaymentTimeExpired extends Event

  case class PaymentSelected() extends Event

  case class PaymentReceived() extends Event

  case object CheckoutTimerExpirationKey

  case object PaymentTimerExpirationKey

}
