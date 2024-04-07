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

package com.shatteredpixel.shatteredpixeldungeon.items.fishingrods;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Hook;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public abstract class FishingRod extends Item {
    public static final String AC_CAST = "CAST";
    public static final String AC_UNCAST = "UNCAST";

    {
        identify();
        defaultAction = AC_CAST;
        unique = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (!hook) actions.add(AC_CAST);
        else actions.add(AC_UNCAST);
        return actions;
    }

    public float amplifier = 1f;
    public int baseFishingStr = 1;
    public int bonusFishingStr = 0;
    public int tier;
    public boolean hook;

    public float fishingPower(){
        return level()*amplifier;
    }

    public int fishingStrength(){
        return baseFishingStr + bonusFishingStr;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return true;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_CAST) && !hook){
            GameScene.selectCell(caster);
        }
        if (action.equals(AC_UNCAST) && hook){
            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                if (Dungeon.level.heroFOV[mob.pos] && mob instanceof Hook) {
                    for (Item b : ((Hook) mob).items) b.cast(mob, hero.pos);
                    mob.die(new Doom());
                    hook = false;
                    defaultAction = AC_CAST;
                }
            }
        }
    }

    private CellSelector.Listener caster = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {
            if (target != null && (Dungeon.level.heroFOV[target])) {

                PathFinder.buildDistanceMap(target, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
                if (PathFinder.distance[curUser.pos] == Integer.MAX_VALUE) {
                    GLog.w(Messages.get(FishingRod.class, "cant_reach"));
                    return;
                }

                final Ballistica hooking = new Ballistica(curUser.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);

                if (!(Dungeon.level.water[hooking.collisionPos] && !Dungeon.level.solid[hooking.collisionPos])) {
                    GLog.w(Messages.get(FishingRod.class, "no_water"));
                    return;
                }
                throwSound();
                Sample.INSTANCE.play(Assets.Sounds.CHAINS);
                hook = true;
                MagicMissile.boltFromChar(Dungeon.hero.sprite.parent, MagicMissile.MAGIC_MISSILE, Dungeon.hero.sprite, target, new Callback(){

                    @Override
                    public void call() {
                        Hook hook = new Hook();
                        hook.tier = tier;
                        hook.tries = fishingStrength();
                        hook.power = (int) fishingPower();
                        GameScene.add(hook);
                        ScrollOfTeleportation.appear(hook, target);
                        defaultAction = AC_UNCAST;
                    }
                });



            } else if (target != null && !(Dungeon.level.heroFOV[target])){
                GLog.w(Messages.get(FishingRod.class, "cant_see"));
            }

        }

        @Override
        public String prompt() {
            return Messages.get(FishingRod.class, "prompt");
        }
    };

    @Override
    public Item upgrade() {
        super.upgrade();
        bonusFishingStr = (int) Math.min(Integer.MAX_VALUE, level() / 5);
        return this;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("hook", hook);
        bundle.put("amp", bonusFishingStr);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        hook = bundle.getBoolean("hook");
        if (hook) defaultAction = AC_UNCAST;
        else defaultAction = AC_CAST;
        bonusFishingStr = bundle.getInt("amp");
    }

    @Override
    public long visiblyUpgraded() {
        return level();
    }

    @Override
    public long buffedVisiblyUpgraded() {
        return level();
    }

    @Override
    public String desc() {
        String desc = super.desc();
        desc += "\n\n" + Messages.get(FishingRod.class, "basics", Math.round(fishingPower()*100), fishingStrength());
        return desc;
    }

    @Override
    public long value() {
        return 60 * Dungeon.escalatingDepth() / 5;
    }
}
