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
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.watabou.utils.Random;

public class Gnoll extends Mob {
	
	{
		spriteClass = GnollSprite.class;
		
		HP = HT = 12;
		defenseSkill = 4;
		
		EXP = 2;
		maxLvl = 8;
		
		loot = Gold.class;
		lootChance = 0.5f;

        switch (Dungeon.cycle){
            case 1:
                HP = HT = 115;
                defenseSkill = 27;
                EXP = 17;
                break;
            case 2:
                HP = HT = 1500;
                defenseSkill = 126;
                EXP = 140;
                break;
            case 3:
                HP = HT = 22000;
                defenseSkill = 360;
                EXP = 934;
                break;
        }
	}
	
	@Override
	public int damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Random.NormalIntRange(28, 40);
            case 2: return Random.NormalIntRange(130, 167);
            case 3: return Random.NormalIntRange(512, 644);
        }
		return Random.NormalIntRange( 1, 6 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 42;
            case 2: return 190;
            case 3: return 540;
        }
		return 10;
	}
	
	@Override
	public int drRoll() {
        switch (Dungeon.cycle){
            case 1: return Random.NormalIntRange(6, 17);
            case 2: return Random.NormalIntRange(69, 130);
            case 3: return Random.NormalIntRange(275, 500);
        }
		return Random.NormalIntRange(0, 2);
	}
}
