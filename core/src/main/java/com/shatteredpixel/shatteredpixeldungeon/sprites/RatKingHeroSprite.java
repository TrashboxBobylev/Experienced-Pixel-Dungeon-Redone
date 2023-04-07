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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.noosa.TextureFilm;

public class RatKingHeroSprite extends HeroSprite {
    // placeholder
    private static final int FRAME_WIDTH = 16;
    private static final int FRAME_HEIGHT = 17;
    {
        //runFramerate = 10;
    }

    public void updateArmor() {
        // there's only two armors. really one atm.
        // TODO decide if I want a "crownless" rat king towards the beginning of the game.
        TextureFilm film = new TextureFilm( tiers(), 0, FRAME_WIDTH, FRAME_HEIGHT );
        idle = new Animation( 2, true );
        idle.frames( film, 0, 0, 0, 1 );

        run = new Animation( 20, true );
        run.frames( film, 6, 7, 8, 9, 10 );

        attack = new Animation( 15, false );
        attack.frames( film, 2, 3, 4, 5, 0 );

        die = new Animation( 10, false );
        die.frames( film, 11,12,13,14 );

        zap = attack.clone();

        operate = new Animation( 8, false );
        operate.frames( film, 2,6,2,6);

        fly = new Animation( 1, true );
        fly.frames( film, 0 );

        read = operate;

        if (Dungeon.hero.isAlive())
            idle();
        else
            die();
    }

    public static TextureFilm ratKiers;

    @Override
    public TextureFilm tiers() {
        if(ratKiers == null) ratKiers = tiers(Assets.Sprites.RAT_KING_HERO, FRAME_HEIGHT);
        return ratKiers;
    }
}
