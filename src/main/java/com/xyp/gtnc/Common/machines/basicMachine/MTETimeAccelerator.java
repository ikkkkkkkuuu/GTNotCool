package com.xyp.gtnc.Common.machines.basicMachine;

import static gregtech.api.enums.GTValues.V;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTETieredMachineBlock;
import gregtech.api.render.TextureFactory;

public class MTETimeAccelerator extends MTETieredMachineBlock {

    private int mSpeedTierOverride = -1;
    private int mRadiusTierOverride = -1;

    private static final int RADIUS_BONUS = 2;

    public final int getRadiusTierOverride() {
        if (mRadiusTierOverride == -1) mRadiusTierOverride = mTier + RADIUS_BONUS;
        return mRadiusTierOverride;
    }

    public final int getSpeedTierOverride() {
        if (mSpeedTierOverride == -1) mSpeedTierOverride = mTier;
        return mSpeedTierOverride;
    }

    private int incSpeedTierOverride() {
        mSpeedTierOverride = getSpeedTierOverride() + 1;
        if (mSpeedTierOverride > mTier) mSpeedTierOverride = 1;
        return mSpeedTierOverride;
    }

    private int incRadiusTierOverride() {
        mRadiusTierOverride = getRadiusTierOverride() + 1;
        if (mRadiusTierOverride > mTier + RADIUS_BONUS) mRadiusTierOverride = 1;
        return mRadiusTierOverride;
    }

    private byte mMode = 1; // 0: RandomTicks around, 1: TileEntities with range 1
    private static IIconContainer sIconNormIdle;
    private static IIconContainer sIconNormActive;
    private static IIconContainer sIconTEIdle;
    private static IIconContainer sIconTEActive;
    public static final int[] mAccelerateStatic = { 1, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 2048, 2048, 2048, 2048,
        2048, 2048 };
    private static final int AMPERAGE_NORMAL = 3;
    private static final int AMPERAGE_TE = 6;

    @Override
    public void registerIcons(IIconRegister aBlockIconRegister) {
        super.registerIcons(aBlockIconRegister);
        sIconNormIdle = Textures.BlockIcons.customOptional("iconsets/OVERLAY_ACCELERATOR");
        sIconNormActive = Textures.BlockIcons.customOptional("iconsets/OVERLAY_ACCELERATOR_ACTIVE");
        sIconTEIdle = Textures.BlockIcons.customOptional("iconsets/OVERLAY_ACCELERATOR_TE");
        sIconTEActive = Textures.BlockIcons.customOptional("iconsets/OVERLAY_ACCELERATOR_TE_ACTIVE");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onValueUpdate(byte aValue) {
        mMode = aValue;
    }

    @Override
    public byte getUpdateData() {
        return mMode;
    }

    public MTETimeAccelerator(int pID, String pName, String pNameRegional, int pTier) {
        super(pID, pName, pNameRegional, pTier, 0, "");
    }

    @Override
    public String[] getDescription() {
        // #tr Tooltip_TimeAccelerator_00
        // # §6Max Speed: §e%sx
        // # zh_CN §6加速倍率：§e%sx
        // #tr Tooltip_TimeAccelerator_01
        // # §bTE Mode Radius: §a%d§7~§a%d
        // # zh_CN §bTE模式半径：§a%d§7~§a%d
        // #tr Tooltip_TimeAccelerator_02
        // # §dBlock Mode Radius: §a%d§7~§a%d
        // # zh_CN §d方块模式半径：§a%d§7~§a%d
        // #tr Tooltip_TimeAccelerator_03
        // # §aZero Energy Cost!
        // # zh_CN §a零能耗！
        // #tr Tooltip_TimeAccelerator_04
        // # §eCan accelerate all machines including GT!
        // # zh_CN §e可以加速所有机器 包括GT！
        return new String[] {
            StatCollector.translateToLocalFormatted("Tooltip_TimeAccelerator_00", mAccelerateStatic[mTier]),
            StatCollector.translateToLocalFormatted("Tooltip_TimeAccelerator_01", 1, mTier + RADIUS_BONUS),
            StatCollector.translateToLocalFormatted("Tooltip_TimeAccelerator_02", 1, mTier + RADIUS_BONUS),
            StatCollector.translateToLocal("Tooltip_TimeAccelerator_03"),
            StatCollector.translateToLocal("Tooltip_TimeAccelerator_04"), };
    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    private static final String[] mUnlocalizedModeStr = { "GT5U.word_accelerator.mode.blocks",
        "GT5U.word_accelerator.mode.tile_entities" };

    @Override
    public String[] getInfoData() {
        List<String> tInfoDisplay = new ArrayList<>();
        tInfoDisplay.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.infodata.world_accelerator.mode",
                StatCollector.translateToLocal(mUnlocalizedModeStr[mMode])));
        tInfoDisplay.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.infodata.world_accelerator.speed",
                mAccelerateStatic[getSpeedTierOverride()],
                mAccelerateStatic[mTier]));
        // #tr GTNC.infodata.time_accelerator.free
        // # No energy consumption! Can accelerate GT machines too!
        // # zh_CN 不消耗任何能源！并且可以加速GT机器！
        tInfoDisplay.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.infodata.world_accelerator.consuming",
                getEnergyDemand(getSpeedTierOverride(), getRadiusTierOverride(), mMode == 1)) + "  "
                + StatCollector.translateToLocal("GTNC.infodata.time_accelerator.free"));
        if (mMode == 0) tInfoDisplay.add(
            StatCollector
                .translateToLocalFormatted("GT5U.infodata.world_accelerator.radius", getRadiusTierOverride(), mTier));
        else tInfoDisplay.add(
            StatCollector
                .translateToLocalFormatted("GT5U.infodata.world_accelerator.radius", getRadiusTierOverride(), mTier));
        return tInfoDisplay.toArray(new String[0]);
    }

    public MTETimeAccelerator(String pName, int pTier, int pInvSlotCount, String[] pDescription,
        ITexture[][][] pTextures) {
        super(pName, pTier, pInvSlotCount, pDescription, pTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity pTileEntity) {
        return new MTETimeAccelerator(mName, mTier, mInventory.length, mDescriptionArray, mTextures);
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] pTextures) {
        return null;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity pBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean pActive, boolean pRedstone) {
        if (mMode == 0) {
            return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[mTier][colorIndex + 1], side.offsetY != 0 ? null
                : pActive ? TextureFactory.of(sIconNormActive) : TextureFactory.of(sIconNormIdle) };
        } else {
            return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[mTier][colorIndex + 1], side.offsetY != 0 ? null
                : pActive ? TextureFactory.of(sIconTEActive) : TextureFactory.of(sIconTEIdle) };
        }
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity pBaseMetaTileEntity, int pIndex, ForgeDirection side,
        ItemStack pStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity pBaseMetaTileEntity, int pIndex, ForgeDirection side,
        ItemStack pStack) {
        return false;
    }

    @Override
    public void saveNBTData(NBTTagCompound pNBT) {
        pNBT.setByte("mAccelMode", mMode);
        pNBT.setByte("mSpeed", (byte) getSpeedTierOverride());
        pNBT.setByte("mRadius", (byte) getRadiusTierOverride());
    }

    public long getEnergyDemand(int pSpeedTier, int pRangeTier, boolean pIsAcceleratingTEs) {
        if (pIsAcceleratingTEs) return V[pSpeedTier] * AMPERAGE_TE;
        float multiplier = 100.0F / (float) mTier * (float) pRangeTier / 100.0F;
        long demand = V[pSpeedTier] * AMPERAGE_NORMAL;
        return (int) (demand * multiplier);
    }

    @Override
    public void loadNBTData(NBTTagCompound pNBT) {
        mMode = pNBT.getByte("mAccelMode");
        if (pNBT.hasKey("mSpeed")) mSpeedTierOverride = pNBT.getByte("mSpeed");
        if (pNBT.hasKey("mRadius")) mRadiusTierOverride = pNBT.getByte("mRadius");
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean isEnetInput() {
        return true;
    }

    @Override
    public boolean isInputFacing(ForgeDirection side) {
        return true;
    }

    @Override
    public boolean isTeleporterCompatible() {
        return false;
    }

    @Override
    public long maxEUStore() {
        return 512 + V[mTier] * 50;
    }

    @Override
    public long maxEUInput() {
        return V[mTier];
    }

    @Override
    public long maxAmperesIn() {
        return 8;
    }

    @Override
    public boolean onWrenchRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer pPlayer, float aX,
        float aY, float aZ, ItemStack aTool) {
        incSpeedTierOverride();
        getBaseMetaTileEntity().issueTileUpdate();
        markDirty();
        if (pPlayer instanceof EntityPlayerMP playerMP) {
            playerMP.addChatMessage(
                new ChatComponentTranslation(
                    "tt.block.world_accelerator.set_speed",
                    mAccelerateStatic[getSpeedTierOverride()]));
        }
        return true;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer pPlayer, float pX, float pY, float pZ,
        ItemStack aTool) {
        if (pPlayer.isSneaking()) {
            incRadiusTierOverride();
            markDirty();
            if (pPlayer instanceof EntityPlayerMP playerMP) {
                playerMP.addChatMessage(
                    new ChatComponentTranslation("tt.block.world_accelerator.set_range", getRadiusTierOverride()));
            }
        } else {
            mMode = (byte) (mMode == 0x00 ? 0x01 : 0x00);
            markDirty();
            if (pPlayer instanceof EntityPlayerMP playerMP) {
                playerMP.addChatMessage(
                    new ChatComponentTranslation(
                        "tt.block.world_accelerator.set_mode",
                        new ChatComponentTranslation(mUnlocalizedModeStr[mMode])));
            }
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity pBaseMetaTileEntity, long pTick) {
        if (!pBaseMetaTileEntity.isServerSide()) return;
        if (!pBaseMetaTileEntity.isAllowedToWork()) return;

        World tWorld = pBaseMetaTileEntity.getWorld();
        if (mMode == 0) {
            if (pTick % 20 == 0) {
                doAccelerateNormalBlocks(pBaseMetaTileEntity, tWorld);
            }
        } else {
            doAccelerateTileEntities(pBaseMetaTileEntity, tWorld);
        }
    }

    private void doAccelerateTileEntities(IGregTechTileEntity pBaseMetaTileEntity, World pWorld) {
        if (!pBaseMetaTileEntity.isActive()) {
            getBaseMetaTileEntity().setActive(true);
        }

        int tX = pBaseMetaTileEntity.getXCoord();
        int tY = pBaseMetaTileEntity.getYCoord();
        int tZ = pBaseMetaTileEntity.getZCoord();
        int radius = mMode == 1 ? getRadiusTierOverride() : 1;

        final long tMaxTime = System.nanoTime() + 25_000_000;
        final int iterations = mAccelerateStatic[getSpeedTierOverride()];

        for (int xi = tX - radius; xi <= tX + radius; xi++) {
            for (int yi = Math.max(tY - radius, 0); yi <= Math.min(tY + radius, 255); yi++) {
                for (int zi = tZ - radius; zi <= tZ + radius; zi++) {
                    if (xi == tX && yi == tY && zi == tZ) continue; // skip self
                    TileEntity tTile = pWorld.getTileEntity(xi, yi, zi);
                    if (tTile == null || tTile.isInvalid() || !tTile.canUpdate()) continue;
                    // Skip other time accelerators to prevent mutual recursive acceleration
                    if (tTile instanceof IGregTechTileEntity
                        && ((IGregTechTileEntity) tTile).getMetaTileEntity() instanceof MTETimeAccelerator) continue;
                    // Skip blacklisted tiles (e.g. AE2 network blocks) — repeatedly ticking them lags hard
                    if (isBlacklisted(tTile)) continue;

                    // Refund EU *and* steam for all accelerated ticks except the last, so each machine only
                    // pays normal running cost. Steam machines burn mStoredSteam (not EU) as their process gate
                    // (drainEnergyForProcess); refunding only EU let them truly drain N steam per real tick, so
                    // above ~128x they run dry mid-loop, fail the drain, stutter, and stop advancing — looking
                    // like "no acceleration". Refunding steam too keeps them running at any multiplier.
                    for (int j = 0; j < iterations - 1; j++) {
                        long savedEU = getGTEU(tTile);
                        long savedSteam = getGTSteam(tTile);
                        tTile.updateEntity();
                        restoreGTEU(tTile, savedEU);
                        restoreGTSteam(tTile, savedSteam);
                        if (System.nanoTime() > tMaxTime) return;
                    }
                    // Last tick runs normally — consumes normal energy
                    tTile.updateEntity();
                    if (System.nanoTime() > tMaxTime) return;
                }
            }
        }
    }

    private static long getGTEU(TileEntity tTile) {
        return tTile instanceof IGregTechTileEntity gtTE ? gtTE.getStoredEU() : -1;
    }

    private static long getGTSteam(TileEntity tTile) {
        return tTile instanceof IGregTechTileEntity gtTE ? gtTE.getStoredSteam() : -1;
    }

    private static void restoreGTSteam(TileEntity tTile, long savedSteam) {
        if (savedSteam < 0) return;
        IGregTechTileEntity gtTE = (IGregTechTileEntity) tTile;
        long current = gtTE.getStoredSteam();
        if (current < savedSteam) gtTE.increaseStoredSteam(savedSteam - current, true);
    }

    /**
     * True if this tile should not be accelerated, based on the configured class-name prefix blacklist
     * (see {@link com.xyp.gtnc.Config.Config#timeAcceleratorTileBlacklist}). Ticking AE2 network blocks
     * up to thousands of times per tick causes severe lag, so they are excluded by default.
     * <p>
     * For GregTech machines the world tile is always {@code BaseMetaTileEntity}; the actual machine logic
     * lives in its {@link MetaTileEntity}. So besides the tile's own class name we also match against the
     * wrapped MetaTileEntity's class name, otherwise a prefix like {@code com.xyp.gtnc.} could never match
     * a GT machine (its tile is a GregTech class, not ours).
     */
    private static boolean isBlacklisted(TileEntity tTile) {
        String[] blacklist = com.xyp.gtnc.Config.Config.timeAcceleratorTileBlacklist;
        if (blacklist == null || blacklist.length == 0) return false;
        String tileClassName = tTile.getClass()
            .getName();
        String mteClassName = null;
        if (tTile instanceof IGregTechTileEntity gtTE) {
            final var mte = gtTE.getMetaTileEntity();
            if (mte != null) mteClassName = mte.getClass()
                .getName();
        }
        for (String prefix : blacklist) {
            if (prefix == null || prefix.isEmpty()) continue;
            if (tileClassName.startsWith(prefix)) return true;
            if (mteClassName != null && mteClassName.startsWith(prefix)) return true;
        }
        return false;
    }

    private static void restoreGTEU(TileEntity tTile, long savedEU) {
        if (savedEU < 0) return;
        long current = ((IGregTechTileEntity) tTile).getStoredEU();
        if (current < savedEU) ((IGregTechTileEntity) tTile).increaseStoredEnergyUnits(savedEU - current, true);
    }

    private void doAccelerateNormalBlocks(IGregTechTileEntity pBaseMetaTileEntity, World pWorld) {
        if (!pBaseMetaTileEntity.isActive()) {
            getBaseMetaTileEntity().setActive(true);
        }

        Random rnd = new Random();
        int tX = pBaseMetaTileEntity.getXCoord();
        int tY = pBaseMetaTileEntity.getYCoord();
        int tZ = pBaseMetaTileEntity.getZCoord();
        int radius = getRadiusTierOverride();

        for (int xi = tX - radius; xi <= tX + radius; xi++) {
            for (int yi = Math.max(tY - radius, 0); yi <= Math.min(tY + radius, 255); yi++) {
                for (int zi = tZ - radius; zi <= tZ + radius; zi++) {
                    tryTickBlock(pWorld, xi, yi, zi, rnd);
                }
            }
        }
    }

    private void tryTickBlock(World pWorld, int pX, int pY, int pZ, Random pRnd) {
        for (int j = 0; j < getSpeedTierOverride(); j++) {
            Block tBlock = pWorld.getBlock(pX, pY, pZ);
            if (tBlock.getTickRandomly()) {
                tBlock.updateTick(pWorld, pX, pY, pZ, pRnd);
            }
        }
    }

    @Override
    public NBTTagCompound getDescriptionData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("acceleration", getSpeedTierOverride());
        return tag;
    }

    @Override
    public void onDescriptionPacket(NBTTagCompound data) {
        this.mSpeedTierOverride = data.getInteger("acceleration");
    }
}
