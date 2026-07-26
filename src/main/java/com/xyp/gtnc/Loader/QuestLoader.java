package com.xyp.gtnc.Loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.gson.JsonParser;
import com.xyp.gtnc.ScienceNotCool;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.utils.NBTConverter;
import betterquesting.handlers.SaveLoadHandler;
import betterquesting.network.handlers.NetChapterSync;
import betterquesting.network.handlers.NetQuestSync;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestLineDatabase;

/** Imports this mod's packaged quest line into a world's BetterQuesting database once. */
public final class QuestLoader {

    private static final String RESOURCE_ROOT = "assets/" + ScienceNotCool.MODID + "/taskbook/";
    private static final String BACK_TO_FUTURE_LINE = "BackToFuture-QkFDS1RPRlVUVVJFX0dUTg==";
    private static final UUID BACK_TO_FUTURE_LINE_ID = new UUID(4774171070805526101L, 6076853730111083598L);
    private static final String[] QUEST_FILES = { "StarterReward-U1RBUlRFUi1QQUNLLUdUTg==.json",
        "TravelerTerminal-VFJBVkVMRVItVEVSTUlOQUw=.json", "ArcaneExemption-QVJDQU5FLUVYRU1QVElPTg==.json" };
    private static final String[] PLACEMENT_FILES = { "StarterReward-U1RBUlRFUi1QQUNLLUdUTg==.json",
        "TravelerTerminal-VFJBVkVMRVItVEVSTUlOQUw=.json", "ArcaneExemption-QVJDQU5FLUVYRU1QVElPTg==.json" };

    private QuestLoader() {}

    /**
     * Runs after BetterQuesting has loaded the world's database. Existing quests are left untouched so pack makers
     * can safely customize them in-game.
     */
    public static void importIfMissing() {
        importTasks(false);
    }

    /** Replaces this mod's packaged quest definitions without resetting player progress. */
    public static boolean updateTasks() {
        return importTasks(true);
    }

    private static boolean importTasks(boolean overwrite) {
        try {
            List<UUID> updatedQuestIds = importQuests(overwrite);
            boolean lineChanged = importLine(overwrite);
            boolean changed = !updatedQuestIds.isEmpty() || lineChanged;
            if (changed) {
                SaveLoadHandler.INSTANCE.markDirty();
                if (!updatedQuestIds.isEmpty()) {
                    NetQuestSync.sendSync(null, updatedQuestIds, true, false);
                }
                if (lineChanged) {
                    NetChapterSync.sendSync(null, Collections.singletonList(BACK_TO_FUTURE_LINE_ID));
                }
                ScienceNotCool.LOG
                    .info("{} the Back to future BetterQuesting task line.", overwrite ? "Updated" : "Imported");
            }
            return true;
        } catch (IOException | RuntimeException exception) {
            ScienceNotCool.LOG.error(
                "Failed to {} the Back to future BetterQuesting task line.",
                overwrite ? "update" : "import",
                exception);
            return false;
        }
    }

    private static List<UUID> importQuests(boolean overwrite) throws IOException {
        List<UUID> updatedQuestIds = new ArrayList<>();
        for (String questFile : QUEST_FILES) {
            NBTTagCompound questTag = readNbt("Quests/" + BACK_TO_FUTURE_LINE + "/" + questFile);
            UUID questId = NBTConverter.UuidValueType.QUEST.readId((NBTTagCompound) questTag.copy());
            IQuest quest = QuestDatabase.INSTANCE.get(questId);
            if (quest != null && !overwrite) {
                continue;
            }

            if (quest == null) {
                quest = QuestDatabase.INSTANCE.createNew(questId);
            }
            quest.readFromNBT(questTag);
            updatedQuestIds.add(questId);
        }
        return updatedQuestIds;
    }

    private static boolean importLine(boolean overwrite) throws IOException {
        NBTTagCompound lineTag = readNbt("QuestLines/" + BACK_TO_FUTURE_LINE + "/QuestLine.json");
        UUID lineId = NBTConverter.UuidValueType.QUEST_LINE.readId((NBTTagCompound) lineTag.copy());
        IQuestLine line = QuestLineDatabase.INSTANCE.get(lineId);
        if (line != null && !overwrite) {
            return false;
        }

        NBTTagList placements = new NBTTagList();
        for (String placementFile : PLACEMENT_FILES) {
            placements.appendTag(readNbt("QuestLines/" + BACK_TO_FUTURE_LINE + "/" + placementFile));
        }
        lineTag.setTag("quests", placements);

        int orderIndex = line == null ? Integer.MAX_VALUE : QuestLineDatabase.INSTANCE.getOrderIndex(lineId);
        if (line == null) {
            line = QuestLineDatabase.INSTANCE.createNew(lineId);
        }
        line.readFromNBT(lineTag, false);
        QuestLineDatabase.INSTANCE.getOrderedEntries();
        QuestLineDatabase.INSTANCE.setOrderIndex(lineId, orderIndex);
        return true;
    }

    private static NBTTagCompound readNbt(String relativePath) throws IOException {
        String resourcePath = RESOURCE_ROOT + relativePath;
        InputStream input = QuestLoader.class.getClassLoader()
            .getResourceAsStream(resourcePath);
        if (input == null) {
            throw new IOException("Missing packaged quest resource " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            StringBuilder json = new StringBuilder();
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) >= 0) {
                json.append(buffer, 0, read);
            }
            return NBTConverter.JSONtoNBT_Object(
                new JsonParser().parse(json.toString())
                    .getAsJsonObject(),
                new NBTTagCompound(),
                true);
        }
    }
}
