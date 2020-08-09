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

public class ExpGen extends NewsArticle {
    {
        title = "Automatic XP";
        icon = "ITEM: 426";
        summary = "If even killing mobs in mass can't satisfy you, there is passive EXP farm method with Power Plants.\n\n" +
                "They can be bought in shops for pretty high price, and you can get as many as you want, but there is limitations to this technology:\n\n" +
                "_-_ While power plants don't need mobs to work, they need your saturation.\n\n" +
                "_-_ You can't sleep with them in sight, because they emit light from consumed sunlight.\n\n" +
                "But the great news: they scale with you progression in the game!";
    }
}
