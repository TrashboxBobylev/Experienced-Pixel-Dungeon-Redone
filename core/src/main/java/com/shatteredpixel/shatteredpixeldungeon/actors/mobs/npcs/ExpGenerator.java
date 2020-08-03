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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.ArmorKit;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ExpGenSprite;

public class ExpGenerator extends NPC {
    {
        spriteClass = ExpGenSprite.class;
    }

    @Override
    protected Char chooseEnemy() {
        return null;
    }

    @Override
    public void rollToDropLoot() {
        Dungeon.level.drop(new com.shatteredpixel.shatteredpixeldungeon.items.ExpGenerator(), pos).sprite.drop();
    }

    @Override
    public void add( Buff buff ) {
        if (buff instanceof RatKing.Barter || buff instanceof Viscosity.DeferedDamage) super.add(buff);
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    protected void onAdd() {
        super.onAdd();
        spend(3f);
    }

    @Override
    protected boolean act() {
        spend(1f);
        Dungeon.hero.earnExp(Dungeon.escalatingDepth()/5, this.getClass());
        if (Dungeon.hero.fieldOfView[pos]) sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "exp", Dungeon.escalatingDepth()/5));
        return super.act();
    }
}
