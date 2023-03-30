/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2020 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2020 Trashbox Bobylev
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

package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class SuperPickaxe extends Item {
    {
        image = ItemSpriteSheet.SUPER_PICKAXE;
        defaultAction = AC_THROW;
        usesTargeting = true;
        stackable = false;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return quantity * 100;
    }

    @Override
    public int throwPos(Hero user, int dst) {
        return dst;
    }

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
                ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                        reset(user.sprite,
                                enemy.sprite,
                                item,
                                new Callback() {
                                    @Override
                                    public void call() {
                                        boolean success = false;
                                        curUser = user;
                                        if (user.attack(enemy, 2.5f, 0, 0.25f)) {
                                            Sample.INSTANCE.play(Assets.Sounds.HIT);
                                            success = true;
                                        }
                                        Invisibility.dispel();
                                        if (user.buff(Talent.LethalMomentumTracker.class) != null){
                                            user.buff(Talent.LethalMomentumTracker.class).detach();
                                            user.next();
                                        } else {
                                            user.spendAndNext(delay);
                                        }
                                        if (!success){
                                            ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                                                    reset(cell, user.pos,
                                                            item, new Callback() {
                                                                @Override
                                                                public void call() {

                                                                }
                                                            });
                                        }
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
                                        boolean success = false;
                                        if (cell != Dungeon.level.entrance && cell != Dungeon.level.exit
                                                && !Dungeon.level.openSpace[cell]){
                                            int tile = Dungeon.level.map[cell];
                                            Level.set(cell, Terrain.EMPTY);
                                            if (new Ballistica( user.pos, cell, Ballistica.PROJECTILE ).collisionPos ==
                                                cell) {
                                                success = true;
                                                Dungeon.level.buildFlagMaps();
                                                Dungeon.level.cleanWalls();
                                                GameScene.updateMap();
                                                CellEmitter.bottom(cell).burst(Speck.factory(Speck.ROCK), 4);
                                                Sample.INSTANCE.play(Assets.Sounds.ROCKS);
                                                Camera.main.shake(3, 0.7f);
                                            } else {
                                                Level.set(cell, tile);
                                            }
                                        }
                                        user.spendAndNext(delay);
                                        if (!success){
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
                                    }
                                });
            }
        }
    }
}
