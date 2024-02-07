/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AnkhInvulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.BiggerGambleBag;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.OofSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class OOFThief extends Mob {

	public int counter;
	protected boolean hasRaged = false;
	
	{
		spriteClass = OofSprite.class;
		
		HP = HT = 50;
		defenseSkill = 18;
		baseSpeed = 3f;
		
		EXP = 0;
		maxLvl = 11;

		loot = BiggerGambleBag.class;
		lootChance = 0.5f; //initially, see lootChance()

		properties.add(Property.UNDEAD);

        switch (Dungeon.cycle){
            case 1:
                HP = HT = 425;
                defenseSkill = 39;
                break;
            case 2:
                HP = HT = 4225;
                defenseSkill = 215;
                break;
            case 3:
                HP = HT = 110000;
                defenseSkill = 650;
                break;
            case 4:
                HP = HT = 10000000;
                defenseSkill = 4500;
                break;
        }
	}

	private static final String COUNT = "counter";
	private static final String HAS_RAGED = "has_raged";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put(COUNT, counter);
		bundle.put(HAS_RAGED, hasRaged);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		counter = bundle.getInt(COUNT);
		hasRaged = bundle.getBoolean(HAS_RAGED);
	}

	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Random.NormalIntRange(53, 70);
            case 2: return Random.NormalIntRange(255, 320);
            case 3: return Random.NormalIntRange(905, 1455);
            case 4: return Random.NormalIntRange(14300, 21500);
        }
		return Random.NormalIntRange( 8, 14 );
	}

	@Override
	public float attackDelay() {
		float v = super.attackDelay() * 0.35f;
		if (hasRaged) v /= 2f;
		return v;
	}

	@Override
	public float speed() {
		float v = super.speed();
		if (hasRaged) v *= 2f;
		return v;
	}

	@Override
	public float lootChance() {
		return super.lootChance() * (float)Math.pow(5/6f, Dungeon.LimitedDrops.OOF_DROP.count);
	}

	@Override
	public Item createLoot() {
		Dungeon.LimitedDrops.OOF_DROP.count++;
		return super.createLoot();
	}

	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 80;
            case 2: return 375;
            case 3: return 1000;
            case 4: return 4222;
        }
		return 18;
	}

	@Override
	public int cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Random.NormalIntRange(15, 40);
            case 2: return Random.NormalIntRange(110, 250);
            case 3: return Random.NormalIntRange(575, 1100);
            case 4: return Random.NormalIntRange(15000, 24000);
        }
		return Random.NormalIntRange(4, 8);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);

		if (cause == Chasm.class){
			hasRaged = true; //don't let enrage trigger for chasm deaths
		}
	}

	@Override
	public synchronized boolean isAlive() {
        if (!super.isAlive()) {
            if (!hasRaged) {
                triggerEnrage();
            } else {
				return super.isAlive();
			}
        }
        return true;
    }

	protected void triggerEnrage(){
		HP = 1;
		Buff.affect(this, AnkhInvulnerability.class, 3f);
		if (Dungeon.level.heroFOV[pos]) {
			SpellSprite.show( this, SpellSprite.BERSERK);
		}
		hasRaged = true;
	}

	@Override
	public boolean isInvulnerable(Class effect) {
		return super.isInvulnerable(effect) || buff(AnkhInvulnerability.class) != null;
	}

	private static final int STEAL_COUNT = 3;

	@Override
	public long attackProc( Char enemy, long damage ) {
		damage = super.attackProc( enemy, damage );
		counter++;

		if (counter >= STEAL_COUNT && counter % STEAL_COUNT == 0 && alignment == Alignment.ENEMY
				&& enemy instanceof Hero){
			steal( (Hero)enemy );
		}
		
		if (counter >= STEAL_COUNT*4 && counter % (4*STEAL_COUNT) == 0) {
			if (Dungeon.hero.fieldOfView[pos])
				CellEmitter.get( pos ).start( Speck.factory( Speck.STEAM ), 0.05f, 20 );

			int count = 64;
			int newPos;
			do {
				newPos = Dungeon.level.randomRespawnCell( this );
				if (count-- <= 0) {
					break;
				}
			} while (newPos == -1 || Dungeon.level.heroFOV[newPos] || Dungeon.level.distance(newPos, pos) < (count/3));

			if (newPos != -1) {

				pos = newPos;
				sprite.place( pos );
				sprite.visible = Dungeon.level.heroFOV[pos];
				if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);
				GLog.n( Messages.get(OOFThief.class, "escapes"));
				state = WANDERING;
			}
		}

		return damage;
	}

	protected boolean steal( Hero hero ) {

		Item toSteal = hero.belongings.randomUnequipped();

		if (toSteal != null) {

			hero.sprite.showStatus(CharSprite.DEFAULT, "oof");
			GLog.w( Messages.get(Thief.class, "stole", toSteal.name()) );
			if (!toSteal.stackable) {
				Dungeon.quickslot.convertToPlaceholder(toSteal);
			}
			Item.updateQuickslot();

			Dungeon.oofedItems.add(toSteal.detach( hero.belongings.backpack ));
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean getCloser(int target) {
		if (Random.Int(3) == 0) {
			if (Dungeon.level.heroFOV[pos]) {
				sprite.emitter().burst(Speck.factory(Speck.DUST), 2);
			}

			return true;
		}

		return super.getCloser(target);
	}

	@Override
	protected boolean getFurther(int target) {
		if (Random.Int(3) == 0) {
			if (Dungeon.level.heroFOV[pos]) {
				sprite.emitter().burst(Speck.factory(Speck.DUST), 2);
			}

			return true;
		}

		return super.getFurther(target);
	}
}
