package rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation._

import scala.collection.JavaConverters._

@RestController
@RequestMapping(Array("products"))
class ProductsController @Autowired()(productsService: ProductsServiceImpl) {

  @PostMapping(consumes = Array("application/json"))
  @ResponseBody
  def findProducts(@RequestBody parameters: java.util.List[String]): java.util.List[ItemResponse] = {
    productsService.getProducts(parameters.asScala.toList)
      .map(x => new ItemResponse(x))
      .asJava
  }
}