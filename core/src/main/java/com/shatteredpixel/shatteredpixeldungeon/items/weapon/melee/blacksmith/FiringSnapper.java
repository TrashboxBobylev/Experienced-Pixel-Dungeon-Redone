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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FiringSnapper extends BlacksmithWeapon {
    {
        image = ItemSpriteSheet.FIRINGWHIP;
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1.35f;

        DLY = 1f;
    }

    @Override
    public boolean canReach(Char owner, int target) {
        return new Ballistica(owner.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID).collisionPos == target;
    }

    @Override
    public long min(long lvl) {
        return super.min(lvl)/2;
    }

    @Override
    public long max(long lvl) {
        return super.max(lvl)/2;
    }

    @Override
    public long proc(Char attacker, Char defender, long damage) {
        Ballistica path = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
        for (int cell: path.subPath(1, path.dist)){
            CellEmitter.center(cell).burst(BlastParticle.FACTORY, 10);
            CellEmitter.center(cell).burst(FlameParticle.FACTORY, 20);
            if (Actor.findChar(cell) != null){
                attacker.sprite.parent.add(new TargetedCell(cell, 0xFF7F00));
                CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
                CellEmitter.center(cell).burst(FlameParticle.FACTORY, 40);
                this.hitSound(0.85f);
                ArrayList<Char> affected = new ArrayList<>();

                for (int n : PathFinder.NEIGHBOURS9) {
                    int c = cell + n;
                    if (c >= 0 && c < Dungeon.level.length()) {
                        if (Dungeon.level.heroFOV[c]) {
                            CellEmitter.get(c).burst(SmokeParticle.FACTORY, 6);
                        }

                        Char ch = Actor.findChar(c);
                        if (ch != null && ch.alignment != attacker.alignment) {
                            affected.add(ch);
                        }
                    }
                }

                for (Char ch : affected){

                    //if they have already been killed by another bomb
                    if(!ch.isAlive()){
                        continue;
                    }

                    long dmg = super.proc(attacker, ch, Math.round(damage*1.0f));

                    //those not at the center of the blast take less damage
                    if (ch.pos != cell){
                        dmg = Math.round(dmg*0.5f);
                    }

                    if (dmg > 0) {
                        ch.damage(dmg, this);
                    }
                }
            }
        }

        return -1;
    }

    protected void duelistAbility(Hero hero, Integer target ){
        beforeAbilityUsed(hero, null);
        Buff.affect(hero, DemonFireImmune.class, 15f);
        PathFinder.buildDistanceMap( hero.pos, BArray.not( Dungeon.level.solid, null ), 2 );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                this.hitSound(0.85f);
                GameScene.add(Blob.seed(i, 1, Fire.class));
                if (Dungeon.level.pit[i])
                    GameScene.add(Blob.seed(i, 1, FireKeeper.class));
                else
                    GameScene.add(Blob.seed(i, 4, FireKeeper.class));
                CellEmitter.get(i).burst(FlameParticle.FACTORY, 20);
                if (Random.Int(15) == 0 && Actor.findChar(i) == null){
                    RipperDemon rat = new RipperDemon();
                    rat.alignment = Char.Alignment.ALLY;
                    rat.state = rat.HUNTING;
                    Buff.affect(rat, DemonAlly.class);
                    Buff.affect(rat, DemonKiller.class, 10f);
                    Buff.affect(rat, DemonFireImmune.class, 10f);
                    Buff.affect(rat, AscensionChallenge.AscensionBuffBlocker.class);
                    GameScene.add( rat );
                    ScrollOfTeleportation.appear( rat, i );
                }
            }
        }
        Sample.INSTANCE.play(Assets.Sounds.BURNING);
        hero.sprite.attack(hero.pos);
        hero.next();
        afterAbilityUsed(hero);
    }

    public static class DemonFireImmune extends FlavourBuff {
        {
            immunities.add(Fire.class);
            immunities.add(Burning.class);
        }
    }

    public static class DemonKiller extends FlavourBuff {
        {
            type = buffType.NEUTRAL;
            announced = false;
        }

        @Override
        public boolean act() {
            target.die(null);
            if (Dungeon.hero.fieldOfView[target.pos])
                CellEmitter.get( target.pos ).burst( Speck.factory( Speck.STEAM ), 15 );
            return super.act();
        }
    }

    public static class DemonAlly extends AllyBuff {}

    public static class FireKeeper extends Blob {

        {
            actPriority = HERO_PRIO;
        }

        @Override
        protected void evolve() {

            int cell;

            Freezing freeze = (Freezing)Dungeon.level.blobs.get( Freezing.class );

            for (int i = area.left; i < area.right; i++){
                for (int j = area.top; j < area.bottom; j++){
                    cell = i + j*Dungeon.level.width();
                    off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

                    if (freeze != null && freeze.volume > 0 && freeze.cur[cell] > 0){
                        freeze.clear(cell);
                        off[cell] = cur[cell] = 0;
                        continue;
                    }

                    if (off[cell] > 0) {

                        volume += off[cell];

                        GameScene.add(Blob.seed(cell, 5, Fire.class));
                    }
                }
            }
        }

        @Override
        public void use( BlobEmitter emitter ) {
            super.use( emitter );
        }

        @Override
        public String tileDesc() {
            return "";
        }
    }
}
