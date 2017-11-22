package actors

import actors.PaymentServer._
import akka.actor.{Actor, PoisonPill}
import akka.event.LoggingReceive
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import messages.CheckoutMessages.PaymentReceived
import messages.PaymentServiceMessages.DoPayment

class PaymentServer extends Actor {
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  import akka.pattern.pipe
  import context.dispatcher

  val http = Http(context.system)

  override def receive: Receive = LoggingReceive {
    case DoPayment(paymentSystem) =>
      http.singleRequest(HttpRequest(uri = paymentSystem.address)).pipeTo(self)
    case HttpResponse(StatusCodes.OK, _, _, _) =>
      context.parent ! PaymentReceived
      self ! PoisonPill
    case HttpResponse(StatusCodes.BadRequest, _, _, _) =>
      throw BadRequest
    case HttpResponse(StatusCodes.Unauthorized, _, _, _) =>
      throw Unauthorized
    case HttpResponse(StatusCodes.Forbidden, _, _, _) =>
      throw Forbidden
    case HttpResponse(StatusCodes.NotFound, _, _, _) =>
      throw NotFound
    case HttpResponse(StatusCodes.MethodNotAllowed, _, _, _) =>
      throw MethodNotAllowed
    case HttpResponse(StatusCodes.NotAcceptable, _, _, _) =>
      throw NotAcceptable
    case HttpResponse(StatusCodes.RequestTimeout, _, _, _) =>
      throw RequestTimeout
    case HttpResponse(StatusCodes.ExpectationFailed, _, _, _) =>
      throw ExceptionFailed
    case HttpResponse(StatusCodes.ImATeapot, _, _, _) =>
      throw ImATeapot
    case HttpResponse(StatusCodes.InternalServerError, _, _, _) =>
      throw InternalServerError
    case HttpResponse(StatusCodes.BadRequest, _, _, _) =>
      throw BadGateway
    case HttpResponse(StatusCodes.ServiceUnavailable, _, _, _) =>
      throw ServiceUnavailable
    case HttpResponse(_, _, _, _) =>
      throw Exception
  }
}

case object PaymentServer {
  def apply: PaymentServer = new PaymentServer()


  sealed trait PaymentSystem {
    def address: String
  }

  case object PayPal extends PaymentSystem {
    override def address: String = "https://www.paypal.com/pl/home"
  }

  case object PayU extends PaymentSystem {
    override def address: String = "https://www.payu.pl/"
  }

  case object BadRequest extends Exception

  case object Unauthorized extends Exception

  case object Forbidden extends Exception

  case object NotFound extends Exception

  case object MethodNotAllowed extends Exception

  case object NotAcceptable extends Exception

  case object RequestTimeout extends Exception

  case object ExceptionFailed extends Exception


  case object ImATeapot extends Exception


  case object InternalServerError extends Exception

  case object BadGateway extends Exception

  case object ServiceUnavailable extends Exception

}
