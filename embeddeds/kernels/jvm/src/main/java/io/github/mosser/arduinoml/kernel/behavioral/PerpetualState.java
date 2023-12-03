package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class PerpetualState extends NormalState{

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
