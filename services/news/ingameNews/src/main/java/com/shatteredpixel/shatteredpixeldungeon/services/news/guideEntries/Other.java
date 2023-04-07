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

package com.shatteredpixel.shatteredpixeldungeon.services.news.guideEntries;

import com.shatteredpixel.shatteredpixeldungeon.services.news.NewsArticle;

public class Other extends NewsArticle {
    {
        title = "Misc Remarks";
        icon = "ICON: PREFS";
        summary = "There is an other things that you should know about:\n\n" +
                "_-_ You can upgrade artifacts once they reach +10.\n\n" +
                "_-_ Wands don't have max charge limitation anymore; and Mage's Staff holds 33% more charges compared to wand without staff." +
                "_-_ Shops now sell more items with more variety over them.\n\n" +
                "_-_ The inventory space is massively increased, both for backpack and dedicated bags.\n\n" +
                "_-_ Enchantments and glyphs no longer have a chance of disappearing with upgrading them.\n\n" +
                "_-_ Rings have massive nerf compared to vanilla Shattered PD.\n\n" +
                "_-_ _Potion of Mastery_ buffs item's effectiveness by 20%.\n\n" +
                "_-_ Class armor charges 150% faster.\n\n"+
                "_-_ Bees will grow more aggressive with cycles, increasing their attack speed.\n\n"+
                "_-_ _Scroll of Challenge_ is _Scroll of Determination_ now; instead of confusing entities, it applies life-steal shielding to hero.\n\n" +
                "_-_ _Scroll of Divination_, once it has done its intended function, will attempt to identify every item in inventory with 33% chance.";

    }
}
