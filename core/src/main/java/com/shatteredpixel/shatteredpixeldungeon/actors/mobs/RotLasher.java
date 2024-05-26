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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotLasherSprite;

public class RotLasher extends Mob {

	{
		spriteClass = RotLasherSprite.class;

		HP = HT = 80;
		defenseSkill = 0;

		EXP = 1;

		loot = Generator.Category.SEED;
		lootChance = 0.75f;

		state = WANDERING = new Waiting();
		viewDistance = 1;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.MINIBOSS);
        switch (Dungeon.cycle){
            case 1:
                HP = HT = 365;
                defenseSkill = 0;
                EXP = 15;
                break;
            case 2:
                HP = HT = 4895;
                defenseSkill = 0;
                EXP = 128;
                break;
        }
	}

	@Override
	protected boolean act() {
		if (HP < HT && (enemy == null || !Dungeon.level.adjacent(pos, enemy.pos))) {
			sprite.showStatusWithIcon(CharSprite.POSITIVE, Long.toString(Math.min(5, HT - HP)), FloatingText.HEALING);
			HP = Math.min(HT, HP + 3 + Dungeon.cycle * 25L);
		}
		return super.act();
	}

	@Override
	public void damage(long dmg, Object src) {
		if (src instanceof Burning) {
			destroy();
			sprite.die();
		} else {
			super.damage(dmg, src);
		}
	}

	@Override
	public long attackProc(Char enemy, long damage) {
		damage = super.attackProc( enemy, damage );
		Buff.affect( enemy, Cripple.class, 2f );
		return super.attackProc(enemy, damage);
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	protected boolean getFurther(int target) {
		return false;
	}

	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Char.combatRoll(45, 63);
            case 2: return Char.combatRoll(250, 324);
        }
		return Char.combatRoll(10, 20);
	}

	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 60;
            case 2: return 280;
        }
		return 25;
	}

	@Override
	public long cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Char.combatRoll(14, 34);
            case 2: return Char.combatRoll(140, 234);
        }
		return Char.combatRoll(0, 8);
	}
	
	{
		immunities.add( ToxicGas.class );
	}

	private class Waiting extends Mob.Wandering{

		@Override
		protected boolean noticeEnemy() {
			spend(TICK);
			return super.noticeEnemy();
		}
	}
}
