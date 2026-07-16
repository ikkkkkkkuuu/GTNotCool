package com.xyp.gtnc.Common.building.client;

import com.xyp.gtnc.Common.building.PixelColorStore;
import com.xyp.gtnc.Common.packet.building.MessageSyncPixelColors;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 客户端：把服务端同步来的像素颜色写入 {@link PixelColorStore}，并请求相关区块重建渲染。
 */
@SideOnly(Side.CLIENT)
public final class BuildingColorSyncClient {

    private BuildingColorSyncClient() {}

    public static void apply(MessageSyncPixelColors msg) {
        int n = msg.x == null ? 0 : msg.x.length;
        if (n == 0) return;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int x = msg.x[i], y = msg.y[i], z = msg.z[i];
            if (msg.clear) {
                PixelColorStore.remove(msg.dim, x, y, z);
            } else {
                PixelColorStore.put(msg.dim, x, y, z, msg.rgb[i]);
            }
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }
        // 请求这批坐标所在区块重建（colorMultiplier 会被重新采样）。
        PixelColorStore.requestRenderUpdate(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
