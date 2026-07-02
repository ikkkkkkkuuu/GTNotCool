package com.xyp.gtnc.ae2thing.client.event;

import cpw.mods.fml.common.eventhandler.Event;

public class EncodeEvent extends Event {

    public static boolean encode = false;

    public EncodeEvent() {

    }

    public EncodeEvent(boolean encode) {
        EncodeEvent.encode = encode;
    }
}
