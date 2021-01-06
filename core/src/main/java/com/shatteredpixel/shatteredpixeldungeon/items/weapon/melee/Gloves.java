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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
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
	public int max(int lvl) {
		if (Dungeon.hero.buff(AttackBuff.class) != null){
			return  (int)(3f*(tier+1)) +    //6 base, down from 10
					lvl*tier + Dungeon.hero.buff(AttackBuff.class).stack;
		}
		return  (int)(3f*(tier+1)) +    //6 base, down from 10
				lvl*tier;               //+1 per level, down from +2
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
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

}
