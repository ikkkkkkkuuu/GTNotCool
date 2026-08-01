package com.xyp.gtnc.Client.research;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ResearchNoteData;
import thaumcraft.common.lib.utils.HexUtils;

/** Pure-Java solver for Thaumcraft 4 research notes. This class has no Minecraft client dependencies. */
public final class ResearchSolver {

    private ResearchSolver() {}

    public static Plan solve(ResearchNoteData note, AspectList playerAspects) {
        if (note == null || note.complete) return null;

        Map<String, HexUtils.Hex> board = new HashMap<>(note.hexes);
        Map<String, Aspect> fixed = new LinkedHashMap<>();
        for (Map.Entry<String, ResearchManager.HexEntry> entry : note.hexEntries.entrySet()) {
            ResearchManager.HexEntry value = entry.getValue();
            if (value != null && value.type == 1 && value.aspect != null && board.containsKey(entry.getKey())) {
                fixed.put(entry.getKey(), value.aspect);
            }
        }
        if (fixed.size() < 2) return null;

        Map<Aspect, List<Aspect>> neighbours = buildAspectGraph(playerAspects);
        Candidate best = null;
        for (String start : fixed.keySet()) {
            Candidate candidate = connectAll(start, board, fixed, neighbours);
            if (candidate != null && (best == null || candidate.placements.size() < best.placements.size())) {
                best = candidate;
            }
        }
        if (best == null) return null;

        List<Placement> placements = new ArrayList<>();
        for (Map.Entry<String, Aspect> entry : best.placements.entrySet()) {
            HexUtils.Hex hex = board.get(entry.getKey());
            placements.add(new Placement(hex.q, hex.r, entry.getValue()));
        }
        placements.sort(
            Comparator.comparingInt((Placement p) -> distanceToNearestFixed(p, board, fixed))
                .reversed());

        List<Placement> erases = new ArrayList<>();
        for (Map.Entry<String, ResearchManager.HexEntry> entry : note.hexEntries.entrySet()) {
            ResearchManager.HexEntry value = entry.getValue();
            HexUtils.Hex hex = board.get(entry.getKey());
            if (hex != null && value != null && value.type == 2) {
                erases.add(new Placement(hex.q, hex.r, null));
            }
        }
        return new Plan(fingerprint(note), fixed.values(), erases, placements);
    }

    public static String fingerprint(ResearchNoteData note) {
        if (note == null) return "";
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, ResearchManager.HexEntry> entry : note.hexEntries.entrySet()) {
            ResearchManager.HexEntry value = entry.getValue();
            if (value != null && value.type == 1 && value.aspect != null) {
                parts.add(entry.getKey() + "=" + value.aspect.getTag());
            }
        }
        Collections.sort(parts);
        return String.valueOf(note.key) + "|" + String.join(",", parts);
    }

    private static Candidate connectAll(String start, Map<String, HexUtils.Hex> board, Map<String, Aspect> fixed,
        Map<Aspect, List<Aspect>> neighbours) {
        Map<String, Aspect> tree = new LinkedHashMap<>();
        tree.put(start, fixed.get(start));
        Set<String> remaining = new HashSet<>(fixed.keySet());
        remaining.remove(start);

        while (!remaining.isEmpty()) {
            Path bestPath = null;
            String bestTerminal = null;
            for (String terminal : remaining) {
                Path path = findPath(terminal, board, fixed, tree, neighbours);
                if (path != null && (bestPath == null || path.nodes.size() < bestPath.nodes.size())) {
                    bestPath = path;
                    bestTerminal = terminal;
                }
            }
            if (bestPath == null) return null;
            tree.put(bestTerminal, fixed.get(bestTerminal));
            for (Node node : bestPath.nodes) tree.put(node.position, node.aspect);
            remaining.remove(bestTerminal);
        }

        Map<String, Aspect> placements = new LinkedHashMap<>(tree);
        for (String terminal : fixed.keySet()) placements.remove(terminal);
        return new Candidate(placements);
    }

    private static Path findPath(String terminal, Map<String, HexUtils.Hex> board, Map<String, Aspect> fixed,
        Map<String, Aspect> tree, Map<Aspect, List<Aspect>> aspectGraph) {
        State initial = new State(terminal, fixed.get(terminal));
        Deque<State> queue = new ArrayDeque<>();
        Map<String, State> parents = new HashMap<>();
        Set<String> seen = new HashSet<>();
        queue.add(initial);
        seen.add(initial.key());

        while (!queue.isEmpty()) {
            State current = queue.removeFirst();
            HexUtils.Hex currentHex = board.get(current.position);
            for (int direction = 0; direction < 6; direction++) {
                String nextPosition = currentHex.getNeighbour(direction)
                    .toString();
                if (!board.containsKey(nextPosition)) continue;

                Aspect treeAspect = tree.get(nextPosition);
                if (treeAspect != null && compatible(current.aspect, treeAspect)) {
                    return reconstruct(current, initial, parents);
                }
                if (treeAspect != null || fixed.containsKey(nextPosition)) continue;

                List<Aspect> possible = aspectGraph.get(current.aspect);
                if (possible == null) continue;
                for (Aspect nextAspect : possible) {
                    State next = new State(nextPosition, nextAspect);
                    if (seen.add(next.key())) {
                        parents.put(next.key(), current);
                        queue.addLast(next);
                    }
                }
            }
        }
        return null;
    }

    private static Path reconstruct(State end, State initial, Map<String, State> parents) {
        List<Node> nodes = new ArrayList<>();
        State cursor = end;
        while (!cursor.key()
            .equals(initial.key())) {
            nodes.add(new Node(cursor.position, cursor.aspect));
            cursor = parents.get(cursor.key());
            if (cursor == null) return null;
        }
        Collections.reverse(nodes);
        return new Path(nodes);
    }

    private static Map<Aspect, List<Aspect>> buildAspectGraph(AspectList playerAspects) {
        List<Aspect> all = new ArrayList<>(Aspect.aspects.values());
        Map<Aspect, List<Aspect>> graph = new HashMap<>();
        for (Aspect aspect : all) {
            List<Aspect> linked = new ArrayList<>();
            for (Aspect other : all) {
                if (aspect != other && compatible(aspect, other)) linked.add(other);
            }
            linked.sort((a, b) -> Integer.compare(score(b, playerAspects), score(a, playerAspects)));
            graph.put(aspect, linked);
        }
        return graph;
    }

    private static int score(Aspect aspect, AspectList playerAspects) {
        int amount = playerAspects == null ? 0 : playerAspects.getAmount(aspect);
        return amount * 100 - depth(aspect, new HashSet<Aspect>());
    }

    private static int depth(Aspect aspect, Set<Aspect> visiting) {
        if (aspect == null || aspect.getComponents() == null || !visiting.add(aspect)) return 0;
        Aspect[] components = aspect.getComponents();
        int result = 1 + Math.max(depth(components[0], visiting), depth(components[1], visiting));
        visiting.remove(aspect);
        return result;
    }

    public static boolean compatible(Aspect first, Aspect second) {
        return contains(first, second) || contains(second, first);
    }

    private static boolean contains(Aspect compound, Aspect component) {
        Aspect[] components = compound == null ? null : compound.getComponents();
        return components != null && (components[0] == component || components[1] == component);
    }

    private static int distanceToNearestFixed(Placement placement, Map<String, HexUtils.Hex> board,
        Map<String, Aspect> fixed) {
        HexUtils.Hex point = new HexUtils.Hex(placement.q, placement.r);
        int result = Integer.MAX_VALUE;
        for (String key : fixed.keySet()) result = Math.min(result, HexUtils.getDistance(point, board.get(key)));
        return result;
    }

    public static final class Plan {

        public final String fingerprint;
        public final List<Aspect> fixedAspects;
        public final List<Placement> erases;
        public final List<Placement> placements;

        private Plan(String fingerprint, Collection<Aspect> fixedAspects, List<Placement> erases,
            List<Placement> placements) {
            this.fingerprint = fingerprint;
            this.fixedAspects = new ArrayList<>(fixedAspects);
            this.erases = erases;
            this.placements = placements;
        }
    }

    public static final class Placement {

        public final int q;
        public final int r;
        public final Aspect aspect;

        private Placement(int q, int r, Aspect aspect) {
            this.q = q;
            this.r = r;
            this.aspect = aspect;
        }
    }

    private static final class Candidate {

        private final Map<String, Aspect> placements;

        private Candidate(Map<String, Aspect> placements) {
            this.placements = placements;
        }
    }

    private static final class Path {

        private final List<Node> nodes;

        private Path(List<Node> nodes) {
            this.nodes = nodes;
        }
    }

    private static final class Node {

        private final String position;
        private final Aspect aspect;

        private Node(String position, Aspect aspect) {
            this.position = position;
            this.aspect = aspect;
        }
    }

    private static final class State {

        private final String position;
        private final Aspect aspect;

        private State(String position, Aspect aspect) {
            this.position = position;
            this.aspect = aspect;
        }

        private String key() {
            return position + "|" + aspect.getTag();
        }
    }
}
