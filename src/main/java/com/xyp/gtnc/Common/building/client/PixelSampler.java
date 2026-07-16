package com.xyp.gtnc.Common.building.client;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 客户端：读取方块材质像素。
 * <p>
 * <b>为何不用 sprite 内存像素</b>：GTNH 为省内存，材质拼进图集后会丢弃 CPU 侧像素数组
 * （{@code TextureAtlasSprite.getFrameTextureData(0)} 访问空 list → IndexOutOfBounds）。因此这里改为
 * <b>直接从资源包读原始 PNG 文件</b>：sprite 的 {@code getIconName()} = {@code domain:path}
 * （如 {@code gregtech:iconsets/MACHINE_HEATPROOFCASING}）→ 资源
 * {@code assets/<domain>/textures/blocks/<path>.png}，用 {@code IResourceManager} 读取、{@code ImageIO} 解析像素。
 * 结果按 iconName 缓存（同一材质只读一次盘）。
 * <p>
 * <b>像素格式</b>：ImageIO 读出 {@code 0xAARRGGBB}，本类统一转 {@code 0xRRGGBB}；alpha<128 记 -1（透明）。
 * <p>
 * <b>仅客户端</b>：材质与资源管理器只在客户端有。
 */
@SideOnly(Side.CLIENT)
public final class PixelSampler {

    private PixelSampler() {}

    /** 读到的一格材质像素结果。 */
    public static final class Sprite {

        public final int width, height;
        /** 长度 width*height 的 0xRRGGBB 数组（行优先）；alpha<128 的像素置为 -1。 */
        public final int[] rgb;

        Sprite(int width, int height, int[] rgb) {
            this.width = width;
            this.height = height;
            this.rgb = rgb;
        }
    }

    /** iconName → Sprite 缓存（null 表示读过但失败，避免反复重试）。 */
    private static final Map<String, Sprite> CACHE = new HashMap<>();

    /**
     * 读取方块某面材质的像素。失败返回 null。
     *
     * @param side 方块面（0=下 1=上 2=北 3=南 4=西 5=东），与 {@link Block#getIcon(int, int)} 一致。
     */
    public static Sprite sample(Block block, int meta, int side) {
        IIcon icon;
        try {
            icon = block.getIcon(side, meta);
        } catch (Throwable t) {
            return null;
        }
        return sampleIcon(icon);
    }

    /** 读取任意 {@link IIcon} 的像素（供 MTE 多层材质合成复用）。失败返回 null。 */
    public static Sprite sampleIcon(IIcon icon) {
        if (!(icon instanceof TextureAtlasSprite sprite)) return null;
        String iconName = sprite.getIconName();
        if (iconName == null || iconName.isEmpty()) return null;

        if (CACHE.containsKey(iconName)) return CACHE.get(iconName);

        Sprite result = loadFromResource(iconName);
        CACHE.put(iconName, result);
        return result;
    }

    /** iconName（domain:path）→ 读 assets/domain/textures/blocks/path.png。 */
    private static Sprite loadFromResource(String iconName) {
        try {
            String domain, path;
            int colon = iconName.indexOf(':');
            if (colon >= 0) {
                domain = iconName.substring(0, colon);
                path = iconName.substring(colon + 1);
            } else {
                domain = "minecraft";
                path = iconName;
            }
            ResourceLocation loc = new ResourceLocation(domain, "textures/blocks/" + path + ".png");
            InputStream in = Minecraft.getMinecraft()
                .getResourceManager()
                .getResource(loc)
                .getInputStream();
            BufferedImage img;
            try {
                img = ImageIO.read(in);
            } finally {
                in.close();
            }
            if (img == null) return null;
            int w = img.getWidth();
            // 动画材质是纵向多帧（高 = 宽的整数倍），只取第一帧（前 w 行）。
            int h = img.getHeight();
            int frameH = w; // 假定方形帧
            if (h >= frameH && h % frameH == 0) {
                h = frameH;
            }
            int[] argb = new int[w * h];
            img.getRGB(0, 0, w, h, argb, 0, w);
            int[] out = new int[w * h];
            for (int i = 0; i < w * h; i++) {
                int a = (argb[i] >>> 24) & 0xFF;
                out[i] = a < 128 ? -1 : (argb[i] & 0xFFFFFF);
            }
            return new Sprite(w, h, out);
        } catch (Throwable t) {
            return null;
        }
    }

}
