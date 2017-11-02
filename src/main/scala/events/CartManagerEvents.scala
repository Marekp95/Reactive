package events

import actors.CartManager.Item

object CartManagerEvents {

  sealed trait Event

  case class AddItem(item: Item) extends Event

  case class RemoveItem(item: Item) extends Event

  case class CartTimeExpired() extends Event

  case object StartCheckout extends Event

  case class CheckoutCanceled() extends Event

  case class CheckoutClosed() extends Event

  case object CartExpirationKey

}
