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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.traits.PreparationAllowed;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class FantasmalStabber extends BlacksmithWeapon implements PreparationAllowed {
    {
        image = ItemSpriteSheet.PHANTDAGGER;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 0.9f;

        DLY = 1/5f;
    }

    @Override
    public long min(long lvl) {
        return super.min(lvl)/4;
    }

    @Override
    public long max(long lvl) {
        return super.max(lvl)/4;
    }

    @Override
    public long proc(Char attacker, Char defender, long damage) {
        long dmg = super.proc(attacker, defender, damage);
        dmg += defender.drRoll();
        if (attacker instanceof Hero){
            for (Buff b : attacker.buffs()) {
                if (b instanceof Artifact.ArtifactBuff) {
                    if (!((Artifact.ArtifactBuff) b).isCursed()) {
                        ((Artifact.ArtifactBuff) b).charge((Hero) attacker, 0.3f);
                    }
                }
                for (Wand.Charger c : attacker.buffs(Wand.Charger.class)){
                    c.gainCharge(0.15f);
                }
            }
        }
        return dmg;
    }

    @Override
    public long damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                //deals 50% toward max to max on surprise, instead of min to max.
                long diff = max() - min();
                long damage = augment.damageFactor(Random.NormalLongRange(
                        min() + Math.round(diff*0.50f),
                        max()));
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Dungeon.IntRange(0, exStr);
                }
                return damage;
            }
        }
        return super.damageRoll(owner);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    public boolean useTargeting(){
        return false;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        Dagger.sneakAbility(hero, target, 10, this);
    }
}
