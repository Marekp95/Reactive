package messages

import actors.PaymentServer.PaymentSystem
import akka.http.scaladsl.model.StatusCode

object PaymentServiceMessages {

  sealed trait Message

  case class DoPayment(paymentSystem: PaymentSystem) extends Message

  case object PaymentConfirmed extends Message

  case object PaymentReceived extends Message

  case object InvalidPayment extends Message

  case class PaymentError(code: StatusCode) extends Message

}
