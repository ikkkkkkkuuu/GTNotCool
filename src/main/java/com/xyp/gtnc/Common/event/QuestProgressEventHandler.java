package com.xyp.gtnc.Common.event;

import com.xyp.gtnc.Loader.QuestLoader;

import betterquesting.api.events.DatabaseEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Preserves addon quest progress across BetterQuesting's startup default-database reload. */
public final class QuestProgressEventHandler {

    @SubscribeEvent
    public void onDatabaseLoad(DatabaseEvent.Load event) {
        if (event.getType() == DatabaseEvent.DBType.ALL || event.getType() == DatabaseEvent.DBType.QUEST) {
            QuestLoader.captureProgressBeforeDefaultReload();
        }
    }
}
