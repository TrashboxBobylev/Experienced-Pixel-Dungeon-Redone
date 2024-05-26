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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class Gloves extends MeleeWeapon {

	{
		image = ItemSpriteSheet.GLOVES;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.3f;

		internalTier = tier = 1;
		DLY = 0.5f; //2x speed
		
		bones = false;
	}

	@Override
	public long max(long lvl) {
		if (Dungeon.hero.buff(AttackBuff.class) != null){
			return  (long)(3d*(tier()+1)) +    //6 base, down from 10
					lvl*tier() + Dungeon.hero.buff(AttackBuff.class).stack;
		}
		return  Math.round(2.5d*(tier()+1)) +     //5 base, down from 10
				lvl*Math.round(0.5d*(tier()+1));  //+1 per level, down from +2
	}

	@Override
	public long proc(Char attacker, Char defender, long damage) {
		if (damage == 0) Buff.affect(attacker, AttackBuff.class, 2f).stack++;
		return super.proc(attacker, defender, damage);
	}

	public static class AttackBuff extends FlavourBuff {

		public static final float DURATION = 2f;

		public int stack = 0;

		{
			type = buffType.NEGATIVE;
			announced = false;
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

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(2+0.5*lvl) damage, roughly +67% base damage, +50% scaling
		long dmgBoost = augment.damageFactor(2 + Math.round(0.5d*buffedLvl()));
		Sai.comboStrikeAbility(hero, target, 0, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 2 + Math.round(0.5f*buffedLvl()) : 2;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", augment.damageFactor(dmgBoost));
		}
	}

}
