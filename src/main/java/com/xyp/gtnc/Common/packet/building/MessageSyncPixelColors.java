package com.xyp.gtnc.Common.packet.building;

import com.xyp.gtnc.Common.building.client.BuildingColorSyncClient;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

/**
 * 服务端 → 客户端：同步一批像素方块的颜色（用于渲染），或指示清除一批坐标的颜色（撤销）。
 * <p>
 * 绝对坐标 + 维度。{@code clear=true} 时表示移除这些坐标的颜色（撤销后清理），否则写入颜色。收到后写入
 * {@link com.xyp.gtnc.Common.building.PixelColorStore} 并请求这些坐标所在区块重建渲染。
 */
public class MessageSyncPixelColors implements IMessage {

    public int dim;
    public boolean clear;
    public int[] x, y, z;
    public int[] rgb; // clear=true 时忽略

    public MessageSyncPixelColors() {}

    public MessageSyncPixelColors(int dim, boolean clear, int[] x, int[] y, int[] z, int[] rgb) {
        this.dim = dim;
        this.clear = clear;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rgb = rgb;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        clear = buf.readBoolean();
        int n = buf.readInt();
        x = new int[n];
        y = new int[n];
        z = new int[n];
        rgb = new int[n];
        for (int i = 0; i < n; i++) {
            x[i] = buf.readInt();
            y[i] = buf.readInt();
            z[i] = buf.readInt();
            if (!clear) rgb[i] = buf.readInt() & 0xFFFFFF;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeBoolean(clear);
        int n = x == null ? 0 : x.length;
        buf.writeInt(n);
        for (int i = 0; i < n; i++) {
            buf.writeInt(x[i]);
            buf.writeInt(y[i]);
            buf.writeInt(z[i]);
            if (!clear) buf.writeInt(rgb[i] & 0xFFFFFF);
        }
    }

    public static class Handler implements IMessageHandler<MessageSyncPixelColors, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageSyncPixelColors message, MessageContext ctx) {
            // 1.7.10 SimpleImpl 的包 handler 跑在 netty 网络线程；写颜色表 + 请求区块重建（markBlockRangeForRenderUpdate
            // 只能主线程调）必须在客户端主线程执行，否则重建不生效、跨线程写入渲染线程也读不到 → 恒为白色。
            net.minecraft.client.Minecraft.getMinecraft()
                .func_152344_a(() -> BuildingColorSyncClient.apply(message));
            return null;
        }
    }
}
