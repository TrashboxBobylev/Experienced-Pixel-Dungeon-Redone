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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM201Sprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class DM201 extends DM200 {

	{
		spriteClass = DM201Sprite.class;

		HP = HT = 120;

		properties.add(Property.IMMOVABLE);

		HUNTING = new Hunting();

        switch (Dungeon.cycle){
            case 1:
                HP = HT = 900;
                EXP = 52;
                break;
            case 2:
                HP = HT = 9845;
                defenseSkill = 190;
                EXP = 529;
                break;
            case 3:
                HP = HT = 260000;
                defenseSkill = 590;
                EXP = 5400;
                break;
            case 4:
                HP = HT = 32000000;
                defenseSkill = 4800;
                EXP = 210000;
                break;
        }
	}

	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Random.NormalIntRange(69, 91);
            case 2: return Random.NormalIntRange(321, 412);
            case 3: return Random.NormalIntRange(1400, 1794);
            case 4: return Random.NormalIntRange(40000, 75000);
        }
		return Random.NormalIntRange( 15, 25 );
	}

	private boolean threatened = false;

	@Override
	public void damage(long dmg, Object src) {
		if ((src instanceof Char && !Dungeon.level.adjacent(pos, ((Char)src).pos))
				|| enemy == null || !Dungeon.level.adjacent(pos, enemy.pos)){
			threatened = true;
		}
		super.damage(dmg, src);
	}

	public void onZapComplete(){
		zap();
		next();
	}

	private void zap( ){
		threatened = false;
		spend(TICK);

		GameScene.add(Blob.seed(enemy.pos, 15, CorrosiveGas.class).setStrength((Dungeon.escalatingDepth() + 1) / 2));
		for (int i : PathFinder.NEIGHBOURS8){
			if (!Dungeon.level.solid[enemy.pos+i]) {
				GameScene.add(Blob.seed(enemy.pos + i, 5, CorrosiveGas.class).setStrength((Dungeon.escalatingDepth() + 1 )/ 2));
			}
		}

	}

	@Override
	protected boolean canVent(int target) {
		return false;
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
	public void rollToDropLoot() {

		super.rollToDropLoot();

		int ofs;
		do {
			ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs]);
		Dungeon.level.drop( new MetalShard(), pos + ofs ).sprite.drop( pos );
	}

	private class Hunting extends Mob.Hunting {

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

			if (threatened && enemyInFOV){
				if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
					sprite.zap( enemy.pos );
					return false;
				} else {
					zap();
					return true;
				}
			} else {
				return super.act( enemyInFOV, justAlerted );
			}

		}

	}

}
