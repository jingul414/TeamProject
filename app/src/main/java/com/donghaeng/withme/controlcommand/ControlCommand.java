package com.donghaeng.withme.controlcommand;

public abstract class ControlCommand {
    public byte controlCommandType;
    
    public ControlCommand(byte command) {
        setControlCommandType(command);
    }

    public final byte getControlCommandType() {
        return controlCommandType;
    }
    public final void setControlCommandType(byte controlCommandType) {
        this.controlCommandType = controlCommandType;
    }
}
