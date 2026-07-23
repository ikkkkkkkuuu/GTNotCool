package com.xyp.gtnc.mixins.late.Thaumcraft;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ResearchNoteData;
import thaumcraft.common.lib.utils.HexUtils;

/**
 * 研究台连连看求解器（纯逻辑，无客户端/服务端专属依赖）。
 * <p>
 * 在六边形网格上给空格（{@code type==0}）分配已发现的源质，使全部主源质（{@code type==1}）通过
 * 「相邻两格源质互为合成组件」的链彼此连通——与 {@code ResearchManager.checkResearchCompletion} 的判定同构。
 * <p>
 * 算法：以第一个主源质为种子求连通域，对每个尚未接入的主源质用 BFS（状态 = 格 + 该格所填源质，
 * 只走空格、只用已发现源质、每步满足组件关系）找接入连通域的最短链，落子后并入连通域并做一次泛洪
 * （顺带吸收因此变得连通的其它主源质），重复至全部连通。
 */
final class AutoResearchSolver {

    private AutoResearchSolver() {}

    /** BFS 访问节点上限，防止病态网格卡死（正常研究网格远小于此）。 */
    private static final int MAX_VISITED = 200000;

    /** 求解结果：需要落子的「格 key -> 源质」映射（有序）。 */
    static final class Result {

        /** 需要填入的空格及其源质，按落子顺序排列。 */
        final LinkedHashMap<String, Aspect> placements;
        /** true 表示所有主源质在不落任何子时就已连通（调用方需自行放一个「触发子」以引发完成判定）。 */
        final boolean alreadyConnected;

        Result(LinkedHashMap<String, Aspect> placements, boolean alreadyConnected) {
            this.placements = placements;
            this.alreadyConnected = alreadyConnected;
        }
    }

    /** 求解失败原因，供上层给出针对性提示。 */
    enum Failure {
        /** 某个主源质本身尚未被玩家发现——不可能连通。 */
        MAIN_ASPECT_UNDISCOVERED,
        /** 找不到把某主源质接入连通域的链（缺少可作桥梁的已发现源质，或几何上无法连通）。 */
        NO_PATH
    }

    /** 求解异常：携带失败原因与相关源质（可空）。 */
    static final class SolveException extends Exception {

        final Failure failure;
        final Aspect aspect;

        SolveException(Failure failure, Aspect aspect) {
            this.failure = failure;
            this.aspect = aspect;
        }
    }

    /**
     * 求解一份研究笔记。
     *
     * @param note       客户端解析出的笔记数据（含 hexes / hexEntries）
     * @param discovered 玩家已发现的源质集合（完成判定只认「已发现」，与点数无关）
     * @return 落子方案；{@link Result#placements} 为空且 {@link Result#alreadyConnected} 为 true 表示无需落子已连通
     * @throws SolveException 主源质未发现或几何/源质上无法连通
     */
    static Result solve(ResearchNoteData note, Set<Aspect> discovered) throws SolveException {
        java.util.List<String> mains = new java.util.ArrayList<String>();
        for (Map.Entry<String, HexUtils.Hex> e : note.hexes.entrySet()) {
            ResearchManager.HexEntry he = note.hexEntries.get(e.getKey());
            if (he != null && he.type == 1) {
                mains.add(e.getKey());
            }
        }

        // 主源质必须全部已发现，否则连通判定永不成立（与原版一致：不可能完成）。
        for (String m : mains) {
            Aspect a = note.hexEntries.get(m).aspect;
            if (a == null || !discovered.contains(a)) {
                throw new SolveException(Failure.MAIN_ASPECT_UNDISCOVERED, a);
            }
        }

        // 已占用格（有确定源质）：主源质固定，空格随求解逐步填入。
        Map<String, Aspect> assigned = new java.util.HashMap<String, Aspect>();
        for (String m : mains) {
            assigned.put(m, note.hexEntries.get(m).aspect);
        }

        LinkedHashMap<String, Aspect> placements = new LinkedHashMap<String, Aspect>();

        if (mains.size() <= 1) {
            // 0 或 1 个主源质：天然连通，无需落子（调用方仍需放一个触发子来引发完成判定）。
            return new Result(placements, true);
        }

        Set<String> connected = flood(mains.get(0), assigned, note, discovered);

        while (!connected.containsAll(mains)) {
            java.util.List<String> best = null;
            Map<String, Aspect> bestAssign = null;
            Aspect stuckAspect = null;

            for (String m : mains) {
                if (connected.contains(m)) {
                    continue;
                }
                stuckAspect = note.hexEntries.get(m).aspect;
                PathResult pr = bfs(m, connected, assigned, note, discovered);
                if (pr != null && (best == null || pr.cells.size() < best.size())) {
                    best = pr.cells;
                    bestAssign = pr.aspects;
                }
            }

            if (best == null) {
                throw new SolveException(Failure.NO_PATH, stuckAspect);
            }

            for (String cell : best) {
                Aspect a = bestAssign.get(cell);
                assigned.put(cell, a);
                placements.put(cell, a);
            }
            connected = flood(mains.get(0), assigned, note, discovered);
        }

        return new Result(placements, placements.isEmpty());
    }

    /** 从 start（已占用格）出发，沿「相邻两格源质互为组件」的链泛洪，返回可达的已占用格集合。 */
    private static Set<String> flood(String start, Map<String, Aspect> assigned, ResearchNoteData note,
        Set<Aspect> discovered) {
        Set<String> visited = new HashSet<String>();
        Deque<String> queue = new ArrayDeque<String>();
        visited.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            String cur = queue.poll();
            HexUtils.Hex curHex = note.hexes.get(cur);
            Aspect curAsp = assigned.get(cur);
            for (int dir = 0; dir < 6; ++dir) {
                String k = curHex.getNeighbour(dir)
                    .toString();
                if (visited.contains(k) || !assigned.containsKey(k)) {
                    continue;
                }
                if (link(curAsp, assigned.get(k), discovered)) {
                    visited.add(k);
                    queue.add(k);
                }
            }
        }
        return visited;
    }

    /** BFS 结果：一条把某主源质接入连通域的空格链（cells 按落子顺序），及其对应源质。 */
    private static final class PathResult {

        final java.util.List<String> cells;
        final Map<String, Aspect> aspects;

        PathResult(java.util.List<String> cells, Map<String, Aspect> aspects) {
            this.cells = cells;
            this.aspects = aspects;
        }
    }

    private static final class State {

        final String cell;
        final Aspect aspect;
        final State prev;

        State(String cell, Aspect aspect, State prev) {
            this.cell = cell;
            this.aspect = aspect;
            this.prev = prev;
        }
    }

    /**
     * 在空格上寻找把 target（未连通主源质）接入 connected 连通域的最短链。
     * 状态 = (格, 该格所填源质)，只走 type==0 的空格、只用已发现源质、每步满足组件关系。
     */
    private static PathResult bfs(String target, Set<String> connected, Map<String, Aspect> assigned,
        ResearchNoteData note, Set<Aspect> discovered) {
        HexUtils.Hex targetHex = note.hexes.get(target);
        Aspect targetAsp = assigned.get(target);
        Set<String> targetNeighbours = neighbourKeys(targetHex);

        Set<String> visitedStates = new HashSet<String>();
        Deque<State> queue = new ArrayDeque<State>();

        // 种子：从连通域每个已占用格，走向相邻空格并尝试所有可链接的已发现源质。
        for (String oc : connected) {
            HexUtils.Hex ocHex = note.hexes.get(oc);
            Aspect ocAsp = assigned.get(oc);
            for (int dir = 0; dir < 6; ++dir) {
                String k = ocHex.getNeighbour(dir)
                    .toString();
                if (!isBlank(k, assigned, note)) {
                    continue;
                }
                for (Aspect cand : discovered) {
                    if (!link(ocAsp, cand, discovered)) {
                        continue;
                    }
                    String sk = k + "|" + cand.getTag();
                    if (visitedStates.add(sk)) {
                        queue.add(new State(k, cand, null));
                    }
                }
            }
        }

        int visits = 0;
        while (!queue.isEmpty()) {
            if (++visits > MAX_VISITED) {
                return null;
            }
            State s = queue.poll();
            // 能否直接接入 target？
            if (targetNeighbours.contains(s.cell) && link(s.aspect, targetAsp, discovered)) {
                return reconstruct(s);
            }
            HexUtils.Hex curHex = note.hexes.get(s.cell);
            for (int dir = 0; dir < 6; ++dir) {
                String k = curHex.getNeighbour(dir)
                    .toString();
                if (!isBlank(k, assigned, note)) {
                    continue;
                }
                for (Aspect cand : discovered) {
                    if (!link(s.aspect, cand, discovered)) {
                        continue;
                    }
                    String sk = k + "|" + cand.getTag();
                    if (visitedStates.add(sk)) {
                        queue.add(new State(k, cand, s));
                    }
                }
            }
        }
        return null;
    }

    private static PathResult reconstruct(State end) {
        java.util.ArrayList<String> cells = new java.util.ArrayList<String>();
        Map<String, Aspect> aspects = new java.util.HashMap<String, Aspect>();
        for (State s = end; s != null; s = s.prev) {
            cells.add(s.cell);
            aspects.put(s.cell, s.aspect);
        }
        java.util.Collections.reverse(cells); // 从连通域向外的落子顺序
        return new PathResult(cells, aspects);
    }

    private static Set<String> neighbourKeys(HexUtils.Hex hex) {
        Set<String> out = new HashSet<String>();
        for (int dir = 0; dir < 6; ++dir) {
            out.add(
                hex.getNeighbour(dir)
                    .toString());
        }
        return out;
    }

    /** 该 key 是否为网格中一个尚未占用的空格（type==0）。 */
    private static boolean isBlank(String key, Map<String, Aspect> assigned, ResearchNoteData note) {
        if (assigned.containsKey(key) || !note.hexes.containsKey(key)) {
            return false;
        }
        ResearchManager.HexEntry he = note.hexEntries.get(key);
        return he != null && he.type == 0;
    }

    /** 两源质相邻是否连通：均已发现，且其一为非基础源质且另一为其组件（与原版判定同构）。 */
    private static boolean link(Aspect a1, Aspect a2, Set<Aspect> discovered) {
        if (a1 == null || a2 == null || !discovered.contains(a1) || !discovered.contains(a2)) {
            return false;
        }
        if (!a1.isPrimal()) {
            Aspect[] c = a1.getComponents();
            if (c != null && (c[0] == a2 || c[1] == a2)) {
                return true;
            }
        }
        if (!a2.isPrimal()) {
            Aspect[] c = a2.getComponents();
            if (c != null && (c[0] == a1 || c[1] == a1)) {
                return true;
            }
        }
        return false;
    }
}
