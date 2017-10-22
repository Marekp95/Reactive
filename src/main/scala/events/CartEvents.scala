package events

object CartEvents {

  sealed trait Event

  case class ItemAdded[T](item: T) extends Event

  case class ItemRemoved[T](item: T) extends Event

  case class CartTimeExpired() extends Event

  case object StartCheckout extends Event

  case object CheckoutCanceled extends Event

  case object CheckoutClosed extends Event

  case object CartExpirationKey

}
