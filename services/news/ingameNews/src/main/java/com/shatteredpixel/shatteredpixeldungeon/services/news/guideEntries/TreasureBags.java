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

public class TreasureBags extends NewsArticle {
    {
        title ="Boxes with Items";
        icon ="ITEM: 464";
        summary = "Once boss is defeated once, it starts dropping _treasure bags_. They can be opened for additional goods.\n\n" +
                "_-_ Once dungeon is reset at least once, you can get some exclusive items and more consumables.\n\n\n" +
                "There is also two other treasure bags to find:\n" +
                "_-_ _Gamble Bag_ provides 0-12 random items on opening, can be found in any shop." +
                "_-_ _Alchemy Bag_ can be acquired by fishing or in deeper shops.";
    }
}
