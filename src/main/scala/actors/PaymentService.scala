package actors

import actors.PaymentServer._
import akka.actor.SupervisorStrategy.{Escalate, Restart, Stop}
import akka.actor.{Actor, OneForOneStrategy, PoisonPill, Props, Timers}
import akka.event.LoggingReceive
import messages.CheckoutMessages.PaymentReceived
import messages.PaymentServiceMessages.{DoPayment, InvalidPayment, PaymentConfirmed}

import scala.concurrent.duration._

class PaymentService extends Actor with Timers {
  override def receive: Receive = LoggingReceive {
    case DoPayment(address) =>
      context.actorOf(Props[PaymentServer]) ! DoPayment(address)
    case PaymentReceived =>
      sender ! PaymentConfirmed
      context.parent ! PaymentReceived
      self ! PoisonPill
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute) {
      case BadRequest => context.parent ! InvalidPayment; Stop
      case Unauthorized => context.parent ! InvalidPayment; Stop
      case Forbidden => context.parent ! InvalidPayment; Restart
      case NotFound => context.parent ! InvalidPayment; Stop
      case MethodNotAllowed => Restart
      case NotAcceptable => Restart
      case RequestTimeout => Restart
      case ExceptionFailed => Restart
      case ImATeapot => Escalate
      case InternalServerError => Restart
      case BadGateway => Restart
      case ServiceUnavailable => Restart
      case Exception => Escalate
    }
}

object PaymentService {
  def apply: PaymentService = new PaymentService()
}
