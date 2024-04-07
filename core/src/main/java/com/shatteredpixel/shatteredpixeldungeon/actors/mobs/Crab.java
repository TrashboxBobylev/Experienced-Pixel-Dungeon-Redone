/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrabSprite;
import com.watabou.utils.Random;

public class Crab extends Mob {

	{
		spriteClass = CrabSprite.class;
		
		HP = HT = 15;
		defenseSkill = 5;
		baseSpeed = 2f;
		
		EXP = 4;
		maxLvl = 9;
		
		loot = new MysteryMeat();
		lootChance = 0.167f;

        switch (Dungeon.cycle){
            case 1:
                HP = HT = 140;
                defenseSkill = 30;
                EXP = 20;
                break;
            case 2:
                HP = HT = 1845;
                defenseSkill = 140;
                EXP = 175;
                break;
            case 3:
                HP = HT = 35000;
                defenseSkill = 390;
                EXP = 1280;
                break;
            case 4:
                HP = HT = 2250000;
                defenseSkill = 1700;
                EXP = 39000;
                break;
            case 5:
                HP = HT = 625000000;
                defenseSkill = 29000;
                EXP = 17000000;
                break;
        }
	}
	
	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Random.NormalIntRange(30, 45);
            case 2: return Random.NormalIntRange(150, 184);
            case 3: return Random.NormalIntRange(540, 721);
            case 4: return Random.NormalIntRange(6000, 10000);
            case 5: return Random.NormalIntRange(450000, 675000);
        }
		return Random.NormalIntRange( 1, 7 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 46;
            case 2: return 210;
            case 3: return 560;
            case 4: return 2000;
            case 5: return 28000;
        }
		return 12;
	}
	
	@Override
	public int cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Random.NormalIntRange(8, 20);
            case 2: return Random.NormalIntRange(76, 150);
            case 3: return Random.NormalIntRange(300, 550);
            case 4: return Random.NormalIntRange(4000, 8500);
            case 5: return Random.NormalIntRange(440000, 600000);
        }
		return Random.NormalIntRange(0, 4);
	}
}
