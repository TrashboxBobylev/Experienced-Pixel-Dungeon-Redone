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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SlimeSprite;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class Slime extends Mob {
	
	{
		spriteClass = SlimeSprite.class;
		
		HP = HT = 20;
		defenseSkill = 5;
		
		EXP = 4;
		maxLvl = 9;
		
		lootChance = 0.2f; //by default, see lootChance()

        switch (Dungeon.cycle){
            case 1:
                HP = HT = 200;
                defenseSkill = 30;
                EXP = 22;
                break;
            case 2:
                HP = HT = 2135;
                defenseSkill = 150;
                EXP = 196;
                break;
            case 3:
                HP = HT = 40000;
                defenseSkill = 415;
                EXP = 1500;
                break;
            case 4:
                HP = HT = 3000000;
                defenseSkill = 1900;
                EXP = 43000;
                break;
			case 5:
				HP = HT = 675000000;
				defenseSkill = 33000;
				EXP = 19500000;
				break;
        }
	}
	
	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Random.NormalIntRange(32, 38);
            case 2: return Random.NormalIntRange(164, 189);
            case 3: return Random.NormalIntRange(560, 740);
            case 4: return Random.NormalIntRange(7000, 9000);
			case 5: return Random.NormalIntRange(475000, 635000);
        }
	    return Random.NormalIntRange( 2, 5 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 46;
            case 2: return 225;
            case 3: return 580;
            case 4: return 2200;
			case 5: return 32000;
        }
		return 12;
	}
	
	@Override
	public void damage(long dmg, Object src) {
		double scaleFactor = AscensionChallenge.statModifier(this);
		long scaledDmg = Math.round(dmg/scaleFactor);
		if (scaledDmg >= 5 + Dungeon.cycle * 75 && Dungeon.cycle < 2){
			//takes 5/6/7/8/9/10 dmg at 5/7/10/14/19/25 incoming dmg
			scaledDmg = 4 + Dungeon.cycle * 75 + (int)(Math.sqrt(8*(scaledDmg - 4) + 1) - 1)/2;
		}
		dmg = (long) (scaledDmg*AscensionChallenge.statModifier(this));
		super.damage(dmg, src);
	}

	@Override
	public float lootChance(){
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/5, 1/15, 1/45, 1/135, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.SLIME_WEP.count);
	}
	
	@Override
	public Item createLoot() {
		Dungeon.LimitedDrops.SLIME_WEP.count++;
		Generator.Category c = Generator.Category.WEP_T2;
		MeleeWeapon w = (MeleeWeapon) Reflection.newInstance(c.classes[Random.chances(c.probs)]);
		w.random();
		w.level(0);
		return w;
	}
}
