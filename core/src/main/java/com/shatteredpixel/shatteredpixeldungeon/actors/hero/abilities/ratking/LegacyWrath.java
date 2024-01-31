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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ratking;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.HuntressArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MageArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

import java.util.HashMap;
import java.util.Map;

public class LegacyWrath extends ArmorAbility {

    {
        baseChargeUse = 100;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(SmokeBomb.class, "prompt");
    }

    private static final float JUMP_DELAY=2f;

    @Override
    public void activate(ClassArmor armor, Hero hero, Integer target) {
        if(target == null) return;

        boolean[] stages = new boolean[3]; // jump/molten/blades

        if( stages[0] = target != hero.pos ) {
            if( !SmokeBomb.isValidTarget(hero, target, 6) ) return;

            if (Actor.findChar(target) != null) { // use heroic leap mechanics instead.
                Ballistica route = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET);
                //can't occupy the same cell as another char, so move back one until it is valid.
                int i = 0;
                while (Actor.findChar(target) != null && target != hero.pos) {
                    target = route.path.get(route.dist - ++i);
                }
            }

            SmokeBomb.blindAdjacentMobs(hero);
            hero.sprite.turnTo(hero.pos, target);
            SmokeBomb.throwSmokeBomb(hero, target);
            hero.move(target);
            CellEmitter.center(hero.pos).burst(Speck.factory(Speck.DUST), 10);
            Camera.main.shake(2, 0.5f);
        }
        // now do mage
        if(stages[1] = MageArmor.doMoltenEarth()) {
            Dungeon.observe();
            hero.sprite.remove(CharSprite.State.INVISIBLE); // you still benefit from initial invisibiilty, even if you can't see it visually.
            hero.sprite.operate(hero.pos,()->{}); // handled.
            MageArmor.playMoltenEarthFX();
        }
        // warrior. this is delayed so the burst burning damage doesn't cancel this out instantly.
        if(stages[0]) for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            Char mob = Actor.findChar(hero.pos + PathFinder.NEIGHBOURS8[i]);
            if (mob != null && mob != hero && mob.alignment != Char.Alignment.ALLY) {
                Buff.prolong(mob, Paralysis.class, 2);
            }
        }
        // huntress
        HashMap<Callback, Mob> targets = new HashMap<>();
        for (Mob mob : Dungeon.level.mobs) {
            if (Dungeon.level.distance(hero.pos, mob.pos) <= 10
                    && Dungeon.level.heroFOV[mob.pos]
                    && mob.alignment == Char.Alignment.ENEMY) {

                Callback callback = new Callback() {
                    @Override
                    public void call() {
                        hero.attack( targets.get( this ) );
                        Invisibility.dispel();
                        targets.remove( this );
                        if (targets.isEmpty()) finish(armor, hero, stages);
                    }
                };
                targets.put( callback, mob );
            }
        }
        // this guarentees proper sequence of events for spectral blades
        if ( stages[2] = targets.size() > 0 ) {
            // turn towards the average point of all enemies being shot at.
            Point sum = new Point(); for(Mob mob : targets.values()) sum.offset(Dungeon.level.cellToPoint(mob.pos));
            sum.scale(1f/targets.size());
            // wait for user sprite to finish doing what it's doing, then start shooting.
            hero.sprite.doAfterAnim( () -> hero.sprite.zap(Dungeon.level.pointToCell(sum), ()->{
                Shuriken proto = new Shuriken();
                for(Map.Entry<Callback, Mob> entry : targets.entrySet())
                    ( (MissileSprite)hero.sprite.parent.recycle( MissileSprite.class ) )
                            .reset( hero.sprite, entry.getValue().pos, proto, entry.getKey() );
            }));
            hero.busy();
        } else { // still need to finish, but also need to indicate that there was no enemies to shoot at.
            if( stages[1] ) Invisibility.dispel();
            else GLog.w( Messages.get(HuntressArmor.class, "no_enemies") );
            finish(armor, hero, stages);
        }
    }

    private void finish(ClassArmor armor, Hero hero, boolean[] stages) {
        hero.sprite.doAfterAnim(hero.sprite::idle); // because I overrode the default behavior I need to do this.

        int delay = 0;
        if(stages[1]) delay++;
        if(stages[2]) delay += hero.attackDelay();
        if(stages[0]) Actor.addDelayed(new Actor() {
            { actPriority = HERO_PRIO; } // this is basically the hero acting.
            @Override
            protected boolean act() {
                Buff.prolong(hero, Invisibility.class, Invisibility.DURATION/4f);
                remove(this);
                return true;
            }
        },(delay += JUMP_DELAY)-1);

        hero.spendAndNext(delay);
        for(boolean stage : stages) if(stage) { armor.useCharge(hero, this); return; }
    }

    @Override public int icon() { return HeroIcon.LEGACYWRATH; }

    @Override
    public Talent[] talents() {
        return new Talent[0];
    }
}

