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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class Kunai extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.KUNAI;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.1f;
		
		internalTier = tier = 3;
		baseUses = 5;
	}
	
	private Char enemy;
	
	@Override
	protected void onThrow(int cell) {
		enemy = Actor.findChar(cell);
		super.onThrow(cell);
	}

    @Override
    public long proc(Char attacker, Char defender, long damage) {
	    if (defender instanceof Mob && ((Mob) defender).surprisedBy(attacker)){
            Buff.affect(attacker, WeaponCharge.class, 15f).stack = (int) (level()*level() + 2);
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
	public long damageRoll(Char owner) {
		if (owner instanceof Hero) {
			Hero hero = (Hero)owner;
			if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
				//deals 60% toward max to max on surprise, instead of min to max.
				long diff = max() - min();
				long damage = augment.damageFactor(Char.combatRoll(
						min() + Math.round(diff*0.6f),
						max()));
				int exStr = hero.STR() - STRReq();
				if (exStr > 0) {
					damage += Dungeon.IntRange(0, exStr);
				}

				return damage;
			}
		}
		return super.damageRoll(owner);
	}

    public static class WeaponCharge extends FlavourBuff {

        public static final float DURATION = 20f;

        public int stack = 0;

        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.PREPARATION;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(), stack);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("stack", stack);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            stack = bundle.getInt("stack");
        }
    }
	
}
