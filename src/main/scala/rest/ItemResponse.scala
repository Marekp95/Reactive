package rest

import java.net.URI

import actors.CartManager.Item

class ItemResponse(item: Item) extends Serializable {
  def getBrand: String = {
    item.brand
  }

  def getName: String = {
    item.name
  }

  def getCount: Int = {
    item.count
  }

  def getPrice: BigDecimal = {
    item.price
  }

  def getId: URI = {
    item.id
  }
}
