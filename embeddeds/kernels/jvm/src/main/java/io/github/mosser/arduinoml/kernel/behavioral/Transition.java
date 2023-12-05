package io.github.mosser.arduinoml.kernel.behavioral;

public class Transition {
    private State next;

    public State getNext() {
        return next;
    }

    public void setNext(State next) {
        this.next = next;
    }

}
