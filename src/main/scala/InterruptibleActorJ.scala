package org.nisshiee.interruptibleactor

import scala.annotation.varargs
import akka.actor._

abstract class InterruptibleActorJ extends InterruptibleActor {

  @throws(classOf[Exception])
  def onReceive(message: Any): Unit

  override def regularReceive = {
    case m => onReceive(m)
  }
}

object InterruptibleActorJ {

  @varargs
  def create[T <: InterruptibleActorJ](factory: ActorRefFactory, clazz: Class[T], args: AnyRef*): ActorRef =
    InterruptibleActor(clazz, args: _*)(factory)

  def RESET(): Any = InterruptibleActor.Reset
}
