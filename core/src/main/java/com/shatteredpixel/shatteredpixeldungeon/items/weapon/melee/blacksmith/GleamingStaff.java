/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2019-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.blacksmith;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.GambleBag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class GleamingStaff extends BlacksmithWeapon {

    {
        image = ItemSpriteSheet.GREEDSTAFF;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 1f;

        DLY = 1f;
    }

    public int counter = 0;

    @Override
    public long min(long lvl) {
        return Math.round(super.min(lvl)*0.6f);
    }

    @Override
    public long max(long lvl) {
        return Math.round(super.max(lvl)*0.75f);
    }

    @Override
    public long defenseFactor( Char owner ) {
        long defense = tier + Dungeon.cycle * 5 + (buffedLvl() * (1 + Dungeon.cycle));
        if (owner.buff(GuardTracker.class) != null)
            return defense * 2;
        return defense;	//2 extra defence
    }

    public String statsInfo(){
        return Messages.get(this, "stats_desc", tier+Dungeon.cycle*5 + (buffedLvl()*(1+Dungeon.cycle)), COMBO_COUNT-1);
    }

    private static final int COMBO_COUNT = 21;

    @Override
    public long proc(Char attacker, Char defender, long damage) {
        counter++;
        Sample.INSTANCE.play( Assets.Sounds.GOLD, 2f, 1f + 3.5f * ((COMBO_COUNT * 1f - counter) / (COMBO_COUNT * 1f))  );
        if (counter >= COMBO_COUNT){
            counter = 0;
            defender.sprite.showStatus(0xF3F600, "!!!");
            Sample.INSTANCE.play( Assets.Sounds.BADGE );
            GambleBag lootbag = new GambleBag();
            defender.sprite.centerEmitter().start( Speck.factory( Speck.STAR ), 0.025f, 20);
            Dungeon.level.drop(lootbag, defender.pos).sprite.drop();
        } else {
            defender.sprite.showStatusWithIcon(0xF3F600, Integer.toString(COMBO_COUNT - counter), FloatingText.GOLD);
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        beforeAbilityUsed(hero, null);
        Buff.prolong(hero, GuardTracker.class, 9 * hero.attackDelay());
        hero.sprite.operate(hero.pos, () -> {
            hero.spendAndNext(10 * hero.attackDelay());
            hero.sprite.idle();
            afterAbilityUsed(hero);
        });
    }

    private static final String LOOT_COMBO         = "combo";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put(LOOT_COMBO, counter);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        counter = bundle.getInt(LOOT_COMBO);
    }

    public static class GuardTracker extends FlavourBuff {

        {
            announced = true;
            type = buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.DUEL_GUARD;
        }

        @Override
        public float iconFadePercent() {
            final int duration = target instanceof Hero ? Math.round(10 * ((Hero) target).attackDelay()) : 10;
            return Math.max(0, (duration - visualcooldown()) / duration);
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
            else target.sprite.remove(CharSprite.State.ILLUMINATED);
        }
    }
}
