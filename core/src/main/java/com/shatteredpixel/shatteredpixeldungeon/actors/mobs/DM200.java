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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM200Sprite;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class DM200 extends Mob {

	{
		spriteClass = DM200Sprite.class;

		HP = HT = 80;
		defenseSkill = 12;

		EXP = 9;
		maxLvl = 17;

		loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR);
		lootChance = 0.125f; //initially, see lootChance()

		properties.add(Property.INORGANIC);
		properties.add(Property.LARGE);

		HUNTING = new Hunting();
        switch (Dungeon.cycle){
            case 1:
                HP = HT = 780;
                defenseSkill = 40;
                EXP = 49;
                break;
            case 2:
                HP = HT = 8231;
                defenseSkill = 190;
                EXP = 471;
                break;
            case 3:
                HP = HT = 180000;
                defenseSkill = 570;
                EXP = 4750;
                break;
            case 4:
                HP = HT = 20000000;
                defenseSkill = 4400;
                EXP = 180000;
                break;
			case 5:
				HP = HT = 2475000000L;
				defenseSkill = 55500;
				EXP = 42000000;
				break;
        }
	}

	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Char.combatRoll(60, 79);
            case 2: return Char.combatRoll(280, 395);
            case 3: return Char.combatRoll(1300, 1631);
            case 4: return Char.combatRoll(20000, 60000);
			case 5: return Char.combatRoll(2500000, 5000000);
        }
		return Char.combatRoll( 10, 25 );
	}

	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 85;
            case 2: return 335;
            case 3: return 800;
            case 4: return 4600;
			case 5: return 68750;
        }
		return 20;
	}

	@Override
	public long cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Char.combatRoll(30, 47);
            case 2: return Char.combatRoll(100, 241);
            case 3: return Char.combatRoll(570, 1000);
            case 4: return Char.combatRoll(16000, 36000);
			case 5: return Char.combatRoll(1900000, 2500000);
        }
		return Char.combatRoll(0, 8);
	}

	@Override
	public float lootChance(){
		//each drop makes future drops 1/2 as likely
		// so loot chance looks like: 1/8, 1/16, 1/32, 1/64, etc.
		return super.lootChance() * (float)Math.pow(1/2f, Dungeon.LimitedDrops.DM200_EQUIP.count);
	}

	public Item createLoot() {
		Dungeon.LimitedDrops.DM200_EQUIP.count++;
		//uses probability tables for dwarf city
		if (loot == Generator.Category.WEAPON){
			return Generator.randomWeapon(4, true);
		} else {
			return Generator.randomArmor(4);
		}
	}

	private int ventCooldown = 0;

	private static final String VENT_COOLDOWN = "vent_cooldown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(VENT_COOLDOWN, ventCooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		ventCooldown = bundle.getInt( VENT_COOLDOWN );
	}

	@Override
	protected boolean act() {
		ventCooldown--;
		return super.act();
	}

	public void onZapComplete(){
		zap();
		next();
	}

	private void zap( ){
		spend( TICK );
		ventCooldown = 30;

		Ballistica trajectory = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET);

		for (int i : trajectory.subPath(0, trajectory.dist)){
			GameScene.add(Blob.seed(i, 20, ToxicGas.class));
		}
		GameScene.add(Blob.seed(trajectory.collisionPos, 100, ToxicGas.class));

	}

	protected boolean canVent(int target){
		if (ventCooldown > 0) return false;
		PathFinder.buildDistanceMap(target, BArray.not(Dungeon.level.solid, null), Dungeon.level.distance(pos, target)+1);
		//vent can go around blocking terrain, but not through it
		if (PathFinder.distance[pos] == Integer.MAX_VALUE){
			return false;
		}
		return true;
	}

	private class Hunting extends Mob.Hunting{

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (!enemyInFOV || canAttack(enemy)) {
				return super.act(enemyInFOV, justAlerted);
			} else {
				enemySeen = true;
				target = enemy.pos;

				int oldPos = pos;

				if (distance(enemy) >= 1 && Random.Int(100/distance(enemy)) == 0 && canVent(target)){
					if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
						sprite.zap( enemy.pos );
						return false;
					} else {
						zap();
						return true;
					}

				} else if (getCloser( target )) {
					spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else if (canVent(target)) {
					if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
						sprite.zap( enemy.pos );
						return false;
					} else {
						zap();
						return true;
					}

				} else {
					spend( TICK );
					return true;
				}

			}
		}
	}

}
