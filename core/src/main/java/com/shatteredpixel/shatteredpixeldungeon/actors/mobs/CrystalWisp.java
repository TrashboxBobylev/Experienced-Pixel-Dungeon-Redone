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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrystalWispSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class CrystalWisp extends Mob{

	{
		spriteClass = CrystalWispSprite.class;

		HP = HT = 30;
		defenseSkill = 16;

		EXP = 7;
		maxLvl = -2;

		flying = true;

		properties.add(Property.INORGANIC);

		switch (Dungeon.cycle){
			case 1:
				HP = HT = 320;
				defenseSkill = 48;
				EXP = 34;
				break;
			case 2:
				HP = HT = 4600;
				defenseSkill = 225;
				EXP = 321;
				break;
			case 3:
				HP = HT = 80000;
				defenseSkill = 500;
				EXP = 3000;
				break;
			case 4:
				HP = HT = 7000000;
				defenseSkill = 3000;
				EXP = 74000;
				break;
			case 5:
				HP = HT = 1400000000;
				defenseSkill = 59500;
				EXP = 36500000;
				break;
		}
	}

	public CrystalWisp(){
		super();
		switch (Random.Int(3)){
			case 0: default:
				spriteClass = CrystalWispSprite.Blue.class;
				break;
			case 1:
				spriteClass = CrystalWispSprite.Green.class;
				break;
			case 2:
				spriteClass = CrystalWispSprite.Red.class;
				break;
		}
	}

	@Override
	public boolean[] modifyPassable(boolean[] passable) {
		for (int i = 0; i < Dungeon.level.length(); i++){
			passable[i] = passable[i] || Dungeon.level.map[i] == Terrain.MINE_CRYSTAL;
		}
		return passable;
	}

	@Override
	public long damageRoll() {
		switch (Dungeon.cycle) {
			case 1: return Random.NormalIntRange(50, 55);
			case 2: return Random.NormalIntRange(240, 275);
			case 3: return Random.NormalIntRange(800, 900);
			case 4: return Random.NormalIntRange(14000, 18500);
			case 5: return Random.NormalIntRange(1100000, 1800000);
		}
		return Random.NormalIntRange( 5, 10 );
	}

	@Override
	public int attackSkill( Char target ) {
		switch (Dungeon.cycle){
			case 1: return 72;
			case 2: return 275;
			case 3: return 760;
			case 4: return 3600;
			case 5: return 53000;
		}
		return 18;
	}

	@Override
	public int cycledDrRoll() {
		switch (Dungeon.cycle){
			case 1: return Random.NormalIntRange(13, 28);
			case 2: return Random.NormalIntRange(70, 170);
			case 3: return Random.NormalIntRange(500, 700);
			case 4: return Random.NormalIntRange(8000, 16000);
			case 5: return Random.NormalIntRange(650000, 1250000);
		}
		return Random.NormalIntRange(0, 4);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy)
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	protected boolean doAttack(Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos )
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos != enemy.pos) {

			return super.doAttack( enemy );

		} else {

			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
	}

	//used so resistances can differentiate between melee and magical attacks
	public static class LightBeam {}

	private void zap() {
		spend( 1f );

		Invisibility.dispel(this);
		Char enemy = this.enemy;
		if (hit( this, enemy, true )) {

			int dmg = Random.NormalIntRange( 5, 10 );
			switch (Dungeon.cycle) {
				case 1: dmg = Random.NormalIntRange(50, 55); break;
				case 2: dmg = Random.NormalIntRange(240, 275); break;
				case 3: dmg = Random.NormalIntRange(800, 900); break;
				case 4: dmg = Random.NormalIntRange(14000, 18500); break;
				case 5: dmg = Random.NormalIntRange(1100000, 1800000); break;
			}
			enemy.damage( dmg, new LightBeam() );

			if (!enemy.isAlive() && enemy == Dungeon.hero) {
				Badges.validateDeathFromEnemyMagic();
				Dungeon.fail( this );
				GLog.n( Messages.get(this, "beam_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}

	public void onZapComplete() {
		zap();
		next();
	}

	public static final String SPRITE = "sprite";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SPRITE, spriteClass);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		spriteClass = bundle.getClass(SPRITE);
	}
}
