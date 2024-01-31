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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;

public abstract class BlacksmithWeapon extends MeleeWeapon {
    {
        internalTier = tier = 3;
    }

    @Override
    public long min(long lvl) {
        return  (tier+Dungeon.cycle*5)*2 +  //base
                lvl*2;    //level scaling
    }

    @Override
    public long max(long lvl) {
        return  5*(tier+1+Dungeon.cycle*5) +    //base
                lvl*(tier+1+Dungeon.cycle*5);   //level scaling
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target){
        return 3;
    }

    @Override
    public long value() {
        return Math.round(super.value()*2.2f);
    }
}
