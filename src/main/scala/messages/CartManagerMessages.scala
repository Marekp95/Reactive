package messages

import actors.CartManager.Item

object CartManagerMessages {

  sealed trait Message

  case class AddItem(item: Item) extends Message

  case class RemoveItem(item: Item) extends Message

  case class CartTimeExpired() extends Message

  case object StartCheckout extends Message

  case class CheckoutCanceled() extends Message

  case class CheckoutClosed() extends Message

  case object CartExpirationKey

}
