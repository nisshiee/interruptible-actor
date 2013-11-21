package org.nisshiee.interruptibleactor;

public class JavaActor extends InterruptibleActorJ {

    @Override
    public void onReceive(Object message) throws Exception {
        context().sender().tell(message, self());
        Thread.sleep(500);
    }
}
