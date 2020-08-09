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

public class Fishing extends NewsArticle {
    {
        title = "Catching More Items";
        icon = "ITEM: 80";
        summary = "When you get used at bartering, the pool of items that you can exchange or sell is quite limited. How to get more items without hassle in form of fighting and exploration?\n" +
                "Get yourself a fishing rod! This simple device is capable of providing you with infinite amount of items in exchange of your time.\n\n" +
                "_-_ To start, use CAST action on fishing rod and point into water. There should be a fishing hook on this water.\n\n" +
                "_-_ Now simple wait (in-game turns, not real time), until the hook will glow. It's time to use UNCAST action.\n\n" +
                "_-_ The hook will grab some item on it's way and drop it right under your foots.\n\n" +
                "There is a fishing rods for each stage of game (not chapters, but cycles, of which we will get talk later).";
    }
}
