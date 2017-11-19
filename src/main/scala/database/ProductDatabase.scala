package database

import java.net.URI

import actors.CartManager.Item
import com.google.common.collect.{LinkedHashMultimap, Multimap}

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.Random

class ProductDatabase {

  val cache: Multimap[String, Item] = LinkedHashMultimap.create()

  {
    val filename = "query_result"
    for (line <- Source.fromResource(filename).getLines) {
      val data = line.split("\",\"")
      val id = data(0).replace("\"", "")
      val name = data(1).replace("\"", "")
      val brand = data(2).replace("\"", "")
      val item = Item(new URI(id), name, brand, Random.nextInt(1000), BigDecimal(Random.nextInt(10000)))

      name.split(" ").toStream.filter(s => s.nonEmpty).foreach(s => cache.put(s.toUpperCase, item))
    }
  }

  def processRequest(parameters: List[String]): List[Item] = {
    parameters.toStream
      .map(p => p.toUpperCase)
      .flatMap(p => cache.get(p).asScala)
      .groupBy(identity)
      .mapValues(_.size)
      .toStream
      .sortWith(_._2 > _._2)
      .slice(0, 10)
      .map(_._1)
      .toList
  }
}

object ProductDatabase {
  def apply: ProductDatabase = new ProductDatabase()

  def main(args: Array[String]): Unit = {
    val productDatabase = new ProductDatabase()

    println(productDatabase.processRequest(List("Ale", "Bigfoot")))
  }
}