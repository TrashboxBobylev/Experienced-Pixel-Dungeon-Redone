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

public class PotionsOfExp extends NewsArticle {
    {
        title = "Improving XP Gain";
        icon = "ITEM: 338";
        summary = "When killing mobs and resetting floors is not enough, it's time to bring some proteins.\n" +
                "Now introducing special starlight juice to improve your accuracy, evasion and IQ!\n\n" +
                "Potion of Experience is pretty nice way to gather more experience and loot, because it activates 2x experience boost and additional item from mobs when Fate Lock is activated.\n\n" +
                "Potion of Overload is exotic version of PoE and activates mechanic, named ressurection in some roguelikes: when you kill mob, there is a high probability of spawning another mob with same rules as regular spawning.\n" +
                "These two potions work exceptionally well together.\n\n" +
                "Starflower gives both effects for much shorter time, have fun with it.\n\n\n" +
                "If you need gold instead of XP, you can use _Scroll of Midas_. It turns every enemy in FOV into gold, but doesn't give additional drops and any experience.";
    }
}
