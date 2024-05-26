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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Monk extends Mob {
	
	{
		spriteClass = MonkSprite.class;
		
		HP = HT = 70;
		defenseSkill = 30;
		
		EXP = 11;
		maxLvl = 21;
		
		loot = new Food();
		lootChance = 0.083f;

		properties.add(Property.UNDEAD);
        switch (Dungeon.cycle){
            case 1:
                HP = HT = 825;
                defenseSkill = 89;
                EXP = 71;
                break;
            case 2:
                HP = HT = 12678;
                defenseSkill = 345;
                EXP = 600;
                break;
            case 3:
                HP = HT = 275000;
                defenseSkill = 700;
                EXP = 6500;
                break;
            case 4:
                HP = HT = 100000000;
                defenseSkill = 6400;
                EXP = 800000;
                break;
			case 5:
				HP = HT = 3900000000L;
				defenseSkill = 100500;
				EXP = 145000000;
				break;
        }
	}
	
	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Char.combatRoll(62, 78);
            case 2: return Char.combatRoll(270, 400);
            case 3: return Char.combatRoll(1700, 2240);
            case 4: return Char.combatRoll(40000, 100000);
			case 5: return Char.combatRoll(3100000, 7750000);
        }
		return Char.combatRoll( 12, 25 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 134;
            case 2: return 440;
            case 3: return 1000;
            case 4: return 7000;
			case 5: return 102500;
        }
		return 30;
	}
	
	@Override
	public float attackDelay() {
		return super.attackDelay()*0.5f;
	}
	
	@Override
	public long cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Char.combatRoll(20, 30);
            case 2: return Char.combatRoll(60, 193);
            case 3: return Char.combatRoll(700, 1000);
            case 4: return Char.combatRoll(26000, 73000);
			case 5: return Char.combatRoll(2700000, 5150000);
        }
		return Char.combatRoll(0, 2);
	}
	
	@Override
	public void rollToDropLoot() {
		Imp.Quest.process( this );
		
		super.rollToDropLoot();
	}
	
	protected float focusCooldown = 0;
	
	@Override
	protected boolean act() {
		boolean result = super.act();
		if (buff(Focus.class) == null && state == HUNTING && focusCooldown <= 0) {
			Buff.affect( this, Focus.class );
		}
		return result;
	}
	
	@Override
    public void spend(float time) {
		focusCooldown -= time;
		super.spend( time );
	}
	
	@Override
	public void move( int step, boolean travelling) {
		// moving reduces cooldown by an additional 0.67, giving a total reduction of 1.67f.
		// basically monks will become focused notably faster if you kite them.
		if (travelling) focusCooldown -= 0.67f;
		super.move( step, travelling);
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		if (buff(Focus.class) != null && paralysed == 0 && state != SLEEPING){
			return INFINITE_EVASION;
		}
		return super.defenseSkill( enemy );
	}
	
	@Override
	public String defenseVerb() {
		Focus f = buff(Focus.class);
		if (f == null) {
			return super.defenseVerb();
		} else {
			f.detach();
			if (sprite != null && sprite.visible) {
				Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1, Random.Float(0.96f, 1.05f));
			}
			focusCooldown = Random.NormalFloat( 6, 7 );
			return Messages.get(this, "parried");
		}
	}
	
	private static String FOCUS_COOLDOWN = "focus_cooldown";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( FOCUS_COOLDOWN, focusCooldown );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		focusCooldown = bundle.getInt( FOCUS_COOLDOWN );
	}
	
	public static class Focus extends Buff {
		
		{
			type = buffType.POSITIVE;
			announced = true;
		}
		
		@Override
		public int icon() {
			return BuffIndicator.MIND_VISION;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.25f, 1.5f, 1f);
		}
	}
}
