package org.nisshiee.interruptibleactor

import akka.actor._

class Observer extends Actor {

  private[interruptibleactor] var log: List[Int] = Nil

  override def receive = { case m: Int => log = m :: log }
}
