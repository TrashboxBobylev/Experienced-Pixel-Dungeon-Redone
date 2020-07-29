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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.watabou.utils.Random;

public class Rat extends Mob {

	{
		spriteClass = RatSprite.class;
		
		HP = HT = 8;
		defenseSkill = 2;
		
		maxLvl = 5;

		switch (Dungeon.cycle){
            case 1:
                HP = HT = 100;
                defenseSkill = 26;
                EXP = 15;
                break;
            case 2:
                HP = HT = 1250;
                defenseSkill = 120;
                EXP = 120;
                break;
        }
	}
	
	@Override
	public int damageRoll() {
	    switch (Dungeon.cycle) {
            case 1: return Random.NormalIntRange(25, 31);
            case 2: return Random.NormalIntRange(110, 145);
        }
        return Random.NormalIntRange(1, 4);
	}
	
	@Override
	public int attackSkill( Char target ) {
	    switch (Dungeon.cycle){
            case 1: return 38;
            case 2: return 175;
        }
		return 8;
	}
	
	@Override
	public int drRoll() {
        switch (Dungeon.cycle){
            case 1: return Random.NormalIntRange(5, 15);
            case 2: return Random.NormalIntRange(60, 100);
        }
		return Random.NormalIntRange(0, 1);
	}
}
