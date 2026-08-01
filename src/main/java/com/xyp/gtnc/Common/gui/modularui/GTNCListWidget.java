package com.xyp.gtnc.Common.gui.modularui;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.widgets.ListWidget;

public class GTNCListWidget<I extends IWidget, W extends ListWidget<I, W>> extends ListWidget<I, W> {

    private int initialScrollY;

    public GTNCListWidget() {
        this(0);
    }

    public GTNCListWidget(int initialScrollY) {
        super();
        this.initialScrollY = initialScrollY;
    }

    @Override
    public void postResize() {
        super.postResize();
        if (initialScrollY > 0 && getScrollArea().getScrollY() != null) {
            getScrollArea().getScrollY()
                .scrollTo(getScrollArea(), initialScrollY);
            initialScrollY = 0;
        }
    }
}
