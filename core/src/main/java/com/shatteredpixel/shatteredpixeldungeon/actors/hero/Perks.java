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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Perks {
    public enum Perk {
        ;

        public String desc() {
            return Messages.get(Perks.class, name() + ".desc");
        }

        @Override
        public String toString() {
            return Messages.get(Perks.class, name() + ".name");
        }
    }

    public static void storeInBundle(Bundle bundle, ArrayList<Perk> perks) {
        ArrayList<String> conductIds = new ArrayList<>();
        for (Perk conduct: perks){
            conductIds.add(conduct.name());
        }
        bundle.put("perks", conductIds.toArray(new String[0]));
    }

    public static void restoreFromBundle(Bundle bundle, ArrayList<Perk> perks) {
        perks.clear();
        if (bundle.getStringArray("conduct") != null) {
            String[] conductIds = bundle.getStringArray("conduct");
            for (String conduct : conductIds) {
                perks.add(Perk.valueOf(conduct));
            }
        }
    }
}
