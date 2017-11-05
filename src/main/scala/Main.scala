import actors.Customer
import akka.actor.{ActorSystem, Props}
import messages.CustomerMessages.{Continue, Start}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main {

  def main(args: Array[String]): Unit = {
    // test function
    val system = ActorSystem()
    val customer = system.actorOf(Props[Customer])
        customer ! Start
//    customer ! Continue
    Await.result(system.whenTerminated, Duration.Inf)
  }
}
