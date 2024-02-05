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
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Random;

public class Bat extends Mob {

	{
		spriteClass = BatSprite.class;
		
		HP = HT = 30;
		defenseSkill = 15;
		baseSpeed = 2f;
		
		EXP = 7;
		maxLvl = 15;
		
		flying = true;
		
		loot = new PotionOfHealing();
		lootChance = 0.1667f; //by default, see lootChance()
        switch (Dungeon.cycle){
            case 1:
                HP = HT = 320;
                defenseSkill = 48;
                EXP = 34;
                break;
            case 2:
                HP = HT = 4600;
                defenseSkill = 225;
                EXP = 321;
                break;
            case 3:
                HP = HT = 80000;
                defenseSkill = 500;
                EXP = 3000;
                break;
            case 4:
                HP = HT = 7000000;
                defenseSkill = 3000;
                EXP = 74000;
                break;
        }
	}
	
	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Random.NormalIntRange(50, 65);
            case 2: return Random.NormalIntRange(240, 312);
            case 3: return Random.NormalIntRange(800, 1100);
            case 4: return Random.NormalIntRange(14000, 25000);
        }
		return Random.NormalIntRange( 5, 18 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 68;
            case 2: return 260;
            case 3: return 700;
            case 4: return 3390;
        }
		return 16;
	}
	
	@Override
	public int cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Random.NormalIntRange(13, 28);
            case 2: return Random.NormalIntRange(70, 170);
            case 3: return Random.NormalIntRange(500, 700);
            case 4: return Random.NormalIntRange(8000, 16000);
        }
		return Random.NormalIntRange(0, 4);
	}
	
	@Override
	public long attackProc( Char enemy, long damage ) {
		damage = super.attackProc( enemy, damage );
		long reg = Math.min( damage - 4 - Dungeon.cycle * 60L, HT - HP );
		
		if (reg > 0) {
			HP += reg;
			sprite.showStatusWithIcon(CharSprite.POSITIVE, Long.toString(reg), FloatingText.HEALING);
		}
		
		return damage;
	}
	
	@Override
	public float lootChance(){
		return super.lootChance() * ((7f - Dungeon.LimitedDrops.BAT_HP.count) / 7f);
	}
	
	@Override
	public Item createLoot(){
		Dungeon.LimitedDrops.BAT_HP.count++;
		return super.createLoot();
	}
	
}
