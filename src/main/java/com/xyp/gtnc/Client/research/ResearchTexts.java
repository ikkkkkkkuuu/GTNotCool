package com.xyp.gtnc.Client.research;

import net.minecraft.util.StatCollector;

import cpw.mods.fml.common.FMLCommonHandler;

public final class ResearchTexts {

    private ResearchTexts() {}

    public static String unlockAll() {
        // #tr gui.gtnc.research.unlock_all
        // # Discover all aspects
        // # zh_CN 解锁全部要素
        return tr("gui.gtnc.research.unlock_all", "Discover all aspects", "解锁全部要素");
    }

    public static String auto(boolean enabled) {
        // #tr gui.gtnc.research.auto_on
        // # Auto research: ON
        // # zh_CN 自动研究：开启
        String on = tr("gui.gtnc.research.auto_on", "Auto research: ON", "自动研究：开启");
        // #tr gui.gtnc.research.auto_off
        // # Auto research: OFF
        // # zh_CN 自动研究：关闭
        String off = tr("gui.gtnc.research.auto_off", "Auto research: OFF", "自动研究：关闭");
        return enabled ? on : off;
    }

    public static String solve() {
        // #tr gui.gtnc.research.solve
        // # Solve current note
        // # zh_CN 解锁当前笔记
        return tr("gui.gtnc.research.solve", "Solve current note", "解锁当前笔记");
    }

    public static String retry() {
        // #tr gui.gtnc.research.retry
        // # Retry last solution
        // # zh_CN 重试上次方案
        return tr("gui.gtnc.research.retry", "Retry last solution", "重试上次方案");
    }

    public static String confirm() {
        // #tr gui.gtnc.research.confirm
        // # OK
        // # zh_CN 确定
        return tr("gui.gtnc.research.confirm", "OK", "确定");
    }

    public static String noNote() {
        // #tr gui.gtnc.research.no_note
        // # No incomplete research note was found.
        // # zh_CN 没有找到未完成的研究笔记
        return tr("gui.gtnc.research.no_note", "No incomplete research note was found.", "没有找到未完成的研究笔记");
    }

    public static String solveFailed() {
        // #tr gui.gtnc.research.solve_failed
        // # No valid connection was found for this note.
        // # zh_CN 未能为这张笔记找到有效连接方案
        return tr("gui.gtnc.research.solve_failed", "No valid connection was found for this note.", "未能为这张笔记找到有效连接方案");
    }

    public static String busy() {
        // #tr gui.gtnc.research.busy
        // # Research automation is already running.
        // # zh_CN 自动研究任务正在执行
        return tr("gui.gtnc.research.busy", "Research automation is already running.", "自动研究任务正在执行");
    }

    public static String started() {
        // #tr gui.gtnc.research.started
        // # Research automation started. Keep this table open.
        // # zh_CN 自动研究已开始，请保持研究台界面开启
        return tr(
            "gui.gtnc.research.started",
            "Research automation started. Keep this table open.",
            "自动研究已开始，请保持研究台界面开启");
    }

    public static String complete() {
        // #tr gui.gtnc.research.complete
        // # Research automation finished.
        // # zh_CN 自动研究操作已完成
        return tr("gui.gtnc.research.complete", "Research automation finished.", "自动研究操作已完成");
    }

    public static String cancelled() {
        // #tr gui.gtnc.research.cancelled
        // # Research automation was cancelled because the table was closed.
        // # zh_CN 研究台已关闭，自动研究任务已取消
        return tr(
            "gui.gtnc.research.cancelled",
            "Research automation was cancelled because the table was closed.",
            "研究台已关闭，自动研究任务已取消");
    }

    public static String missingPrimal(String aspectName) {
        // #tr gui.gtnc.research.missing_primal
        // # Missing primal aspect:
        // # zh_CN 基础要素不足：
        return tr("gui.gtnc.research.missing_primal", "Missing primal aspect:", "基础要素不足：") + " " + aspectName;
    }

    public static String noRetry() {
        // #tr gui.gtnc.research.no_retry
        // # The current note does not match the last solution.
        // # zh_CN 当前笔记与上次方案不一致
        return tr("gui.gtnc.research.no_retry", "The current note does not match the last solution.", "当前笔记与上次方案不一致");
    }

    public static String invalidAmount() {
        // #tr gui.gtnc.research.invalid_amount
        // # Enter an amount greater than zero.
        // # zh_CN 请输入大于零的合成数量
        return tr("gui.gtnc.research.invalid_amount", "Enter an amount greater than zero.", "请输入大于零的合成数量");
    }

    public static String synthesizeHint() {
        // #tr gui.gtnc.research.synthesize_hint
        // # Ctrl-click an aspect to synthesize a chosen amount.
        // # zh_CN Ctrl 点击要素可输入批量合成数量
        return tr(
            "gui.gtnc.research.synthesize_hint",
            "Ctrl-click an aspect to synthesize a chosen amount.",
            "Ctrl 点击要素可输入批量合成数量");
    }

    private static String tr(String key, String english, String chinese) {
        String translated = StatCollector.translateToLocal(key);
        if (!key.equals(translated)) return translated;
        return "zh_CN".equals(
            FMLCommonHandler.instance()
                .getCurrentLanguage()) ? chinese : english;
    }
}
