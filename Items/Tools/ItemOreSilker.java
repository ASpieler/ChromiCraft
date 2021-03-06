/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.DragonAPI.Interfaces.Item.ToolSprite;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class ItemOreSilker extends ItemChromaTool implements ToolSprite {

	public ItemOreSilker(int index) {
		super(index);
		this.setMaxDamage(180);
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass) {
		return toolClass.toLowerCase(Locale.ENGLISH).contains("pick") ? Math.max(3, Items.diamond_pickaxe.getHarvestLevel(stack, toolClass)) : super.getHarvestLevel(stack, toolClass);
	}

	@Override
	public boolean canHarvestBlock(Block b, ItemStack is) {
		return Items.diamond_pickaxe.canHarvestBlock(b, is);
	}

	@Override
	public final IIcon getIconFromDamage(int dmg) { //To get around a bug in backtools
		return Items.stone_pickaxe.getIconFromDamage(0);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack is, int x, int y, int z, EntityPlayer ep)
	{
		if (ep.capabilities.isCreativeMode)
			return false;
		World world = ep.worldObj;
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (id == Blocks.glowstone || ReikaOreHelper.isVanillaOre(id) || ModOreList.isModOre(id, meta)) {
			if (id.canSilkHarvest(world, ep, x, y, z, meta)) {
				this.dropSilkedOre(world, x, y, z, id, meta);
				is.damageItem(1, ep);
				return true;
			}
		}
		return false;
	}

	private void dropSilkedOre(World world, int x, int y, int z, Block b, int meta) {
		world.setBlockToAir(x, y, z);
		ReikaSoundHelper.playBreakSound(world, x, y, z, b);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			ReikaRenderHelper.spawnDropParticles(world, x, y, z, b, meta);
		}
		ReikaItemHelper.dropItem(world, x+itemRand.nextDouble(), y+itemRand.nextDouble(), z+itemRand.nextDouble(), new ItemStack(b, 1, meta));
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		return ForgeHooks.isToolEffective(stack, block, meta) ? 4 : super.getDigSpeed(stack, block, meta);
	}

	@Override
	public Set<String> getToolClasses(ItemStack stack) {
		Set set = new HashSet();
		set.add("pickaxe");
		set.add("pickax");
		return set;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack is, World world, Block b, int x, int y, int z, EntityLivingBase elb) {
		if (b.getBlockHardness(world, x, y, z) > 0) {
			is.damageItem(1, elb);
		}
		return true;
	}

}
