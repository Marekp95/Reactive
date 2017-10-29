package events

object CartEvents {

  sealed trait Event

  case class AddItem[T](item: T) extends Event

  case class RemoveItem[T](item: T) extends Event

  case class CartTimeExpired() extends Event

  case object StartCheckout extends Event

  case class CheckoutCanceled() extends Event

  case class CheckoutClosed() extends Event

  case object CartExpirationKey

}
