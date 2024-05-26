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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class Skeleton extends Mob {
	
	{
		spriteClass = SkeletonSprite.class;
		
		HP = HT = 25;
		defenseSkill = 9;
		
		EXP = 5;
		maxLvl = 10;

		loot = Generator.Category.WEAPON;
		lootChance = 0.1667f; //by default, see lootChance()

		properties.add(Property.UNDEAD);
		properties.add(Property.INORGANIC);

        switch (Dungeon.cycle){
            case 1:
                HP = HT = 225;
                defenseSkill = 34;
                EXP = 23;
                break;
            case 2:
                HP = HT = 2135;
                defenseSkill = 161;
                EXP = 221;
                break;
            case 3:
                HP = HT = 50000;
                defenseSkill = 435;
                EXP = 1700;
                break;
            case 4:
                HP = HT = 3400000;
                defenseSkill = 2100;
                EXP = 51000;
                break;
			case 5:
				HP = HT = 1000000000;
				defenseSkill = 40000;
				EXP = 22500000;
				break;
        }
	}
	
	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Char.combatRoll(34, 50);
            case 2: return Char.combatRoll(180, 231);
            case 3: return Char.combatRoll(600, 850);
            case 4: return Char.combatRoll(8000, 14000);
			case 5: return Char.combatRoll(525000, 1100000);
        }
		return Char.combatRoll( 2, 10 );
	}
	
	@Override
	public void die( Object cause ) {
		
		super.die( cause );
		
		if (cause == Chasm.class) return;
		
		boolean heroKilled = false;
		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			Char ch = findChar( pos + PathFinder.NEIGHBOURS8[i] );
			if (ch != null && ch.isAlive()) {
				long damage = Math.round(damageRoll()*1.5d);
				damage = Math.round( damage * AscensionChallenge.statModifier(this));
				//armor is 2x effective against bone explosion
				damage = Math.max( 0,  damage - (ch.drRoll() + ch.drRoll()) );
				ch.damage( damage, this );
				if (ch == Dungeon.hero && !ch.isAlive()) {
					heroKilled = true;
				}
			}
		}
		
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play( Assets.Sounds.BONES );
		}
		
		if (heroKilled) {
			Dungeon.fail( this );
			GLog.n( Messages.get(this, "explo_kill") );
		}
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/2 as likely
		// so loot chance looks like: 1/6, 1/12, 1/24, 1/48, etc.
		return super.lootChance() * (float)Math.pow(1/2f, Dungeon.LimitedDrops.SKELE_WEP.count);
	}

	@Override
	public Item createLoot() {
		Dungeon.LimitedDrops.SKELE_WEP.count++;
		return super.createLoot();
	}

	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 50;
            case 2: return 224;
            case 3: return 600;
            case 4: return 2500;
			case 5: return 36000;
        }
		return 12;
	}
	
	@Override
	public long cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Char.combatRoll(10, 24);
            case 2: return Char.combatRoll(95, 170);
            case 3: return Char.combatRoll(360, 625);
            case 4: return Char.combatRoll(5000, 10000);
			case 5: return Char.combatRoll(55000, 700000);
        }
		return Char.combatRoll(0, 5);
	}

}
