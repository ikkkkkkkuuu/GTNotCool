package com.xyp.gtnc.Common.building.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

/**
 * 客户端：提取 GT 机器（MTE：控制器 / 各种仓）某一面的<b>真实多层材质</b>并合成成一张像素图。
 * <p>
 * <b>为何需要它</b>：GT 的 {@code BlockMachines.getIcon(side, meta)} 对所有机器硬编码返回 {@code MACHINE_LV_SIDE}
 * 占位图，且拿不到「正面控制屏」等 overlay。真实材质在 {@code MTE.getTexture(gte, side, facing, ...)} 返回的
 * {@link ITexture}[] 里，每层是 {@code GTRenderedTexture}（持 {@link IIconContainer} + 染色 RGBa）或
 * {@code GTMultiTextureRender}（多层组合）。这里反射取出各层的底图 {@link IIconContainer#getIcon()} 与 overlay
 * {@link IIconContainer#getOverlayIcon()}，按各自 {@code getIconColor} 染色、alpha 合成成一张 {@link PixelSampler.Sprite}。
 * <p>
 * <b>脆弱性</b>：依赖 GT 内部字段名（{@code mIconContainer}/{@code mTextures}/{@code mRGBa}）反射。GT 改名则失效，
 * 但全程 try/catch，失败返回 null → 调用方回退到 {@code block.getIcon}，不崩不变白。
 * <p>
 * <b>仅客户端</b>。
 */
@SideOnly(Side.CLIENT)
public final class MTETextureExtractor {

    private MTETextureExtractor() {}

    // GTRenderedTexture：单图标 + overlay + 染色
    private static Field fRT_icon, fRT_rgba;
    // GTSidedTextureRender / GTMultiTextureRender：子纹理数组
    private static Field fSided_textures, fMulti_textures;
    // GTCopiedBlockTextureRender / GTCopiedCTMBlockTexture：复制方块材质
    private static Field fCopy_block, fCopy_side, fCopy_meta;
    private static Field fCTM_block, fCTM_side, fCTM_meta;
    private static Class<?> cRendered, cSided, cMulti, cCopy, cCTM;
    private static boolean reflectResolved, reflectOk;

    private static void resolveReflection() {
        if (reflectResolved) return;
        reflectResolved = true;
        try {
            cRendered = Class.forName("gregtech.common.render.GTRenderedTexture");
            fRT_icon = accessible(findField(cRendered, "mIconContainer"));
            fRT_rgba = accessible(findField(cRendered, "mRGBa"));

            cSided = tryClass("gregtech.common.render.GTSidedTextureRender");
            if (cSided != null) fSided_textures = accessible(findField(cSided, "mTextures"));

            cMulti = tryClass("gregtech.common.render.GTMultiTextureRender");
            if (cMulti != null) fMulti_textures = accessible(findField(cMulti, "mTextures"));

            cCopy = tryClass("gregtech.common.render.GTCopiedBlockTextureRender");
            if (cCopy != null) {
                fCopy_block = accessible(findField(cCopy, "mBlock"));
                fCopy_side = accessible(findField(cCopy, "mSide"));
                fCopy_meta = accessible(findField(cCopy, "mMeta"));
            }
            cCTM = tryClass("gregtech.common.render.GTCopiedCTMBlockTexture");
            if (cCTM != null) {
                fCTM_block = accessible(findField(cCTM, "mBlock"));
                fCTM_side = accessible(findField(cCTM, "mSide"));
                fCTM_meta = accessible(findField(cCTM, "mMeta"));
            }
            reflectOk = fRT_icon != null;
        } catch (Throwable t) {
            reflectOk = false;
        }
    }

    private static Class<?> tryClass(String n) {
        try {
            return Class.forName(n);
        } catch (Throwable t) {
            return null;
        }
    }

    private static Field accessible(Field f) {
        if (f != null) f.setAccessible(true);
        return f;
    }

    private static Field findField(Class<?> cls, String name) {
        Class<?> c = cls;
        while (c != null) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        return null;
    }

    /** 一层贴到某面后的像素（0xAARRGGBB，行优先），供顺序合成。 */
    private static final class LayerPixels {

        final int w, h;
        final int[] argb;

        LayerPixels(int w, int h, int[] argb) {
            this.w = w;
            this.h = h;
            this.argb = argb;
        }
    }

    /**
     * 取 MTE 某面的合成材质。side 用方块面序号（0=下 1=上 2=北 3=南 4=西 5=东）。失败返回 null（调用方回退）。
     */
    public static PixelSampler.Sprite sample(IGregTechTileEntity gte, int side) {
        resolveReflection();
        if (!reflectOk || gte == null) return null;
        try {
            IMetaTileEntity mte = gte.getMetaTileEntity();
            if (mte == null) return null;
            ForgeDirection dir = ForgeDirection.getOrientation(side);
            ForgeDirection facing = gte.getFrontFacing();
            // active=true：取「运行中」材质（控制屏亮起 / 活动 overlay），而非默认待机外观。redstone=false。
            ITexture[] layers = mte.getTexture(gte, dir, facing, -1, true, false);
            if (layers == null) return null;

            List<LayerPixels> collected = new ArrayList<>();
            for (ITexture t : layers) {
                collectLayer(t, side, collected, 0);
            }
            if (collected.isEmpty()) return null;

            // 按序 alpha 合成（先底层后上层）。
            int W = -1, H = -1;
            int[] acc = null;
            for (LayerPixels lp : collected) {
                if (acc == null) {
                    W = lp.w;
                    H = lp.h;
                    acc = new int[W * H];
                }
                if (lp.w != W || lp.h != H) continue;
                blend(acc, lp.argb, W, H);
            }
            if (acc == null) return null;
            int[] out = new int[W * H];
            for (int i = 0; i < out.length; i++) {
                int a = (acc[i] >>> 24) & 0xFF;
                out[i] = a < 128 ? -1 : (acc[i] & 0xFFFFFF);
            }
            return new PixelSampler.Sprite(W, H, out);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * 递归把一个 ITexture 在指定面上的可见层展开成 LayerPixels，追加到 out（保序）。
     * 覆盖四种类型：Multi(全层叠加)、Sided(只取该面子纹理)、Rendered(底图+overlay)、Copied/CTM(复制方块材质)。
     */
    private static void collectLayer(ITexture t, int side, List<LayerPixels> out, int depth) throws Exception {
        if (t == null || depth > 8) return;
        Class<?> cls = t.getClass();

        if (cMulti != null && cMulti.isAssignableFrom(cls)) {
            ITexture[] subs = (ITexture[]) fMulti_textures.get(t);
            if (subs != null) for (ITexture s : subs) collectLayer(s, side, out, depth + 1);
            return;
        }
        if (cSided != null && cSided.isAssignableFrom(cls)) {
            ITexture[] subs = (ITexture[]) fSided_textures.get(t);
            // Sided 每面一个子纹理，只取当前面（索引即方块面序号）。
            if (subs != null && side >= 0 && side < subs.length) collectLayer(subs[side], side, out, depth + 1);
            return;
        }
        if (cRendered != null && cRendered.isAssignableFrom(cls)) {
            IIconContainer icon = (IIconContainer) fRT_icon.get(t);
            if (icon == null) return;
            short[] rgba = fRT_rgba != null ? (short[]) fRT_rgba.get(t) : null;
            // 底图 + overlay 各作为一层（overlay 叠在底图上）。
            LayerPixels base = iconToLayer(safeIcon(icon, false), rgba);
            if (base != null) out.add(base);
            LayerPixels over = iconToLayer(safeIcon(icon, true), rgba);
            if (over != null) out.add(over);
            return;
        }
        if (cCopy != null && cCopy.isAssignableFrom(cls)) {
            addCopied(fCopy_block.get(t), fCopy_side.getByte(t), fCopy_meta.getInt(t), side, out);
            return;
        }
        if (cCTM != null && cCTM.isAssignableFrom(cls)) {
            addCopied(fCTM_block.get(t), fCTM_side.getByte(t), fCTM_meta.getInt(t), side, out);
        }
    }

    /** 复制方块材质层：copiedSide==6 表示按被贴的面取图，否则固定用 copiedSide。 */
    private static void addCopied(Object blockObj, byte copiedSide, int meta, int side, List<LayerPixels> out) {
        if (!(blockObj instanceof net.minecraft.block.Block block)) return;
        int useSide = copiedSide == 6 ? side : copiedSide;
        PixelSampler.Sprite s = PixelSampler.sample(block, meta, useSide);
        LayerPixels lp = spriteToLayer(s, null);
        if (lp != null) out.add(lp);
    }

    private static LayerPixels iconToLayer(IIcon icon, short[] rgba) {
        if (icon == null) return null;
        return spriteToLayer(PixelSampler.sampleIcon(icon), rgba);
    }

    /** Sprite(0xRRGGBB/-1) → LayerPixels(0xAARRGGBB)，并乘染色 rgba。 */
    private static LayerPixels spriteToLayer(PixelSampler.Sprite s, short[] rgba) {
        if (s == null) return null;
        float tr = 1f, tg = 1f, tb = 1f;
        if (rgba != null && rgba.length >= 3) {
            tr = (rgba[0] & 0xFF) / 255f;
            tg = (rgba[1] & 0xFF) / 255f;
            tb = (rgba[2] & 0xFF) / 255f;
        }
        int[] argb = new int[s.width * s.height];
        for (int i = 0; i < argb.length; i++) {
            int c = s.rgb[i];
            if (c < 0) {
                argb[i] = 0; // 透明
            } else {
                int r = clamp((int) (((c >> 16) & 0xFF) * tr));
                int g = clamp((int) (((c >> 8) & 0xFF) * tg));
                int b = clamp((int) ((c & 0xFF) * tb));
                argb[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }
        return new LayerPixels(s.width, s.height, argb);
    }

    private static IIcon safeIcon(IIconContainer icon, boolean overlay) {
        try {
            return overlay ? icon.getOverlayIcon() : icon.getIcon();
        } catch (Throwable t) {
            return null;
        }
    }

    /** alpha 合成一层（0xAARRGGBB，染色已预乘）到 acc。 */
    private static void blend(int[] acc, int[] src, int W, int H) {
        for (int idx = 0; idx < W * H; idx++) {
            int s = src[idx];
            int sa = (s >>> 24) & 0xFF;
            if (sa == 0) continue;
            int sr = (s >> 16) & 0xFF, sg = (s >> 8) & 0xFF, sb = s & 0xFF;
            int dst = acc[idx];
            int da = (dst >>> 24) & 0xFF;
            if (da == 0 || sa == 255) {
                acc[idx] = 0xFF000000 | (sr << 16) | (sg << 8) | sb;
            } else {
                float af = sa / 255f;
                int dr = (dst >> 16) & 0xFF, dg = (dst >> 8) & 0xFF, db = dst & 0xFF;
                int rr = (int) (sr * af + dr * (1 - af));
                int rg = (int) (sg * af + dg * (1 - af));
                int rb = (int) (sb * af + db * (1 - af));
                acc[idx] = 0xFF000000 | (clamp(rr) << 16) | (clamp(rg) << 8) | clamp(rb);
            }
        }
    }

    private static int clamp(int v) {
        return v < 0 ? 0 : (v > 255 ? 255 : v);
    }
}
