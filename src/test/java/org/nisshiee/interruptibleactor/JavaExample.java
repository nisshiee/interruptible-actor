package org.nisshiee.interruptibleactor;

import akka.actor.*;
import java.util.*;

public class JavaExample {

    public static List<Object> e1(ActorSystem system) throws Exception {

        ActorRef actor = InterruptibleActorJ.create(system, JavaActor.class);

        List<Object> log = new ArrayList<Object>();
        ActorRef observer = system.actorOf(Props.create(JavaObserver.class, log));

        for (int i = 1; i <= 5; i++) {
            if (i == 1) {
                actor.tell(i, observer);
                Thread.sleep(200);
            } else if (i == 4) {
                actor.tell(InterruptibleActorJ.RESET(), observer);
            } else {
                actor.tell(i, observer);
            }
        }
        Thread.sleep(2000);

        return log;
    }
}
