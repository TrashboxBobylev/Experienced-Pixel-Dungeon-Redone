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

public class Overhaul extends NewsArticle {
    {
        title = "Overhauled Weapons";
        icon = "ITEM: 128";
        summary = "Some weapons in new Dungeon have significantly changed properties:\n\n" +
                "_-_ _Greatsword_ summons knight statues around you to help in fighting monsters. They have same weapon as you, but significantly less HP, than common animated statues.\n\n" +
                "_-_ _Greataxe_ now has ranged ability: on throwing it, you can do devastating 3x3 attack.\n\n" +
                "_-_ _Sai_ is now able to progressively decrease armor on successful hits, making it as either support weapon or great Gladiator equipment.\n\n" +
                "_-_ _Runic Blade_ now can shoot magical blades, that have all properties of magical and melee attacks at once. But the blade after casting needs some recharge.\n\n" +
                "_-_ _Kunai_ on surprise attack gives buff to all melee weapons in your possession.\n\n" +
                "_-_ _Javelin_ on killing hits can create single consumable item like fishing.\n\n" +
                "_-_ _Displacing Dart_ can ignore walls and other enemies, but get damage penalty.\n\n" +
                "_-_ _Adrenaline Dart_ has instant attack speed, but also have damage penalty.\n\n" +
                "_-_ _Holy Dart_ dooms enemies on hits.\n\n" +
                "_-_ _Trident_ generates small amount of gold on each hit.\n\n" +
                "_-_ Any _Grim_ weapon now deal additional damage based on health of enemy, instead of butchering enemies." +
                "";
    }
}
