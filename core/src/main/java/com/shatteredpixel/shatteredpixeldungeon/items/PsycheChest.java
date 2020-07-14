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
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Game;

import java.util.ArrayList;

public class PsycheChest extends Item {
    {
        image = ItemSpriteSheet.PSYCHE_CHEST;
        unique = true;
    }

    private static final String AC_ACTIVATE = "ACTIVATE";
    private static final String AC_DEACTIVATE = "DEACTIVATE";
    private static final String AC_RESET = "RESET";

    private static final ItemSprite.Glowing BLOODY = new ItemSprite.Glowing( 0x550000 );

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (!hero.grinding) actions.add( AC_ACTIVATE );
        else actions.add( AC_DEACTIVATE);
        actions.remove(AC_DROP);
        actions.remove(AC_THROW);
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals(AC_ACTIVATE)) {
            hero.grinding = true;
            GLog.w( Messages.get(this, "activated") );
        }
        if (action.contains(AC_DEACTIVATE)){
            hero.grinding = false;
            GLog.w( Messages.get(this, "deactivated") );
        }
        if (action.contains(AC_RESET) && (hero.HP > hero.HT / 2)){
            InterlevelScene.mode = InterlevelScene.Mode.RESET;
            if (hero.HP > hero.HT / 2) hero.HP -= hero.HT / 2;
            Game.switchScene(InterlevelScene.class);
        } else {
            GLog.w( Messages.get(this, "no_reset") );
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return Dungeon.hero.grinding ? BLOODY : null;
    }
}
