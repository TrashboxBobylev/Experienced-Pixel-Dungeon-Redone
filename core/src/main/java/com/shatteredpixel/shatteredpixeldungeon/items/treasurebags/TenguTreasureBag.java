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
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TomeOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.TenguShuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class TenguTreasureBag extends TreasureBag {

    {
        image = ItemSpriteSheet.TENGU_BAG;
    }

    @Override
    protected ArrayList<Item> items() {
        ArrayList<Item> items = new ArrayList<>();
        if (!Dungeon.LimitedDrops.TOME_OF_MASTERY.dropped()){
            items.add(new TomeOfMastery());
            Dungeon.LimitedDrops.TOME_OF_MASTERY.drop();
        }
        items.add(new Gold().quantity(Random.Int( 450 + Dungeon.escalatingDepth() * 75, 900 + Dungeon.escalatingDepth() * 85 )));
        items.add(new Shuriken().upgrade(1).quantity(Random.Int(2, 10)));
        if (Dungeon.cycle > 0){
            items.add(new TenguShuriken().random().quantity(Random.Int(1, 3)));
            for (int i = 0; i < 8; i++) items.add(Generator.randomUsingDefaults(Generator.Category.MISSILE).random());
            for (int i = 0; i < 2; i++) items.add(Generator.randomUsingDefaults(Generator.Category.WEAPON).random());
        }
        return items;
    }
}
