package com.xyp.gtnc.Common.packet.building;

import net.minecraft.entity.player.EntityPlayerMP;

import com.xyp.gtnc.Common.building.PixelBuildingManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * 客户端 → 服务端：请求放置一批像素方块（放大建筑外壳的一个分片）。
 * <p>
 * 建筑数据（结构 + 材质像素）只能在客户端算出，故由客户端提取后分片发来。服务端逐格放置 {@link com.xyp.gtnc.Common.building.BlockPixel}
 * 并把 (坐标, 颜色) 记进 {@link com.xyp.gtnc.Common.building.PixelBuildingData}（撤销权威源），同时把颜色同步回该范围内的客户端渲染。
 * <p>
 * 分片：{@code first} 标记本次生成的第一个分片（服务端据此新建/清空该生成器的建筑记录）；坐标以生成器方块为原点的相对坐标存，
 * 服务端加上生成器坐标还原绝对坐标。
 */
public class MessagePlacePixels implements IMessage {

    public int genX, genY, genZ; // 生成器方块坐标
    public boolean first; // 是否本次生成的首个分片
    public boolean last; // 是否末尾分片（放置完成）
    public int[] dx, dy, dz; // 相对生成器的偏移
    public int[] rgb; // 各像素 0xRRGGBB

    public MessagePlacePixels() {}

    public MessagePlacePixels(int genX, int genY, int genZ, boolean first, boolean last, int[] dx, int[] dy, int[] dz,
        int[] rgb) {
        this.genX = genX;
        this.genY = genY;
        this.genZ = genZ;
        this.first = first;
        this.last = last;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.rgb = rgb;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        genX = buf.readInt();
        genY = buf.readInt();
        genZ = buf.readInt();
        first = buf.readBoolean();
        last = buf.readBoolean();
        int n = buf.readInt();
        dx = new int[n];
        dy = new int[n];
        dz = new int[n];
        rgb = new int[n];
        for (int i = 0; i < n; i++) {
            dx[i] = buf.readShort();
            dy[i] = buf.readShort();
            dz[i] = buf.readShort();
            rgb[i] = buf.readInt() & 0xFFFFFF;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(genX);
        buf.writeInt(genY);
        buf.writeInt(genZ);
        buf.writeBoolean(first);
        buf.writeBoolean(last);
        int n = dx == null ? 0 : dx.length;
        buf.writeInt(n);
        for (int i = 0; i < n; i++) {
            // 相对偏移用 short（±32767）足够覆盖放大建筑范围，省带宽。
            buf.writeShort(dx[i]);
            buf.writeShort(dy[i]);
            buf.writeShort(dz[i]);
            buf.writeInt(rgb[i] & 0xFFFFFF);
        }
    }

    public static class Handler implements IMessageHandler<MessagePlacePixels, IMessage> {

        @Override
        public IMessage onMessage(MessagePlacePixels message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            // 1.7.10 SimpleImpl 的服务端包 handler 已在服务端主线程执行，可直接改动世界。
            PixelBuildingManager.placeChunk(player, message);
            return null;
        }
    }
}
