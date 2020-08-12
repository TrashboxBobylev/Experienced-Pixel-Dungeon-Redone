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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.ArmorKit;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ExpGenSprite;

public class ExpGenerator extends Mob {
    {
        spriteClass = ExpGenSprite.class;
        state = PASSIVE;
        alignment = Alignment.ALLY;
        state = WANDERING;
        HT = HP = 50 + Dungeon.escalatingDepth() * 5;
    }

    @Override
    protected boolean getCloser(int target) {
        return false;
    }

    @Override
    protected boolean getFurther(int target) {
        return false;
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        if (!(cause instanceof Hero || cause instanceof Buff)) {
            Buff.affect((Char) cause, Adrenaline.class, 50f);
            Buff.affect((Char) cause, Barkskin.class).set(enemy.HT, 1);
            Buff.affect((Char) cause, Bless.class, 60f);
            Buff.affect((Char) cause, Levitation.class, 60f);
            Buff.affect((Char) cause, ArcaneArmor.class).set(enemy.HT, 1);
        }
        Dungeon.level.drop(new com.shatteredpixel.shatteredpixeldungeon.items.ExpGenerator(), pos).sprite.drop();
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return false;
    }

    @Override
    protected void onAdd() {
        super.onAdd();
        spend(3f);
    }

    @Override
    protected boolean act() {
        spend(1f);
        boolean mobs = false;
        if (Dungeon.hero.fieldOfView[pos]) sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "exp", Dungeon.escalatingDepth()/5));
        for (Mob mob : Dungeon.level.mobs) {
            if (Dungeon.level.distance(pos, mob.pos) <= 16 && mob.state != mob.HUNTING) {
                mob.beckon( pos );
            }
            if (mob.alignment == Alignment.ENEMY){
                mobs = true;
            }
        }
        if (mobs) Dungeon.hero.earnExp(Dungeon.escalatingDepth()/5, this.getClass());
        return super.act();
    }
}
