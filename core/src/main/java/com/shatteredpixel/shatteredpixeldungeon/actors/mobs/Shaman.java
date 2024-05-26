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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShamanSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public abstract class Shaman extends Mob {
	
	{
		HP = HT = 35;
		defenseSkill = 15;
		
		EXP = 8;
		maxLvl = 16;
		
		loot = Generator.Category.WAND;
		lootChance = 0.03f; //initially, see rollToDropLoot
        switch (Dungeon.cycle){
            case 1:
                HP = HT = 375;
                defenseSkill = 55;
                EXP = 41;
                break;
            case 2:
                HP = HT = 5121;
                defenseSkill = 221;
                EXP = 412;
                break;
            case 3:
                HP = HT = 97500;
                defenseSkill = 525;
                EXP = 3900;
                break;
            case 4:
                HP = HT = 8000000;
                defenseSkill = 3300;
                EXP = 120000;
                break;
			case 5:
				HP = HT = 1600000000;
				defenseSkill = 57500;
				EXP = 36000000;
				break;
        }
	}
	
	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Char.combatRoll(43, 58);
            case 2: return Char.combatRoll(235, 265);
            case 3: return Char.combatRoll(880, 1200);
            case 4: return Char.combatRoll(18000, 27500);
			case 5: return Char.combatRoll(1500000, 2250000);
        }
		return Char.combatRoll( 5, 10 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 75;
            case 2: return 275;
            case 3: return 735;
            case 4: return 3700;
			case 5: return 56500;
        }
		return 18;
	}
	
	@Override
	public long cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Char.combatRoll(18, 36);
            case 2: return Char.combatRoll(70, 198);
            case 3: return Char.combatRoll(450, 790);
            case 4: return Char.combatRoll(10000, 26000);
			case 5: return Char.combatRoll(850000, 1450000);
        }
		return Char.combatRoll(0, 6);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy)
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/33, 1/100, 1/300, 1/900, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.SHAMAN_WAND.count);
	}

	@Override
	public Item createLoot() {
		Dungeon.LimitedDrops.SHAMAN_WAND.count++;
		return super.createLoot();
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
	public static class EarthenBolt{}
	
	private void zap() {
		spend( 1f );

		Invisibility.dispel(this);
		Char enemy = this.enemy;
		if (hit( this, enemy, true )) {
			
			if (Random.Int( 2 ) == 0) {
				debuff( enemy );
				if (enemy == Dungeon.hero) Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
			}
			
			long dmg = Char.combatRoll( 6, 15 );
            switch (Dungeon.cycle) {
                case 1: dmg = Char.combatRoll(48, 62); break;
                case 2: dmg = Char.combatRoll(250, 324); break;
                case 3: dmg = Char.combatRoll(895, 1200); break;
                case 4: dmg = Char.combatRoll(17000, 26000); break;
				case 5: dmg = Char.combatRoll(1400000, 2150000); break;
            }
			dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
			enemy.damage( dmg, new EarthenBolt() );
			
			if (!enemy.isAlive() && enemy == Dungeon.hero) {
				Badges.validateDeathFromEnemyMagic();
				Dungeon.fail( this );
				GLog.n( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}
	
	protected abstract void debuff( Char enemy );
	
	public void onZapComplete() {
		zap();
		next();
	}
	
	@Override
	public String description() {
		return super.description() + "\n\n" + Messages.get(this, "spell_desc");
	}
	
	public static class RedShaman extends Shaman {
		{
			spriteClass = ShamanSprite.Red.class;
		}
		
		@Override
		protected void debuff( Char enemy ) {
			Buff.prolong( enemy, Weakness.class, Weakness.DURATION );
		}
	}
	
	public static class BlueShaman extends Shaman {
		{
			spriteClass = ShamanSprite.Blue.class;
		}
		
		@Override
		protected void debuff( Char enemy ) {
			Buff.prolong( enemy, Vulnerable.class, Vulnerable.DURATION );
		}
	}
	
	public static class PurpleShaman extends Shaman {
		{
			spriteClass = ShamanSprite.Purple.class;
		}
		
		@Override
		protected void debuff( Char enemy ) {
			Buff.prolong( enemy, Hex.class, Hex.DURATION );
		}
	}

	public static Class<? extends Shaman> random(){
		float roll = Random.Float();
		if (roll < 0.4f){
			return RedShaman.class;
		} else if (roll < 0.8f){
			return BlueShaman.class;
		} else {
			return PurpleShaman.class;
		}
	}
}
