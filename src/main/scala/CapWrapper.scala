package org.nisshiee.interruptibleactor

import akka.actor._

class CapWrapper(val props: Props) extends Actor {

  val actor = context.actorOf(props)

  override def receive = {
    case InterruptibleActor.Reset => {
      actor ! InterruptibleActor.Reset
      actor ! InterruptibleActor.Resume
    }
    case m => actor.tell(m, sender)
  }
}
