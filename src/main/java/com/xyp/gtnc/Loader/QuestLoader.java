package com.xyp.gtnc.Loader;

import java.util.UUID;

import com.hfstudio.bqapi.BQApi;
import com.hfstudio.bqapi.api.builder.Chapters;
import com.xyp.gtnc.ScienceNotCool;

/** Registers this mod's BetterQuesting chapters when the optional API is available. */
public final class QuestLoader {

    private static final String QUEST_RESOURCE_ROOT = "taskbook";
    private static final String BACK_TO_FUTURE_LINE = "BackToFuture-QkFDS1RPRlVUVVJFX0dUTg==";
    private static final UUID BACK_TO_FUTURE_UUID = new UUID(0x4241434B544F4655L, 0x545552455F47544EL);

    private static boolean registered;

    private QuestLoader() {}

    public static void registry() {
        if (registered) {
            return;
        }

        BQApi.register(
            Chapters.imported("sciencenotcool.back_to_future")
                .resourceFolder(ScienceNotCool.MODID, QUEST_RESOURCE_ROOT)
                .lineDirectory(BACK_TO_FUTURE_LINE)
                .uuid(BACK_TO_FUTURE_UUID)
                .build());
        registered = true;
    }
}
