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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BruteSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Brute extends Mob {
	
	{
		spriteClass = BruteSprite.class;
		
		HP = HT = 40;
		defenseSkill = 15;
		
		EXP = 8;
		maxLvl = 16;
		
		loot = Gold.class;
		lootChance = 0.5f;
        switch (Dungeon.cycle){
            case 1:
                HP = HT = 420;
                defenseSkill = 53;
                EXP = 38;
                break;
            case 2:
                HP = HT = 5645;
                defenseSkill = 200;
                EXP = 360;
                break;
            case 3:
                HP = HT = 105000;
                defenseSkill = 525;
                EXP = 3600;
                break;
        }
	}
	
	protected boolean hasRaged = false;
	
	@Override
	public int damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return buff(BruteRage.class) != null ?
                    Random.NormalIntRange( 70, 123 ) :
                    Random.NormalIntRange( 58, 70 );
            case 2: return buff(BruteRage.class) != null ?
                    Random.NormalIntRange( 340, 541 ) :
                    Random.NormalIntRange( 240, 368 );
            case 3: return buff(BruteRage.class) != null ?
                    Random.NormalIntRange( 1200, 1640 ) :
                    Random.NormalIntRange(900, 1340);
        }
		return buff(BruteRage.class) != null ?
			Random.NormalIntRange( 15, 40 ) :
			Random.NormalIntRange( 5, 25 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 80;
            case 2: return 300;
            case 3: return 740;
        }
		return 20;
	}
	
	@Override
	public int drRoll() {
        switch (Dungeon.cycle){
            case 1: return Random.NormalIntRange(20, 39);
            case 2: return Random.NormalIntRange(120, 231);
            case 3: return Random.NormalIntRange(500, 890);
        }
		return Random.NormalIntRange(0, 8);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);

		if (cause == Chasm.class){
			hasRaged = true; //don't let enrage trigger for chasm deaths
		}
	}

	@Override
	public synchronized boolean isAlive() {
		if (HP > 0){
			return true;
		} else {
			if (!hasRaged){
				triggerEnrage();
			}
			return !buffs(BruteRage.class).isEmpty();
		}
	}
	
	protected void triggerEnrage(){
		Buff.affect(this, BruteRage.class).setShield(HT/2 + 4);
		if (Dungeon.level.heroFOV[pos]) {
			sprite.showStatus( CharSprite.NEGATIVE, Messages.get(this, "enraged") );
		}
		spend( TICK );
		hasRaged = true;
	}
	
	private static final String HAS_RAGED = "has_raged";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HAS_RAGED, hasRaged);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		hasRaged = bundle.getBoolean(HAS_RAGED);
	}
	
	public static class BruteRage extends ShieldBuff {
		
		{
			type = buffType.POSITIVE;
		}
		
		@Override
		public boolean act() {
			
			if (target.HP > 0){
				detach();
				return true;
			}
			
			absorbDamage( 4 + Dungeon.cycle * 30 );
			
			if (shielding() <= 0){
				target.die(null);
			}
			
			spend( TICK );
			
			return true;
		}
		
		@Override
		public int icon () {
			return BuffIndicator.FURY;
		}
		
		@Override
		public String toString () {
			return Messages.get(this, "name");
		}
		
		@Override
		public String desc () {
			return Messages.get(this, "desc", shielding());
		}

		{
			immunities.add(Terror.class);
		}
	}
}
