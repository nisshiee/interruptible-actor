package org.nisshiee.interruptibleactor

import org.specs2._, specification._

import akka.actor._
import akka.testkit._

class InterruptibleActorSpec extends Specification { def is =

  "InterruptibleActor"                                                          ^
    "ベーシックなケース"                                                        ! e1^
    "大量のメッセージを捌くケース"                                              ! e2^
    "状態を持つケース"                                                          ! e3^
    "senderにreplyするケース"                                                   ! e4^
    Step(teardown)                                                              ^
                                                                                end

  import InterruptibleActorSpec._
  implicit lazy val system = ActorSystem("InterruptibleActorSpec")

  object observe extends Outside[TestActorRef[Observer]] {
    override def outside = TestActorRef[Observer]
  }

  def teardown = {
    system.shutdown
  }

  def e1 = observe { observer =>
    val actor = InterruptibleActor(classOf[E1Actor], observer)
    (1 to 5) foreach {
      case 1 => {
        actor ! 1
        Thread.sleep(200)
      }
      case 4 => actor ! InterruptibleActor.Reset
      case i => actor ! i
    }
    Thread.sleep(2000)
    observer.underlyingActor.log must equalTo(List(5, 1))
  }

  def e2 = observe { observer =>
    val actor = InterruptibleActor(classOf[E2Actor], observer)
    (1 to 10000) foreach {
      case 5000 => actor ! InterruptibleActor.Reset
      case i => actor ! i
    }
    Thread.sleep(10000)
    val log = observer.underlyingActor.log.reverse
    (log must contain(allOf(be_>(5000)).inOrder)) and
    (log must not contain(be_>(4000) and be_<=(5000)))
  }

  def e3 = observe { observer =>
    val actor = InterruptibleActor(classOf[E3Actor], observer)
    (1 to 5) foreach {
      case 1 => {
        actor ! 1
        Thread.sleep(200)
      }
      case 4 => actor ! InterruptibleActor.Reset
      case i => actor ! i
    }
    Thread.sleep(2000)
    observer.underlyingActor.log must equalTo(List(2, 1))
  }

  def e4 = observe { observer =>
    val actor = InterruptibleActor[E4Actor]
    (1 to 5) foreach {
      case 1 => {
        actor.tell(1, observer)
        Thread.sleep(200)
      }
      case 4 => actor.tell(InterruptibleActor.Reset, observer)
      case i => actor.tell(i, observer)
    }
    Thread.sleep(2000)
    observer.underlyingActor.log must equalTo(List(5, 1))
  }
}

object InterruptibleActorSpec {
  class E1Actor(val observer: ActorRef) extends InterruptibleActor {
    override def regularReceive = {
      case m => {
        observer ! m
        Thread.sleep(500)
      }
    }
  }

  class E2Actor(val observer: ActorRef) extends InterruptibleActor {
    override def regularReceive = {
      case m => {
        observer ! m
      }
    }
  }

  class E3Actor(val observer: ActorRef) extends InterruptibleActor {
    var count = 0
    override def regularReceive = {
      case m => {
        count = count + 1
        observer ! count
        Thread.sleep(500)
      }
    }
  }

  class E4Actor extends InterruptibleActor {
    override def regularReceive = {
      case m => {
        sender ! m
        Thread.sleep(500)
      }
    }
  }
}
