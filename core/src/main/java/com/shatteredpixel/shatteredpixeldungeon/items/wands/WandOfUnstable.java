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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

public class WandOfUnstable extends Wand {

    {
        image = ItemSpriteSheet.WAND_UNSTABLE;
    }

    public Class<?extends Wand>[] wands = new Class[]{
            WandOfBlastWave.class,
            WandOfFireblast.class,
            WandOfCorrosion.class,
            WandOfCorruption.class,
            WandOfFrost.class,
            WandOfLightning.class,
            WandOfLivingEarth.class,
            WandOfDisintegration.class,
            WandOfMagicMissile.class,
            WandOfPrismaticLight.class,
            WandOfRegrowth.class,
            WandOfTransfusion.class,
            WandOfWarding.class
    };

    private Wand wand;

    @Override
    public void onZap(Ballistica attack) {
        if (wand != null) {
            wand.onZap(attack);
            wand = null;
        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        wand = Reflection.newInstance(Random.element(wands));
        if (wand != null) {
            wand.level(level());
            wand.fx(bolt, callback);
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        wand = Reflection.newInstance(Random.element(wands));
        if (wand != null) {
            wand.level(level());
            wand.onHit(staff, attacker, defender, damage);
        }
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        if (wand != null){
            wand.staffFx(particle);
        }
    }

    @Override
    public String name() {
        String glitchy = "¢ÄãćS±«»ùăæǅƆǑʥʄɤȨζϻϡΰѾӫӸףעᵿᶗᵺᶆ";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++){
            builder.append(glitchy.charAt(Random.Int(glitchy.length())));
        }
        return super.name() + builder.toString();
    }


}
