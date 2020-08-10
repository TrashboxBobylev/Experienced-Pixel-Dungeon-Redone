/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;

public class Greataxe extends MeleeWeapon {

	{
		image = ItemSpriteSheet.GREATAXE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 5;
	}

	@Override
	public int max(int lvl) {
		return  5*(tier+4) +    //45 base, up from 30
				lvl*(tier+1);   //scaling unchanged
	}

	@Override
	public int STRReq(int lvl) {
		lvl = Math.max(0, lvl);
		//20 base strength req, up from 18
		return (10 + tier * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}

    @Override
    protected void onThrow(int cell) {
        super.onThrow(cell);
        int enemyCount = 0;
        for (int i : PathFinder.NEIGHBOURS9){

            if (!Dungeon.level.solid[cell + i]
                    && !Dungeon.level.pit[cell + i]
                    && Actor.findChar(cell + i) != null) {
                KindOfWeapon equipped = Dungeon.hero.belongings.weapon;
                Dungeon.hero.belongings.weapon = this;
                boolean hit = Dungeon.hero.attack( Actor.findChar(cell + i) );
                Invisibility.dispel();

                Dungeon.hero.belongings.weapon = equipped;
                enemyCount++;

                CellEmitter.get(cell + i).burst(Speck.factory(Speck.SCREAM), 4);
                break;
            }
        }
        Dungeon.hero.spendAndNext( speedFactor(Dungeon.hero) + speedFactor(Dungeon.hero)*enemyCount*0.8f );
    }
}
