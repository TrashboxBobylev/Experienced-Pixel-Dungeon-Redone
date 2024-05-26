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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

public abstract class TreasureBag extends Item {
    private static final String AC_OPEN = "OPEN";
    {
        defaultAction = AC_OPEN;
        stackable = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions =  super.actions(hero);
        actions.add(AC_OPEN);
        return actions;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public long value() {
        return quantity * 3000;
    }

    protected abstract ArrayList<Item> items();

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_OPEN)){
            detach(hero.belongings.backpack);
            ArrayList<Item> items = items();
            for (Item item: items){
                if (item != null) {
                    if (!item.doPickUp(hero, hero.pos, 0f)) {
                        Dungeon.level.drop(item, hero.pos).sprite.drop();
                    } else {
                        hero.spend(-Item.TIME_TO_PICK_UP);
                        GLog.i(Messages.get(Hero.class, "you_now_have", item.name()));
                    }
                }
            }
            hero.spendAndNext(Actor.TICK);
        }
    }
}
