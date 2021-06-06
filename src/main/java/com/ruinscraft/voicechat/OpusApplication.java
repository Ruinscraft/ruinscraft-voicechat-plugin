package com.ruinscraft.voicechat;

public enum OpusApplication {

    VOIP(2048), AUDIO(2049), RESTRICTED_LOWDELAY(2051);

    private final int opusMagicValue;

    OpusApplication(int opusMagicValue) {
        this.opusMagicValue = opusMagicValue;
    }

    public int getOpusMagicValue() {
        return opusMagicValue;
    }

}
