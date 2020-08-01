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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WarlockSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Warlock extends Mob implements Callback {
	
	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = WarlockSprite.class;
		
		HP = HT = 70;
		defenseSkill = 18;
		
		EXP = 11;
		maxLvl = 21;
		
		loot = Generator.Category.POTION;
		lootChance = 0.5f;

		properties.add(Property.UNDEAD);
        switch (Dungeon.cycle){
            case 1:
                HP = HT = 840;
                defenseSkill = 71;
                EXP = 76;
                break;
            case 2:
                HP = HT = 11212;
                defenseSkill = 291;
                EXP = 625;
                break;
            case 3:
                HP = HT = 325000;
                defenseSkill = 735;
                EXP = 7100;
                break;
        }
	}
	
	@Override
	public int damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Random.NormalIntRange(60, 74);
            case 2: return Random.NormalIntRange(240, 389);
            case 3: return Random.NormalIntRange(1800, 2300);

        }
		return Random.NormalIntRange( 12, 18 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 115;
            case 2: return 392;
            case 3: return 960;
        }
		return 25;
	}
	
	@Override
	public int drRoll() {
        switch (Dungeon.cycle){
            case 1: return Random.NormalIntRange(30, 59);
            case 2: return Random.NormalIntRange(112, 276);
            case 3: return Random.NormalIntRange(900, 1600);
        }
		return Random.NormalIntRange(0, 8);
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos )) {
			
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
	public static class DarkBolt{}
	
	private void zap() {
		spend( TIME_TO_ZAP );
		
		if (hit( this, enemy, true )) {
			//TODO would be nice for this to work on ghost/statues too
			if (enemy == Dungeon.hero && Random.Int( 2 ) == 0) {
				Buff.prolong( enemy, Degrade.class, Degrade.DURATION );
				Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
			}
			
			int dmg = Random.NormalIntRange( 12, 18 );
            switch (Dungeon.cycle) {
                case 1: dmg = Random.NormalIntRange(64, 83); break;
                case 2: dmg = Random.NormalIntRange(276, 400); break;
                case 3: dmg = Random.NormalIntRange(1400, 1621); break;

            }
			enemy.damage( dmg, new DarkBolt() );
			
			if (enemy == Dungeon.hero && !enemy.isAlive()) {
				Dungeon.fail( getClass() );
				GLog.n( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}
	
	public void onZapComplete() {
		zap();
		next();
	}
	
	@Override
	public void call() {
		next();
	}

	@Override
	public Item createLoot(){

		// 1/6 chance for healing, scaling to 0 over 8 drops
		if (Random.Int(2) == 0 && Random.Int(8) > Dungeon.LimitedDrops.WARLOCK_HP.count ){
			Dungeon.LimitedDrops.WARLOCK_HP.drop();
			return new PotionOfHealing();
		} else {
			Item i = Generator.random(Generator.Category.POTION);
			int healingTried = 0;
			while (i instanceof PotionOfHealing){
				healingTried++;
				i = Generator.random(Generator.Category.POTION);
			}

			//return the attempted healing potion drops to the pool
			if (healingTried > 0){
				for (int j = 0; j < Generator.Category.POTION.classes.length; j++){
					if (Generator.Category.POTION.classes[j] == PotionOfHealing.class){
						Generator.Category.POTION.probs[j] += healingTried;
					}
				}
			}

			return i;
		}

	}
}
