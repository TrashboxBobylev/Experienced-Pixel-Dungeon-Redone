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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.*;

import java.util.ArrayList;

public class StarlightSmasher extends BlacksmithWeapon {

    {
        image = ItemSpriteSheet.STARHAMMER;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 4f;
        usesTargeting = true;

        ACC = 1.30f; //30% boost to accuracy
        DLY = 3;
    }

    public boolean starlight_power = false;

    @Override
    public long min(long lvl) {
        return super.min(lvl)*10;
    }

    @Override
    public long max(long lvl) {
        return super.max(lvl)*8;
    }

    @Override
    public long proc(Char attacker, Char defender, long damage) {
        attacker.sprite.centerEmitter().start( Speck.factory( Speck.STAR ), 0.05f, 10 );
        Buff.affect(attacker, Paralysis.class, Random.Int(2, 6));
        Buff.affect(defender, Paralysis.class, Random.Int(2, 4));
        if (!starlight_power){
            starlight_power = true;
            QuickSlotButton.refresh();
            defender.sprite.emitter().burst( MagicMissile.MagicParticle.FACTORY, 15);
            attacker.sprite.showStatus(0x66B3FF, Messages.get(this, "charging"));
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public String defaultAction() {
        if (starlight_power)
            return AC_THROW;
        return super.defaultAction();
    }

    public float castDelay(Char user, int dst ){
        if (starlight_power)
            return TIME_TO_THROW*5f;
        return super.castDelay(user, dst);
    }

    static int check = 0;

    @Override
    public void cast(Hero user, int dst) {
        final int cell = throwPos( user, dst );
        user.sprite.zap( cell );
        user.busy();

        throwSound();

        Char enemy = Actor.findChar( cell );
        QuickSlotButton.target(enemy);

        if (Dungeon.level.insideMap(cell)) {
            final float delay = castDelay(user, dst);
            final Item item = this;

            if (enemy != null && new Ballistica( user.pos, enemy.pos, Ballistica.PROJECTILE ).collisionPos == enemy.pos) {
                check = 0;
                for (int i: PathFinder.NEIGHBOURS9){
                    user.sprite.parent.add(new TargetedCell(enemy.pos + i, 0x66B3FF));
                }
                Sample.INSTANCE.play( Assets.Sounds.EVOKE, 1f, 0.8f  );
                ArrayList<Integer> lol = new ArrayList<>();
                for (int o: PathFinder.NEIGHBOURS9)
                    lol.add(o);
                ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                        reset(user.sprite,
                                enemy.sprite,
                                item,
                                new Callback() {
                                    @Override
                                    public void call() {
                                        curUser = user;
                                        for (int i: PathFinder.NEIGHBOURS9){
                                            int c = enemy.pos + i;
                                            if (c >= 0 && c < Dungeon.level.length()) {
                                                user.sprite.parent.add(new TargetedCell(c, 0xBAFFFF));
                                                MagicMissile missile = ((MagicMissile)enemy.sprite.parent.recycle( MagicMissile.class ));
                                                PointF baseBeginning = DungeonTilemap.raisedTileCenterToWorld( c );
                                                baseBeginning.offset(0f, -DungeonTilemap.SIZE*20f);
                                                int indexOfNeighbour = lol.indexOf(i);
                                                float offX = 0f, offY = 0f;
                                                switch (indexOfNeighbour){
                                                    case 0: case 3: case 6: offX = -1f; break;
                                                    case 2: case 5: case 8: offX = +1f; break;
                                                }
                                                switch (indexOfNeighbour){
                                                    case 0: case 1: case 2: offY = -1f; break;
                                                    case 6: case 7: case 8: offY = +1f; break;
                                                }

                                                baseBeginning.offset(DungeonTilemap.SIZE*offX, DungeonTilemap.SIZE*offY);
                                                missile.reset(MagicMissile.FROST_CONE, baseBeginning, DungeonTilemap.raisedTileCenterToWorld( c ), () -> {
                                                    Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );
                                                    Char target = Actor.findChar(c);
                                                    check += 1;
                                                    if (target != null && target.isAlive() && target.alignment != curUser.alignment){
                                                        target.sprite.burst(0xBAFFFF,  25);

                                                        int dmg = Math.round(damageRoll(curUser)*0.85f);

                                                        if (dmg > 0){
                                                            target.damage(dmg, new WandOfMagicMissile());
                                                            Buff.affect(target, Blindness.class, delay+2f);
                                                        }
                                                    }
                                                    if (check >= 9){
                                                        if (curUser.buff(Talent.LethalMomentumTracker.class) != null){
                                                            curUser.buff(Talent.LethalMomentumTracker.class).detach();
                                                            curUser.next();
                                                        } else {
                                                            curUser.spendAndNext(delay);
                                                        }
                                                        starlight_power = false;
                                                        QuickSlotButton.refresh();

                                                        Ballistica aim;
                                                        if (cell % Dungeon.level.width() > 10){
                                                            aim = new Ballistica(cell, cell - 1, Ballistica.WONT_STOP);
                                                        } else {
                                                            aim = new Ballistica(cell, cell + 1, Ballistica.WONT_STOP);
                                                        }
                                                        ConeAOE aoe = new ConeAOE(aim, 1.5f, 360, Ballistica.PROJECTILE);
                                                        for (Ballistica ray : aoe.rays) {
                                                            ((MagicMissile) user.sprite.parent.recycle(MagicMissile.class)).reset(
                                                                    MagicMissile.FROST,
                                                                    DungeonTilemap.tileCenterToWorld(cell),
                                                                    DungeonTilemap.tileCenterToWorld(ray.path.get(ray.dist)),
                                                                    null
                                                            );
                                                        }
                                                    }
                                                });

                                                missile.setSpeed(500f);
                                            }
                                        }
                                        Invisibility.dispel();
                                        ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                                                reset(cell, user.pos,
                                                        item, new Callback() {
                                                            @Override
                                                            public void call() {

                                                            }
                                                        });

                                    }
                                });
            } else {
                ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                        reset(user.sprite,
                                cell,
                                item,
                                new Callback() {
                                    @Override
                                    public void call() {
                                        curUser = user;
                                        user.spendAndNext(delay);
                                            Sample.INSTANCE.play(Assets.Sounds.MISS);
                                            ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                                                    reset(cell,
                                                            user.pos,
                                                            item, new Callback() {
                                                                @Override
                                                                public void call() {

                                                                }
                                                            });
                                    }
                                });
            }
        }
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (target == null){
            return;
        }

        int lungeCell = new Ballistica(hero.pos, target, Ballistica.PROJECTILE).collisionPos;

        PathFinder.buildDistanceMap( lungeCell, BArray.not( Dungeon.level.solid, null ), 3 );

        final int dest = lungeCell;
        hero.sprite.zap(dest);
        hero.busy();
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                hero.sprite.parent.add(new TargetedCell(i, 0xBAFFFF));
            }
        }
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        ((MissileSprite) hero.sprite.parent.recycle(MissileSprite.class)).
                reset(hero.sprite,
                        dest,
                        this,
                        new Callback() {
                            @Override
                            public void call() {
                                boolean starlight_check = starlight_power;
                                if (Dungeon.hero.fieldOfView != null && Dungeon.hero.fieldOfView[dest]){
                                    GameScene.flash(0x80FFFFFF);
                                }
                                beforeAbilityUsed(hero, null);
                                Sample.INSTANCE.play( Assets.Sounds.BLAST );
                                Invisibility.dispel();
                                for (int i = 0; i < PathFinder.distance.length; i++) {
                                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                                        hero.sprite.parent.add(new TargetedCell(i, 0xBAFFFF));
                                        Char targetCh = Actor.findChar(i);
                                        if (targetCh != null && targetCh.alignment != hero.alignment){
                                            int power = 16 - 4*Dungeon.level.distance(targetCh.pos, dest);
                                            if (power > 0) {
                                                Buff.prolong(targetCh, Blindness.class, power);
                                                Buff.prolong(targetCh, Cripple.class, power);
                                                if (starlight_check){
                                                    targetCh.sprite.burst(0xBAFFFF,  15);

                                                    int dmg = Math.round(damageRoll(curUser)*0.25f);

                                                    if (dmg > 0){
                                                        targetCh.damage(dmg, new WandOfMagicMissile());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Ballistica aim;
                                if (dest % Dungeon.level.width() > 10){
                                    aim = new Ballistica(dest, dest - 1, Ballistica.WONT_STOP);
                                } else {
                                    aim = new Ballistica(dest, dest + 1, Ballistica.WONT_STOP);
                                }
                                ConeAOE aoe = new ConeAOE(aim, 4f, 360, Ballistica.PROJECTILE);
                                for (Ballistica ray : aoe.rays) {
                                    ((MagicMissile) hero.sprite.parent.recycle(MagicMissile.class)).reset(
                                            MagicMissile.FROST,
                                            DungeonTilemap.tileCenterToWorld(dest),
                                            DungeonTilemap.tileCenterToWorld(ray.path.get(ray.dist)),
                                            null
                                    );
                                }
                                hero.spendAndNext(hero.attackDelay());
                                starlight_power = false;
                                afterAbilityUsed(hero);
                                ((MissileSprite) hero.sprite.parent.recycle(MissileSprite.class)).
                                        reset(dest,
                                                hero.pos,
                                                StarlightSmasher.this, new Callback() {
                                                    @Override
                                                    public void call() {

                                                    }
                                                });
                            }
                        });
    }

    @Override
    public Emitter emitter() {
        if (!starlight_power) return super.emitter();
        Emitter emitter = new Emitter();
        emitter.pos(0f, 0f);
        emitter.fillTarget = true;
        emitter.pour(MagicMissile.MagicParticle.FACTORY, 0.035f);
        return emitter;
    }

    private static final String STARLIGHT_BOOL         = "starlight";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put(STARLIGHT_BOOL, starlight_power);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        starlight_power = bundle.getBoolean(STARLIGHT_BOOL);
    }
}
