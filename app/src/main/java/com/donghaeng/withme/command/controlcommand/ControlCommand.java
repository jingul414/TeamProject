package com.donghaeng.withme.command.controlcommand;

public abstract class ControlCommand {
    private byte controlCommandType;
    
    protected ControlCommand(byte controlCommandType) {
        setControlCommandType(controlCommandType);
    }

    protected abstract void cancelControl();

    public final byte getControlCommandType() {
        return controlCommandType;
    }
    private void setControlCommandType(byte controlCommandType) {
        this.controlCommandType = controlCommandType;
    }
}
