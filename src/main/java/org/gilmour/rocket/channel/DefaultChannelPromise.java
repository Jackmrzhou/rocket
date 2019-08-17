package org.gilmour.rocket.channel;

public class DefaultChannelPromise implements ChannelPromise {

    private boolean done;

    public DefaultChannelPromise(){
        done = false;
    }

    @Override
    public void setDone() {
        this.done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
