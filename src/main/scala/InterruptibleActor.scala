package org.nisshiee.interruptibleactor

import scala.reflect.ClassTag

import akka.actor._
import akka.dispatch.RequiresMessageQueue

object InterruptibleActor {

  object Reset
  private[interruptibleactor] object Resume

  def apply[T <: InterruptibleActor](clazz: Class[_], args: Any*)(implicit factory: ActorRefFactory): ActorRef =
    apply(Props(clazz, args: _*))
  def apply[T <: InterruptibleActor](creator: => T)(implicit factory: ActorRefFactory, classTag: ClassTag[T]): ActorRef =
    apply(Props(creator))
  def apply[T <: InterruptibleActor]()(implicit factory: ActorRefFactory, classTag: ClassTag[T]): ActorRef =
    apply(Props[T])
  private def apply(props: Props)(implicit factory: ActorRefFactory): ActorRef =
    factory.actorOf(Props(classOf[CapWrapper], props))
}

trait InterruptibleActor extends Actor with RequiresMessageQueue[InterruptibleMessageQueue] {

  def regularReceive: Receive

  override def receive = receiveReset orElse regularReceive

  val receiveReset: Receive = {
    case InterruptibleActor.Reset => context.become(resetting, discardOld = false)
  }

  val resetting: Receive = {
    case InterruptibleActor.Resume if sender == context.parent => context.unbecome
  }
}
