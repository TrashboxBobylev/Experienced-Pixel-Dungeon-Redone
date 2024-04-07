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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM100Sprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class DM100 extends Mob implements Callback {

	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = DM100Sprite.class;
		
		HP = HT = 20;
		defenseSkill = 8;
		
		EXP = 6;
		maxLvl = 13;
		
		loot = Generator.Category.SCROLL;
		lootChance = 0.25f;
		
		properties.add(Property.ELECTRIC);
		properties.add(Property.INORGANIC);

        switch (Dungeon.cycle){
            case 1:
                HP = HT = 300;
                defenseSkill = 36;
                EXP = 26;
                break;
            case 2:
                HP = HT = 2500;
                defenseSkill = 180;
                EXP = 280;
                break;
            case 3:
                HP = HT = 60000;
                defenseSkill = 450;
                EXP = 2200;
                break;
            case 4:
                HP = HT = 4000000;
                defenseSkill = 2560;
                EXP = 62000;
                break;
			case 5:
				HP = HT = 850000000;
				defenseSkill = 36000;
				EXP = 26000000;
				break;
        }
	}
	
	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Random.NormalIntRange(31, 45);
            case 2: return Random.NormalIntRange(160, 205);
            case 3: return Random.NormalIntRange(725, 1000);
            case 4: return Random.NormalIntRange(10000, 16800);
			case 5: return Random.NormalIntRange(665000, 1450000);
        }
		return Random.NormalIntRange( 2, 8 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 53;
            case 2: return 240;
            case 3: return 660;
            case 4: return 2900;
			case 5: return 47500;
        }
		return 11;
	}
	
	@Override
	public int cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Random.NormalIntRange(8, 24);
            case 2: return Random.NormalIntRange(60, 160);
            case 3: return Random.NormalIntRange(370, 660);
            case 4: return Random.NormalIntRange(6000, 11000);
			case 5: return Random.NormalIntRange(120000, 800000);
        }
		return Random.NormalIntRange(0, 4);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy)
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class LightningBolt{}
	
	@Override
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos )
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos != enemy.pos) {
			
			return super.doAttack( enemy );
			
		} else {

			spend( TIME_TO_ZAP );

			Invisibility.dispel(this);
			if (hit( this, enemy, true )) {
				long dmg = Random.NormalIntRange(3, 10);
				dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
                switch (Dungeon.cycle){
                    case 1: dmg = Random.NormalIntRange(32, 48); break;
                    case 2: dmg = Random.NormalIntRange(190, 248); break;
                    case 3: dmg =  Random.NormalIntRange(600, 850); break;
                    case 4: dmg =  Random.NormalIntRange(9000, 15000); break;
					case 5: dmg =  Random.NormalIntRange(110000, 750000); break;
                }
				enemy.damage( dmg, new LightningBolt() );

				if (enemy.sprite.visible) {
					enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
					enemy.sprite.flash();
				}
				
				if (enemy == Dungeon.hero) {
					
					PixelScene.shake( 2, 0.3f );
					
					if (!enemy.isAlive()) {
						Badges.validateDeathFromEnemyMagic();
						Dungeon.fail( this );
						GLog.n( Messages.get(this, "zap_kill") );
					}
				}
			} else {
				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}
			
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				return true;
			}
		}
	}
	
	@Override
	public void call() {
		next();
	}
	
}
