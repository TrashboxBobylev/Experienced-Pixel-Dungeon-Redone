/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2020 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2020 Trashbox Bobylev
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
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BurntBag extends TreasureBag {
    {
        image = ItemSpriteSheet.BURNT_BAG;
    }
    @Override
    protected ArrayList<Item> items() {
        ArrayList<Item> items = new ArrayList<>();
        int amount = Random.Int(0, 7);
        if (Dungeon.hero.perks.contains(Perks.Perk.MORE_BAG)) amount *= 1.5f;
        for(int i = 0; i < amount; i++) {
            if (Dungeon.Int(2) == 0)
                items.add(new ChargrilledMeat());
            else
                items.add(Generator.random());
        }
        return items;
    }

    public static Item burningRoll(){
        float worstRoll = 0f;
        for (int i = 0; i < 2; i++){
            float lol = Dungeon.Float();
            if (lol > worstRoll) worstRoll = lol;
        }

        if (worstRoll <= 0.35f)
            return new IdealBag();
        else
            return new BurntBag();
    }
}
