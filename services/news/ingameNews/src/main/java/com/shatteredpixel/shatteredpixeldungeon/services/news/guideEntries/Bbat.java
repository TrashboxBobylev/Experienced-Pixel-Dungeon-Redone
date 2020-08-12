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

public class Bbat extends NewsArticle {
    {
        title = "Rogue Assistant";
        icon = "sprites/bbat.png, 0, 0, 15, 15";
        summary = "In darkness of dungeon, it isn't easy to obtain friend. But if you are rogue...\n\n" +
                "Bbat is special ally that follows you and attacks mobs.\n\n" +
                "_-_ It grows with your level ups..\n\n" +
                "_-_When damaged, slighty charges your cloak.\n\n" +
                "_-_On death, gives cooldown in which bbat cannot be resummoned. Once cooldown is expired, you can summon bbat with your cloak.\n\n" +
                "If you want to play with bbat more, then you can choose _Hunter_ subclass.\n\n" +
                "_-_ Hunter's bbat loses some damage, but gains increased attack speed and ability to mark targets.\n" +
                "Marks stack to each other and allow you to teleport behind target and ambush it with tonof debuffs.\n\n" +
                "_-_ While marks can be used to inflict debuffs and move across the floor, you can also let bbat defeat enemy because bbat deals more damage to marked enemy.";
    }
}
