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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.BiggerGambleBag;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.GambleBag;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.QualityBag;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

import java.util.ArrayList;

public class ArenaShopLevel extends ShopRoom{
    @Override
    public int minWidth() {
        return 5;
    }
    public int minHeight() {
        return 5;
    }
    public int maxWidth() { return 5; }
    public int maxHeight() { return 5; }

    @Override
    public void paint( Level level ) {

        Painter.fill( level, this, Terrain.WALL );
        Painter.fill( level, this, 1, Terrain.EMPTY_SP );

        placeItems( level );

        for (Door door : connected.values()) {
            door.set( Door.Type.REGULAR );
        }

    }

    protected void placeItems( Level level ){

        if (itemsToSpawn == null){
            itemsToSpawn = new ArrayList<>();
            for (int i = 0; i < 6; i++) itemsToSpawn.add(new GambleBag());
            for (int i = 0; i < 6; i++) itemsToSpawn.add(new BiggerGambleBag());
            for (int i = 0; i < 6; i++) itemsToSpawn.add(new QualityBag());
        }

        Point itemPlacement = new Point(entrance());
        if (itemPlacement.y == top){
            itemPlacement.y++;
        } else if (itemPlacement.y == bottom) {
            itemPlacement.y--;
        } else if (itemPlacement.x == left){
            itemPlacement.x++;
        } else {
            itemPlacement.x--;
        }

        for (Item item : itemsToSpawn) {

            if (itemPlacement.x == left+1 && itemPlacement.y != top+1){
                itemPlacement.y--;
            } else if (itemPlacement.y == top+1 && itemPlacement.x != right-1){
                itemPlacement.x++;
            } else if (itemPlacement.x == right-1 && itemPlacement.y != bottom-1){
                itemPlacement.y++;
            } else {
                itemPlacement.x--;
            }

            int cell = level.pointToCell(itemPlacement);

            if (level.heaps.get( cell ) != null) {
                do {
                    cell = level.pointToCell(random());
                } while (level.heaps.get( cell ) != null || level.findMob( cell ) != null);
            }

            level.drop( item, cell ).type = Heap.Type.FOR_SALE;
        }

    }
}
