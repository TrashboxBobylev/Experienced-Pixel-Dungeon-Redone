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

public class Blacky extends NewsArticle {
    {
        title = "Combining Items";
        icon = "sprites/blacksmith.png, 0, 0, 13, 16";
        summary = "Blacksmith got some game-changing changes.\n\n" +
                "_-_ You can now combine different items to get their combined levels in one item! There is two rules, that determine, which item should receive upgrades:\n" +
                "More powerful item gets upgrades, like plate armor will inherit level from mail armor.\n" +
                "If items are equal or simply don't have tiers, the highest upgraded item gets all upgrades.\n\n" +
                "_-_Another change is blacksmith being infinite. You can use his offering on as many items as you wish.";
    }
}
