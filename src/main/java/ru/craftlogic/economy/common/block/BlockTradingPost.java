package ru.craftlogic.economy.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.craftlogic.economy.common.tile.TileEntityTradingPost;

import javax.annotation.Nullable;

public class BlockTradingPost extends BlockContainer {
    public BlockTradingPost() {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setHardness(2);
        setResistance(2000);
        setLightOpacity(0);
        setRegistryName("trading_post");
        setTranslationKey("trading_post");
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess storage, IBlockState state, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTradingPost();
    }
}
