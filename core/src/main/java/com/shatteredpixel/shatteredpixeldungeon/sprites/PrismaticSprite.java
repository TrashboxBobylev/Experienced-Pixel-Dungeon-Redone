/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2024 Trashbox Bobylev
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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;

public class PrismaticSprite extends MirrorSprite {

	public PrismaticSprite(){
		super();

		float interval = (Game.timeTotal % 9 ) /3f;
		tint(interval > 2 ? interval - 2 : Math.max(0, 1 - interval),
				interval > 1 ? Math.max(0, 2-interval): interval,
				interval > 2 ? Math.max(0, 3-interval): interval-1, 0.5f);
	}

	@Override
	public void updateArmor() {
		updateArmor( ((PrismaticImage)ch).armTier );
	}
	
	public void updateArmor( int tier ) {
        if (Dungeon.hero != null) {
            CharSprite ref = Dungeon.hero.sprite;

            idle = ref.idle.clone();

            run = ref.run.clone();
            if(Dungeon.hero.heroClass == HeroClass.RAT_KING) run.delay = 0.1f; // this is the actual delay, and on another mob it doesn't make sense to not do this.

            attack = ref.attack.clone();

            // a hack to get the first frame, which should be the first idle frame.
            die = ref.idle.clone();
            die.frames(ref.idle.frames[0]);
            die.delay = ref.die.delay;
            die.looped = ref.die.looped;
        }  else {
			TextureFilm film = new TextureFilm( HeroSprite.tiers(Assets.Sprites.ROGUE, 15), tier, 12, 15 );

			idle = new Animation( 1, true );
			idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

			run = new Animation( 20, true );
			run.frames( film, 2, 3, 4, 5, 6, 7 );

			die = new Animation( 20, false );
			die.frames( film, 0 );

			attack = new Animation( 15, false );
			attack.frames( film, 13, 14, 15, 0 );
		}

        idle();
	}
	
	@Override
	public void update() {
		super.update();
		
		if (flashTime <= 0){
			float interval = (Game.timeTotal % 9 ) /3f;
			tint(interval > 2 ? interval - 2 : Math.max(0, 1 - interval),
					interval > 1 ? Math.max(0, 2-interval): interval,
					interval > 2 ? Math.max(0, 3-interval): interval-1, 0.5f);
		}
	}
	
}
