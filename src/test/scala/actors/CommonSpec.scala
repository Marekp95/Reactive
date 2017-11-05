package actors

import java.io.File

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.iq80.leveldb.util.FileUtils
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpecLike}

class CommonSpec extends TestKit(ActorSystem()) with WordSpecLike with BeforeAndAfterAll
  with ImplicitSender with Matchers with BeforeAndAfterEach {

  val storageLocations = List(
    new File(system.settings.config.getString("akka.persistence.journal.leveldb.dir")),
    new File(system.settings.config.getString("akka.persistence.snapshot-store.local.dir"))
  )

  override def beforeAll() {
    super.beforeAll()
    storageLocations foreach FileUtils.deleteRecursively
  }

  override def afterAll() {
    super.afterAll()
    system.terminate()
    storageLocations foreach FileUtils.deleteRecursively
  }
}
