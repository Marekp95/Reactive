package rest

import java.util

import org.springframework.beans.factory.annotation.Autowired
import scala.collection.JavaConverters._
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("products"))
class ProductsController @Autowired()(productsService: ProductsServiceImpl) {

  @PostMapping(consumes = Array("application/json"))
  @ResponseBody
  def findProducts(@RequestBody parameters: java.util.List[String]): java.util.List[ItemResponse] = {
    val y = new util.ArrayList[ItemResponse]()
    productsService.getProducts(parameters.asScala.toList)
      .map(x => new ItemResponse(x))
      .foreach(i => y.add(i))
    y
  }
}