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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;

import java.util.ArrayList;

public class BlackPsycheChest extends Item {
    {
        image = ItemSpriteSheet.EBONY_CHEST;
        cursed = true;
        cursedKnown = true;
        stackable = true;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    private static final String AC_ACCESS = "ACCESS";
    private static final String AC_RESET = "RESET";

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ACCESS);
        actions.add(AC_RESET);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_ACCESS)){
            Dungeon.depth = 26;
            InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
            Game.switchScene( InterlevelScene.class );
        }
        if (action.equals(AC_RESET)){
            Dungeon.goForNewCycle();
        }

        if (action.equals(AC_RESET) || action.equals(AC_ACCESS)){
            detachAll(Dungeon.hero.belongings.backpack);
        }
    }

    @Override
    public String desc() {
        String desc;
        if (Dungeon.cycle  < 3){
            return Messages.get(BlackPsycheChest.class, "desc" + Dungeon.cycle) + "\n\n" + super.desc();
        } else {
            return Messages.get(BlackPsycheChest.class, "desc" + 3) + "\n\n" + super.desc();
        }
    }
}
