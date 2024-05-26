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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Greatsword extends MeleeWeapon {

	{
		image = ItemSpriteSheet.GREATSWORD;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		internalTier = tier = 5;
	}

    @Override
    public long max(long lvl) {
        return  6L*(tier()+1) +    //36
                lvl*(tier()+2);   //+7
    }

    @Override
    public String statsInfo() {
        return Messages.get(this, "stats_desc", 9 + Dungeon.escalatingDepth() * 3);
    }

    @Override
    public float abilityChargeUse(Hero hero, Char target) {
        return 2*super.abilityChargeUse(hero, target);
    }

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "ability_no_target"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        if (!hero.canAttack(enemy)){
            GLog.w(Messages.get(this, "ability_bad_position"));
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;

        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);
                if (hero.attack(enemy, 0.5f, 0f, 1f)){
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                }
                for (Char mob: Dungeon.level.mobs){
                    if (mob instanceof GuardianKnight){
                        int pos = 0, count = 0;
                        do {
                            if (++count > 30) {
                                break;
                            }
                            pos = Random.Int(Dungeon.level.length());
                        } while (!Dungeon.level.heroFOV[pos]
                                || !Dungeon.level.passable[pos]
                                || Actor.findChar( pos ) != null);

                        if (count > 30) continue;

                        ScrollOfTeleportation.teleportToLocation(mob, pos);
                    }
                }

                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                if (!enemy.isAlive()){
                    onAbilityKill(hero, enemy);
                }
                afterAbilityUsed(hero);
            }
        });
	}

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(Math.round(min()*0.5d)), augment.damageFactor(Math.round(max()*0.5d)));
        } else {
            return Messages.get(this, "typical_ability_desc", augment.damageFactor(Math.round(min(0)*0.5d)), augment.damageFactor(Math.round(max(0)*0.5d)));
        }
    }

    @Override
    public long proc(Char attacker, Char defender, long damage) {
        for (int i : PathFinder.NEIGHBOURS9){

            if (!Dungeon.level.solid[attacker.pos + i]
                    && !Dungeon.level.pit[attacker.pos + i]
                    && Actor.findChar(attacker.pos + i) == null
                    && attacker == Dungeon.hero) {

                GuardianKnight guardianKnight = new GuardianKnight();
                Greatsword copy = new Greatsword();
                copy.level(level());
                copy.enchant(enchantment);
                copy.augment = augment;
                guardianKnight.weapon = copy;
                guardianKnight.pos = attacker.pos + i;
                guardianKnight.aggro(defender);
                GameScene.add(guardianKnight);
                Dungeon.level.occupyCell(guardianKnight);

                CellEmitter.get(guardianKnight.pos).burst(Speck.factory(Speck.EVOKE), 4);
                break;
            }
        }
        return super.proc(attacker, defender, damage);
    }

    public static class GuardianKnight extends Statue {
        {
            state = WANDERING;
            spriteClass = GuardianSprite.class;
            alignment = Alignment.ALLY;
            Buff.affect(this, TickDebuff.class);

            levelGenStatue = false;
        }

        public GuardianKnight() {
            HP = HT = 9 + Dungeon.escalatingDepth() * 4;
            defenseSkill = 4 + Dungeon.escalatingDepth();
        }

        public static class TickDebuff extends Buff {

            private double partialHP = 0;

            private final String PART_HP = "partialHP";

            @Override
            public void storeInBundle(Bundle bundle) {
                super.storeInBundle(bundle);
                bundle.put(PART_HP, partialHP);
            }

            @Override
            public void restoreFromBundle(Bundle bundle) {
                super.restoreFromBundle(bundle);
                if (bundle.contains(PART_HP))
                    partialHP = bundle.getDouble(PART_HP);
            }

            @Override
            public boolean act() {
                if (target.isAlive()) {
                    float regen = 1f / (target.HT / 150f);
                    if (target.HP > 0) {
                        partialHP += regen;
                        if (partialHP >= 1){
                            target.damage((long) partialHP, new Doom());
                            partialHP -= (long)partialHP;
                        }
                    }
                    spend( TICK );
                } else {
                    diactivate();
                }
                return true;
            }
        }

        @Override
        public void die(Object cause) {
            weapon = null;
            super.die(cause);
        }

        @Override
        public long drRoll() {
            return Dungeon.escalatingDepth();
        }
    }

    public static class GuardianSprite extends StatueSprite {

        public GuardianSprite(){
            super();
            tint(1, 0, 0, 0.4f);
        }

        @Override
        public void resetColor() {
            super.resetColor();
            tint(1, 0, 0, 0.4f);
        }
    }
}
