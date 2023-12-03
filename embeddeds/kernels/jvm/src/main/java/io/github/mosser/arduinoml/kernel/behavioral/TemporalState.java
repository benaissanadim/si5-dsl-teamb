package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class TemporalState extends NormalState {

    private int duration;

    private TemporalTransition transition;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public TemporalTransition getTransition() {
        return transition;
    }

    public void setTransition(TemporalTransition transition) {
        this.transition = transition;
    }

}
