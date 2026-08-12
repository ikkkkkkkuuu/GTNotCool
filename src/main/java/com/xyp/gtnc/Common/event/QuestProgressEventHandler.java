package com.xyp.gtnc.Common.event;

import com.xyp.gtnc.Loader.QuestLoader;

import betterquesting.api.events.DatabaseEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Captures only this addon's quest progress after BetterQuesting loads a world's databases. */
public final class QuestProgressEventHandler {

    @SubscribeEvent
    public void onDatabaseLoad(DatabaseEvent.Load event) {
        if (event.getType() == DatabaseEvent.DBType.ALL || event.getType() == DatabaseEvent.DBType.QUEST) {
            QuestLoader.captureProgressBeforeDefaultReload();
        }
    }
}
