package com.xyp.gtnc.Common.packet.wildcard;

import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class WildcardNetwork {

    public static final SimpleNetworkWrapper CHANNEL = ScienceNotCool.channel;

    private WildcardNetwork() {}

    public static void init() {
        CHANNEL.registerMessage(
            MessageUpdateWildcardConfig.Handler.class,
            MessageUpdateWildcardConfig.class,
            0,
            Side.SERVER);
    }
}
