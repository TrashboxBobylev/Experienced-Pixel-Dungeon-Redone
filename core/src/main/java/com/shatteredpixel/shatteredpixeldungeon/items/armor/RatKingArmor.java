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

package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RatKingArmor extends ClassArmor{
    {
        image = ItemSpriteSheet.ARMOR_RAT_KING;
    }
    private static final float JUMP_DELAY=2f;
//
//    @Override
//    public void doSpecial() {
//        GameScene.selectCell( teleporter );
//    }
//
//    protected CellSelector.Listener teleporter = new  CellSelector.Listener() {
//
//        @Override
//        public void onSelect( Integer target ) {
//            if(target == null) return;
//
//            boolean[] stages = new boolean[3]; // jump/molten/blades
//
//            if( stages[0] = target != curUser.pos ) {
//                PathFinder.buildDistanceMap(curUser.pos, BArray.not(Dungeon.level.solid, null), 8);
//
//                if (PathFinder.distance[target] == Integer.MAX_VALUE ||
//                        !Dungeon.level.heroFOV[target]) {
//                    GLog.w(Messages.get(RogueArmor.class, "fov"));
//                    return;
//                }
//
//                if (Actor.findChar(target) != null) { // use heroic leap mechanics instead.
//                    Ballistica route = new Ballistica(curUser.pos, target, Ballistica.STOP_TARGET);
//                    //can't occupy the same cell as another char, so move back one until it is valid.
//                    int i = 0;
//                    while (Actor.findChar(target) != null && target != curUser.pos)
//                        target = route.path.get(route.dist - ++i);
//                }
//
//                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
//                    if (Dungeon.level.adjacent(mob.pos, curUser.pos) && mob.alignment != Char.Alignment.ALLY) {
//                        Buff.prolong(mob, Blindness.class, Blindness.DURATION / 2f);
//                        if (mob.state == mob.HUNTING) mob.state = mob.WANDERING;
//                        mob.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 4);
//                    }
//                }
//
//                CellEmitter.get(curUser.pos).burst(Speck.factory(Speck.WOOL), 10);
//                curUser.sprite.turnTo(curUser.pos, target); // jump from warrior leap
//                ScrollOfTeleportation.appear(curUser, target);
//                curUser.move(target); // impact from warrior leap.
//                Sample.INSTANCE.play(Assets.Sounds.PUFF);
//                Dungeon.level.occupyCell(curUser);
//                // at least do warrior vfx now.
//                Dungeon.observe();
//                GameScene.updateFog();
//                CellEmitter.center(curUser.pos).burst(Speck.factory(Speck.DUST), 10);
//                Camera.main.shake(2, 0.5f);
//            }
//            // now do mage
//            if(stages[1] = MageArmor.doMoltenEarth()) {
//                Dungeon.observe();
//                curUser.sprite.remove(CharSprite.State.INVISIBLE); // you still benefit from initial invisibiilty, even if you can't see it visually.
//                curUser.sprite.operate(curUser.pos,()->{}); // handled.
//                MageArmor.playMoltenEarthFX();
//            }
//            // warrior. this is delayed so the burst burning damage doesn't cancel this out instantly.
//            if(stages[0]) for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
//                Char mob = Actor.findChar(curUser.pos + PathFinder.NEIGHBOURS8[i]);
//                if (mob != null && mob != curUser && mob.alignment != Char.Alignment.ALLY) {
//                    Buff.prolong(mob, Paralysis.class, 5);
//                }
//            }
//            // huntress
//            HashMap<Callback, Mob> targets = new HashMap<>();
//            for (Mob mob : Dungeon.level.mobs) {
//                if (Dungeon.level.distance(curUser.pos, mob.pos) <= 12
//                        && Dungeon.level.heroFOV[mob.pos]
//                        && mob.alignment == Char.Alignment.ENEMY) {
//                    Callback callback = new Callback() {
//                        @Override
//                        public void call() {
//                            curUser.attack( targets.get( this ) );
//                            Invisibility.dispel();
//                            targets.remove( this );
//                            if (targets.isEmpty()) finish(stages);
//                        }
//                    };
//                    targets.put( callback, mob );
//                }
//            }
//            // this guarentees proper sequence of events for spectral blades
//            if ( stages[2] = targets.size() > 0 ) {
//                // turn towards the average point of all enemies being shot at.
//                Point sum = new Point(); for(Mob mob : targets.values()) sum.offset(Dungeon.level.cellToPoint(mob.pos));
//                sum.scale(1f/targets.size());
//                // wait for user sprite to finish doing what it's doing, then start shooting.
//                curUser.sprite.doAfterAnim( () -> curUser.sprite.zap(Dungeon.level.pointToCell(sum), ()->{
//                    Shuriken proto = new Shuriken();
//                    for(Map.Entry<Callback, Mob> entry : targets.entrySet())
//                        ( (MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class ) )
//                                .reset( curUser.sprite, entry.getValue().pos, proto, entry.getKey() );
//                }));
//                curUser.busy();
//            } else { // still need to finish, but also need to indicate that there was no enemies to shoot at.
//                if( stages[1] ) Invisibility.dispel();
//                else GLog.w( Messages.get(HuntressArmor.class, "no_enemies") );
//                finish(stages);
//            }
//        }
//        @Override
//        public String prompt() {
//            return Messages.get(RogueArmor.class, "prompt");
//        }
//    };
//
//    private void finish(boolean[] stages) {
//        curUser.sprite.doAfterAnim(curUser.sprite::idle); // because I overrode the default behavior I need to do this.
//
//        int delay = 0;
//        if(stages[1]) delay++;
//        if(stages[2]) delay += curUser.attackDelay();
//        if(stages[0]) Actor.addDelayed(new Actor() {
//            { actPriority = HERO_PRIO; } // this is basically the hero acting.
//            @Override
//            protected boolean act() {
//                Buff.prolong(curUser, Invisibility.class, Invisibility.DURATION/2f);
//                remove(this);
//                return true;
//            }
//        },(delay += JUMP_DELAY)-1);
//
//        curUser.spendAndNext(delay);
//        for(boolean stage : stages) if(stage) { useCharge(); break; }
//    }

}
