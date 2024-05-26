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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;

import java.util.ArrayList;

public class PsycheChest extends Item {
    {
        image = ItemSpriteSheet.PSYCHE_CHEST;
        unique = true;
        identify();
    }

    private static final String AC_ACTIVATE = "ACTIVATE";
    private static final String AC_DEACTIVATE = "DEACTIVATE";
    private static final String AC_RESET = "RESET";

    private static final ItemSprite.Glowing BLOODY = new ItemSprite.Glowing( 0x550000 );

    public static int questDepth;

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (!hero.grinding) actions.add( AC_ACTIVATE );
        else actions.add( AC_DEACTIVATE);
        actions.add(AC_RESET);
        actions.remove(AC_DROP);
        actions.remove(AC_THROW);
        return actions;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", neededExp());
    }

    public static long neededExp(){
        long neededExp = 100;
        switch (Dungeon.cycle){
            case 1: neededExp = 200; break;
            case 2: neededExp = 1250; break;
            case 3: neededExp = 11750; break;
            case 4: neededExp = 75000; break;
            case 5: neededExp = 500000; break;
        }
        if (Dungeon.isChallenged(Challenges.NO_SCROLLS)){
            neededExp *= 2.5f;
        }
        return neededExp;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals(AC_ACTIVATE)) {
            hero.grinding = true;
            GLog.w( Messages.get(this, "activated") );
        }
        if (action.contains(AC_DEACTIVATE)){
            hero.grinding = false;
            GLog.w( Messages.get(this, "deactivated") );
        }
        if (action.contains(AC_RESET) && (hero.HP > hero.HT * 0.55d)){
            switch (Dungeon.depth){
                case 2: case 3: case 4:
                    for (Mob m: Dungeon.level.mobs){
                        if (m instanceof Ghost) {
                            questDepth = Dungeon.depth;
                            Ghost.Quest.reset();
                        }
                    }
                     break;
                case 7: case 8: case 9:
                    for (Mob m: Dungeon.level.mobs){
                        if (m instanceof Wandmaker) {
                            questDepth = Dungeon.depth;
                            Wandmaker.Quest.reset();
                        }
                    }
                    break;
                case 12: case 13: case 14:
                    for (Mob m: Dungeon.level.mobs){
                        if (m instanceof Blacksmith) {
                            questDepth = Dungeon.depth;
                            Blacksmith.Quest.reset();
                        }
                    }
                    break;
                case 17: case 18: case 19:
                    for (Mob m: Dungeon.level.mobs){
                        if (m instanceof Imp) {
                            questDepth = Dungeon.depth;
                            Imp.Quest.reset();
                        }
                    }
                    break;
            }
            for (Heap heap: Dungeon.level.heaps.valueList()) {
                if (heap.type.forSale()) {
                    if (heap.peek().wereOofed && !Dungeon.oofedItems.contains(heap.peek())){
                        Dungeon.oofedItems.add(heap.items.removeFirst());
                    }
                }
            }
            InterlevelScene.mode = InterlevelScene.Mode.RESET;
            if (hero.HP > hero.HT * 0.55d) hero.HP -= Math.round(hero.HT * 0.55d);
            Dungeon.resetDamage *= 1.12d;
            Game.switchScene(InterlevelScene.class);
        } else if (action.contains(AC_RESET) && (hero.HP < Math.round(hero.HT * 0.55d))){
            GLog.w( Messages.get(this, "no_reset") );
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return Dungeon.hero.grinding ? BLOODY : null;
    }

    @Override
    public boolean canBeOofed() {
        return false;
    }
}
