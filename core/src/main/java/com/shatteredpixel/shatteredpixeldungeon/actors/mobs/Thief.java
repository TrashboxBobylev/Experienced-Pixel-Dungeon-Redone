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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ThiefSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Thief extends Mob {
	
	public Item item;
	
	{
		spriteClass = ThiefSprite.class;
		
		HP = HT = 20;
		defenseSkill = 12;
		
		EXP = 5;
		maxLvl = 11;

		loot = Random.oneOf(Generator.Category.RING, Generator.Category.ARTIFACT);
		lootChance = 0.03f; //initially, see lootChance()

		WANDERING = new Wandering();
		FLEEING = new Fleeing();

		properties.add(Property.UNDEAD);

        switch (Dungeon.cycle){
            case 1:
                HP = HT = 190;
                defenseSkill = 39;
                EXP = 24;
                break;
            case 2:
                HP = HT = 2031;
                defenseSkill = 179;
                EXP = 237;
                break;
            case 3:
                HP = HT = 52000;
                defenseSkill = 450;
                EXP = 2000;
                break;
            case 4:
                HP = HT = 4000000;
                defenseSkill = 2300;
                EXP = 57000;
                break;
			case 5:
				HP = HT = 900000000;
				defenseSkill = 50000;
				EXP = 23000000;
				break;
        }
	}

	private static final String ITEM = "item";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ITEM, item );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		item = (Item)bundle.get( ITEM );
	}

	@Override
	public float speed() {
		if (item != null) return (5*super.speed())/6;
		else return super.speed();
	}

	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Char.combatRoll(32, 47);
            case 2: return Char.combatRoll(160, 208);
            case 3: return Char.combatRoll(550, 901);
            case 4: return Char.combatRoll(9500, 14000);
			case 5: return Char.combatRoll(600000, 1070000);
        }
		return Char.combatRoll( 1, 10 );
	}

	@Override
	public float attackDelay() {
		return super.attackDelay()*0.5f;
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/33, 1/100, 1/300, 1/900, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.THEIF_MISC.count);
	}

	@Override
	public void rollToDropLoot() {
		if (item != null) {
			Dungeon.level.drop( item, pos ).sprite.drop();
			//updates position
			if (item instanceof Honeypot.ShatteredPot) ((Honeypot.ShatteredPot)item).dropPot( this, pos );
			item = null;
		}
		super.rollToDropLoot();
	}

	@Override
	public Item createLoot() {
		Dungeon.LimitedDrops.THEIF_MISC.count++;
		return super.createLoot();
	}

	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 50;
            case 2: return 238;
            case 3: return 660;
            case 4: return 2600;
			case 5: return 38500;
        }
		return 12;
	}

	@Override
	public long cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Char.combatRoll(8, 20);
            case 2: return Char.combatRoll(60, 137);
            case 3: return Char.combatRoll(380, 625);
            case 4: return Char.combatRoll(5000, 10000);
			case 5: return Char.combatRoll(56000, 720000);
        }
		return Char.combatRoll(0, 3);
	}

	@Override
	public long attackProc( Char enemy, long damage ) {
		damage = super.attackProc( enemy, damage );
		
		if (alignment == Alignment.ENEMY && item == null
				&& enemy instanceof Hero && steal( (Hero)enemy )) {
			state = FLEEING;
		}

		return damage;
	}

	@Override
	public long defenseProc(Char enemy, long damage) {
		if (state == FLEEING) {
			Dungeon.level.drop( new Gold(), pos ).sprite.drop();
		}

		return super.defenseProc(enemy, damage);
	}

	protected boolean steal( Hero hero ) {

		Item toSteal = hero.belongings.randomUnequipped();

		if (toSteal != null && !toSteal.unique && toSteal.level() < 1 ) {

			GLog.w( Messages.get(Thief.class, "stole", toSteal.name()) );
			if (!toSteal.stackable) {
				Dungeon.quickslot.convertToPlaceholder(toSteal);
			}
			Item.updateQuickslot();

			item = toSteal.detach( hero.belongings.backpack );
			if (item instanceof Honeypot){
				item = ((Honeypot)item).shatter(this, this.pos);
			} else if (item instanceof Honeypot.ShatteredPot) {
				((Honeypot.ShatteredPot)item).pickupPot(this);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public String description() {
		String desc = super.description();

		if (item != null) {
			desc += Messages.get(this, "carries", item.name() );
		}

		return desc;
	}
	
	private class Wandering extends Mob.Wandering {
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			super.act(enemyInFOV, justAlerted);
			
			//if an enemy is just noticed and the thief posses an item, run, don't fight.
			if (state == HUNTING && item != null){
				state = FLEEING;
			}
			
			return true;
		}
	}

	private class Fleeing extends Mob.Fleeing {
		@Override
		protected void escaped() {
			if (item != null
					&& !Dungeon.level.heroFOV[pos]
					&& Dungeon.level.distance(Dungeon.hero.pos, pos) >= 6) {

				int count = 32;
				int newPos;
				do {
					newPos = Dungeon.level.randomRespawnCell( Thief.this );
					if (count-- <= 0) {
						break;
					}
				} while (newPos == -1 || Dungeon.level.heroFOV[newPos] || Dungeon.level.distance(newPos, pos) < (count/3));

				if (newPos != -1) {

					pos = newPos;
					sprite.place( pos );
					sprite.visible = Dungeon.level.heroFOV[pos];
					if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

				}

				if (item != null) GLog.n( Messages.get(Thief.class, "escapes", item.name()));
				item = null;
				state = WANDERING;
			} else {
				state = WANDERING;
			}
		}
	}
}
