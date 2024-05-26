/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2024 Trashbox Bobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Perks;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.AlchemyBag;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class RingOfWealth extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_WEALTH;
	}

    public static float triesToDrop = Float.MIN_VALUE;
    public static int dropsToRare = Integer.MIN_VALUE;
    public static int level = 0;
	
	public static ArrayList<Item> tryForBonusDrop(int tries){

		//reset (if needed), decrement, and store counts
		if (triesToDrop == Float.MIN_VALUE) {
			triesToDrop = Dungeon.NormalIntRange(4, 12);
			dropsToRare = Dungeon.NormalIntRange(4, 8);
		}

		//now handle reward logic
		ArrayList<Item> drops = new ArrayList<>();

		triesToDrop -= tries;
		while ( triesToDrop <= 0 ){
			if (dropsToRare <= 0){
				int equipBonus = 0;

				Item i;
				do {
					i = genEquipmentDrop(level - 1);
				} while (Challenges.isItemBlocked(i));
				drops.add(i);
				dropsToRare = Random.NormalIntRange(3, 8);
			} else {
				Item i;
				do {
					i = genConsumableDrop(level - 1);
				} while (Challenges.isItemBlocked(i));
				if (Dungeon.hero.perks.contains(Perks.Perk.FISHING_PRO) && Random.Int(4) == 0) i.quantity(i.quantity()*2);
				drops.add(i);
				dropsToRare--;
			}
			triesToDrop += Random.NormalIntRange(0, 20);
		}
		
		return drops;
	}

	//used for visuals
	// 1/2/3 used for low/mid/high tier consumables
	// 3 used for +0-1 equips, 4 used for +2 or higher equips
    public static int latestDropTier = 0;

	public static void showFlareForBonusDrop( Visual vis ){
		switch (latestDropTier){
			default:
				break; //do nothing
			case 1:
				new Flare(6, 20).color(0x00FF00, true).show(vis, 3f);
				break;
			case 2:
				new Flare(6, 24).color(0x00AAFF, true).show(vis, 3.33f);
				break;
			case 3:
				new Flare(6, 28).color(0xAA00FF, true).show(vis, 3.67f);
				break;
			case 4:
				new Flare(6, 32).color(0xFFAA00, true).show(vis, 4f);
				break;
		}
		latestDropTier = 0;
	}
	
	public static Item genConsumableDrop(long level) {
		float roll = Dungeon.Float();
		//60% chance - 4% per level. Starting from +15: 0%
		if (roll < (0.6f - 0.04f * level)) {
			latestDropTier = 1;
			return genLowValueConsumable();
		//30% chance + 2% per level. Starting from +15: 60%-2%*(lvl-15)
		} else if (roll < 0.9f) {
			latestDropTier = 2;
			return genMidValueConsumable();
		//10% chance + 2% per level. Starting from +15: 40%+2%*(lvl-15)
		} else {
			latestDropTier = 3;
			return genHighValueConsumable();
		}
	}

	private static Item genLowValueConsumable(){
		switch (Random.Int(4)){
			case 0: default:
				Item i = new Gold().random();
				return i.quantity(i.quantity()/2);
			case 1:
				return Generator.random(Generator.Category.STONE);
			case 2:
				return Generator.random(Generator.Category.POTION);
			case 3:
				return Generator.random(Generator.Category.SCROLL);
		}
	}

	private static Item genMidValueConsumable(){
		switch (Random.Int(7)){
			case 0: default:
				Item i = genLowValueConsumable();
				return i.quantity(i.quantity()*2);
			case 1:
				i = Generator.randomUsingDefaults(Generator.Category.POTION);
				if (!(i instanceof ExoticPotion)) {
					return Reflection.newInstance(ExoticPotion.regToExo.get(i.getClass()));
				} else {
					return Reflection.newInstance(i.getClass());
				}
			case 2:
				i = Generator.randomUsingDefaults(Generator.Category.SCROLL);
				if (!(i instanceof ExoticScroll)){
					return Reflection.newInstance(ExoticScroll.regToExo.get(i.getClass()));
				} else {
					return Reflection.newInstance(i.getClass());
				}
			case 3:
				return Dungeon.Int(2) == 0 ? new UnstableBrew() : new UnstableSpell();
			case 4:
				return new Bomb();
            case 5:
                return new Food();
		}
	}

	private static Item genHighValueConsumable(){
		switch (Random.Int(5)){
			case 0: default:
				Item i = genMidValueConsumable();
				if (i instanceof Bomb){
					return new Bomb.DoubleBomb();
				} else {
					return i.quantity(i.quantity()*2);
				}
			case 1:
				return new StoneOfEnchantment();
			case 2:
				return Random.Float() < ExoticCrystals.consumableExoticChance() ? new PotionOfDivineInspiration() : new PotionOfExperience();
			case 3:
				return Random.Float() < ExoticCrystals.consumableExoticChance() ? new ScrollOfMetamorphosis() : new ScrollOfTransmutation();
            case 4:
                return new AlchemyBag();
		}
	}

	private static Item genEquipmentDrop( int level ){
		Item result;
		int floorset = (Dungeon.depth)/5;
		switch (Random.Int(5)){
			default: case 0: case 1:
				MeleeWeapon w = Generator.randomWeapon(floorset, true);
				if (!w.hasGoodEnchant() && Dungeon.Int(10) < level)      w.enchant();
				else if (w.hasCurseEnchant())                           w.enchant(null);
				result = w;
				break;
			case 2:
				Armor a = Generator.randomArmor(floorset);
				if (!a.hasGoodGlyph() && Dungeon.Int(10) < level)        a.inscribe();
				else if (a.hasCurseGlyph())                             a.inscribe(null);
				result = a;
				break;
			case 3:
				result = Generator.randomUsingDefaults(Generator.Category.RING);
				break;
			case 4:
				result = Generator.random(Generator.Category.ARTIFACT);
				break;
		}
		//minimum level of sqrt(ringLvl)
		if (result.isUpgradable()){
			if (result.level() < Math.floor(level) / 12){
				result.level((int)Math.floor(level) / 12);
			}
		}
		result.cursed = false;
		result.cursedKnown = true;
		if (result.level() >= 2) {
			latestDropTier = 4;
		} else {
			latestDropTier = 3;
		}
		return result;
	}

    public static final String TRIES_TO_DROP = "tries_to_drop";
    public static final String DROPS_TO_RARE = "drops_to_rare";
    public static final String LEVEL = "level";


    public static void store(Bundle bundle) {
        bundle.put(TRIES_TO_DROP, triesToDrop);
        bundle.put(DROPS_TO_RARE, dropsToRare);
        bundle.put(LEVEL, level);
    }

    public static void restore(Bundle bundle) {
        triesToDrop = bundle.getFloat(TRIES_TO_DROP);
        dropsToRare = bundle.getInt(DROPS_TO_RARE);
        level = bundle.getInt(LEVEL);
    }

}
