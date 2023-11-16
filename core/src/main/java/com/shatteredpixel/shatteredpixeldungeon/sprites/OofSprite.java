/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class OofSprite extends MobSprite {
	public OofSprite() {
		super();
		
		texture( Assets.Sprites.THIEF );
		TextureFilm film = new TextureFilm( texture, 12, 13 );
		
		idle = new Animation( 2, true );
		idle.frames( film, 21 + 21, 21 + 21, 21 + 21, 21 + 22, 21 + 21, 21 + 21, 21 + 21, 21 + 21, 21 + 22 );
		
		run = new Animation( 20, true );
		run.frames( film, 21 + 21, 21 + 21, 21 + 23, 21 + 24, 21 + 24, 21 + 25 );
		
		die = new Animation( 8, false );
		die.frames( film, 21 + 25, 21 + 27, 21 + 28, 21 + 29, 21 + 30 );
		
		attack = new Animation( 14, false );
		attack.frames( film, 21 + 31, 21 + 32, 21 + 33 );
		
		idle();
	}
}
