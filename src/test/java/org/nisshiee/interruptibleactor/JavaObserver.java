package org.nisshiee.interruptibleactor;

import akka.actor.*;
import java.util.*;

public class JavaObserver extends UntypedActor {

    private List<Object> log;

    public JavaObserver(List<Object> log) {
        this.log = log;
    }

    public List<Object> getLog() {
        return this.log;
    }

    @Override
    public void onReceive(Object message) {
        this.log.add(message);
    }
}
