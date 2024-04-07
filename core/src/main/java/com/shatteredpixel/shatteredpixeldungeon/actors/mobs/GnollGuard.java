/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollGuardSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class GnollGuard extends Mob {

	{
		spriteClass = GnollGuardSprite.class;

		HP = HT = 35;
		defenseSkill = 15;

		EXP = 7;
		maxLvl = -2;

		loot = Spear.class;
		lootChance = 0.1f;
		switch (Dungeon.cycle){
			case 1:
				HP = HT = 360;
				defenseSkill = 52;
				EXP = 32;
				break;
			case 2:
				HP = HT = 5250;
				defenseSkill = 185;
				EXP = 325;
				break;
			case 3:
				HP = HT = 96750;
				defenseSkill = 500;
				EXP = 3215;
				break;
			case 4:
				HP = HT = 8650000;
				defenseSkill = 3210;
				EXP = 82000;
				break;
			case 5:
				HP = HT = 1555000000;
				defenseSkill = 59500;
				EXP = 37500000;
				break;
		}

		WANDERING = new Wandering();
	}

	private int sapperID = -1;

	public void linkSapper( GnollSapper sapper){
		this.sapperID = sapper.id();
		if (sprite instanceof GnollGuardSprite){
			((GnollGuardSprite) sprite).setupArmor();
		}
	}

	public boolean hasSapper(){
		return sapperID != -1
				&& Actor.findById(sapperID) instanceof GnollSapper
				&& ((GnollSapper)Actor.findById(sapperID)).isAlive();
	}

	public void loseSapper(){
		if (sapperID != -1){
			sapperID = -1;
			if (sprite instanceof GnollGuardSprite){
				((GnollGuardSprite) sprite).loseArmor();
			}
		}
	}

	@Override
	public void damage(long dmg, Object src) {
		if (hasSapper()) dmg /= 4;
		super.damage(dmg, src);
	}

	@Override
	public long damageRoll() {
		if (enemy != null && !Dungeon.level.adjacent(pos, enemy.pos)){
			switch (Dungeon.cycle) {
				case 1: return Random.NormalIntRange(68, 80);
				case 2: return Random.NormalIntRange(285, 390);
				case 3: return Random.NormalIntRange(1400, 1610);
				case 4: return Random.NormalIntRange(26000, 60000);
				case 5: return Random.NormalIntRange(3000000, 4400000);
			}
			return Random.NormalIntRange( 16, 22 );
		} else {
			switch (Dungeon.cycle) {
				case 1: return Random.NormalIntRange(45, 55);
				case 2: return Random.NormalIntRange(225, 275);
				case 3: return Random.NormalIntRange(850, 1000);
				case 4: return Random.NormalIntRange(16500, 22500);
				case 5: return Random.NormalIntRange(1750000, 2650000);
			}
			return Random.NormalIntRange( 6, 12 );
		}
	}

	@Override
	public long attackProc(Char enemy, long damage) {
		long dmg = super.attackProc(enemy, damage);
		if (enemy == Dungeon.hero && !Dungeon.level.adjacent(pos, enemy.pos) && dmg > 12){
			GLog.n(Messages.get(this, "spear_warn"));
		}
		return dmg;
	}

	@Override
	public int attackSkill( Char target ) {
		switch (Dungeon.cycle){
			case 1: return 85;
			case 2: return 312;
			case 3: return 745;
			case 4: return 4075;
			case 5: return 57950;
		}
		return 20;
	}

	@Override
	public int cycledDrRoll() {
		switch (Dungeon.cycle){
			case 1: return Random.NormalIntRange(22, 41);
			case 2: return Random.NormalIntRange(85, 230);
			case 3: return Random.NormalIntRange(495, 880);
			case 4: return Random.NormalIntRange(15000, 32000);
			case 5: return Random.NormalIntRange(1650000, 1800000);
		}
		return Random.NormalIntRange(0, 6);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		//cannot 'curve' spear hits like the hero, requires fairly open space to hit at a distance
		return Dungeon.level.distance(enemy.pos, pos) <= 2
				&& new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos
				&& new Ballistica( enemy.pos, pos, Ballistica.PROJECTILE).collisionPos == pos;
	}

	@Override
	public String description() {
		if (hasSapper()){
			return super.description() + "\n\n" + Messages.get(this, "desc_armor");
		} else {
			return super.description();
		}
	}

	private static final String SAPPER_ID = "sapper_id";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SAPPER_ID, sapperID);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		sapperID = bundle.getInt(SAPPER_ID);
	}

	public class Wandering extends Mob.Wandering {
		@Override
		protected int randomDestination() {
			if (hasSapper()){
				return ((GnollSapper)Actor.findById(sapperID)).pos;
			} else {
				return super.randomDestination();
			}
		}
	}

}
