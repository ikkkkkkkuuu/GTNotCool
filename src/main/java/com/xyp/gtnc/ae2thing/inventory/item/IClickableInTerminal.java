package com.xyp.gtnc.ae2thing.inventory.item;

import com.xyp.gtnc.ae2thing.util.Util;

public interface IClickableInTerminal {

    void setClickedInterface(Util.DimensionalCoordSide tile);

    Util.DimensionalCoordSide getClickedInterface();
}
