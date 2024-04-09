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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Perks {
    public enum Perk {
        SUCKER_PUNCH,
        DIRECTIVE,
        FOLLOW_UP_STRIKE,
        HOLD_FAST{
            public String desc() {
                return Messages.get(Perks.class, name() + ".desc", HoldFast.minArmor(), HoldFast.armor());
            }
        },
        PROTEIN_INFUSION,
        FISHING_PRO,
        WAND_PRO,
        IRON_WILL,
        MYSTICAL_MEAL,
        ADDITIONAL_MONEY,
        BETTER_BARTERING,
        POTIONS,
        MORE_BAG,
        NO_ONESHOTS,
        COLLECT_EVERYTHING,
        RAT_SUMMONS,
        GRASS_HEALING,
        FRIENDLY_BEES;

        public String desc() {
            return Messages.get(Perks.class, name() + ".desc");
        }

        @Override
        public String toString() {
            return Messages.titleCase(Messages.get(Perks.class, name() + ".name"));
        }
    }

    public static abstract class Cooldown extends FlavourBuff {
        public static <T extends Cooldown> void affectHero(Class<T> cls) {
            if(cls == Cooldown.class) return;
            T buff = Buff.affect(Dungeon.hero, cls);
            buff.spend( buff.duration() );
        }
        public abstract float duration();
        public float iconFadePercent() { return Math.max(0, visualcooldown() / duration()); }
        public String toString() { return Messages.get(this, "name"); }
        public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
    }
    public static class SuckerPunchTracker extends Buff{}
    public static class DirectiveTracker extends FlavourBuff {}
    public static class FollowupStrikeTracker extends Buff{};
    public static class DirectiveMovingTracker extends CounterBuff {
        public int duration() { return 3;}
        public float iconFadePercent() { return Math.max(0, 1f - ((count()) / (duration()))); }
        public String toString() { return Messages.get(this, "name"); }
        public String desc() { return Messages.get(this, "desc", dispTurns(duration() - (count()))); }
        public int icon() { return BuffIndicator.MOMENTUM; }
        public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.7f, 0.5f); }
    }

    public static long onAttackProc(Hero hero, Char enemy, long damage){
        if (hero.perks.contains(Perk.SUCKER_PUNCH)
                && enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
                && enemy.buff(SuckerPunchTracker.class) == null){
            damage += hero.damageRoll()/4;
            Buff.affect(enemy, SuckerPunchTracker.class);
        }
        if (hero.perks.contains(Perk.DIRECTIVE)
                && enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
                && enemy.buff(DirectiveTracker.class) == null){
            Buff.affect(hero, Haste.class, 1.5f);
        }
        if (hero.perks.contains(Perk.FOLLOW_UP_STRIKE)) {
            if (hero.belongings.weapon instanceof MissileWeapon) {
                Buff.affect(enemy, FollowupStrikeTracker.class);
            } else if (enemy.buff(FollowupStrikeTracker.class) != null){
                damage += hero.damageRoll()/4;
                if (!(enemy instanceof Mob) || !((Mob) enemy).surprisedBy(hero)){
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
                }
                enemy.buff(FollowupStrikeTracker.class).detach();
            }
        }
        return damage;
    }

    public static int nextPerkLevel(){
        int num = 5;
        for (int i = 0; i < Dungeon.hero.perks.size(); i++){
            num += 5 + i;
        }
        return num;
    }

    public static void debugEarnPerk(String string){
        Perk perk = Perk.valueOf(string);
        Dungeon.hero.perks.add(perk);
        GLog.p(Messages.get(Perks.class, "perk_obtain", perk.toString()));
        if (Dungeon.hero.sprite != null)
            Dungeon.hero.sprite.emitter().burst(Speck.factory(Speck.STAR), 20);
    }

    public static void earnPerk(Hero hero){
        if (hero.perks.size() < Perk.values().length && hero.lvl == nextPerkLevel()){
            Perk perk;
            do {
                perk = Random.element(Perk.values());
            } while (hero.perks.contains(perk));
            hero.perks.add(perk);
            GLog.p(Messages.get(Perks.class, "perk_obtain", perk.toString()));
            if (hero.sprite != null)
            hero.sprite.emitter().burst(Speck.factory(Speck.STAR), 20);
        }
    }

    public static void storeInBundle(Bundle bundle, ArrayList<Perk> perks) {
        ArrayList<String> conductIds = new ArrayList<>();
        for (Perk conduct: perks){
            conductIds.add(conduct.name());
        }
        bundle.put("perks", conductIds.toArray(new String[0]));
    }

    public static void restoreFromBundle(Bundle bundle, ArrayList<Perk> perks) {
        perks.clear();
        if (bundle.getStringArray("perks") != null) {
            String[] conductIds = bundle.getStringArray("perks");
            for (String conduct : conductIds) {
                perks.add(Perk.valueOf(conduct));
            }
        }
    }
}
