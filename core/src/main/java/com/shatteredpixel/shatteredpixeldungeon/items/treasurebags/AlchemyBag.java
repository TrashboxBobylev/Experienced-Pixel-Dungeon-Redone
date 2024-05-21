/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2019-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.treasurebags;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Perks;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.AquaBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlchemyBag extends TreasureBag {
    {
        image = ItemSpriteSheet.ALCHEMY_BAG;
    }

    @Override
    protected ArrayList<Item> items() {
        ArrayList<Item> items = new ArrayList<>();
        List<Class<? extends Item>> possibleItems = Arrays.asList(
                BlizzardBrew.class, InfernalBrew.class, ShockingBrew.class,
                ElixirOfDragonsBlood.class, ElixirOfHoneyedHealing.class, ElixirOfIcyTouch.class, ElixirOfToxicEssence.class,
                Alchemize.class, AquaBrew.class, BeaconOfReturning.class, ElixirOfFeatherFall.class, CurseInfusion.class,
                PhaseShift.class, Recycle.class, Vampirism.class, SummonElemental.class, TelekineticGrab.class, WildEnergy.class,
                Firebomb.class, Flashbang.class, FrostBomb.class, HolyBomb.class, Noisemaker.class,
                RegrowthBomb.class, ShockBomb.class, WoollyBomb.class, ArcaneBomb.class, ShrapnelBomb.class);
        int amount = Random.Int(1, 4);
        if (Dungeon.hero.perks.contains(Perks.Perk.MORE_BAG)) amount *= 1.5f;
        items.add(Reflection.newInstance(Random.element(possibleItems)).quantity(amount));
        return items;
    }

    @Override
    public long value() {
        return 55 * quantity;
    }
}
