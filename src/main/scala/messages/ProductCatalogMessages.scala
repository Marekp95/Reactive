package messages

import actors.CartManager.Item

object ProductCatalogMessages {

  case class SearchQuery(parameters: List[String])

  case class SearchQueryResponse(items: List[Item])

}
