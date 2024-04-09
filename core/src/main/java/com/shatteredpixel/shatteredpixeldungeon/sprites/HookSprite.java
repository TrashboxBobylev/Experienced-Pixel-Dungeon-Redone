/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2019-2024 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Hook;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.tweeners.AlphaTweener;

public class HookSprite extends CharSprite{

    private Animation[] tierIdles = new Animation[7];

    public HookSprite() {
        super();

        texture(Assets.Sprites.HOOKS);

        TextureFilm frames = new TextureFilm(texture, 7, 15);

        for (int i = 1; i <= 6; i++){
            tierIdles[i] = new Animation( 1, true );
            tierIdles[i].frames(frames, i - 1);
        }
    }

    @Override
    public void turnTo(int from, int to) {
        //do nothing
    }

    @Override
    public void die() {
        super.die();
        //cancels die animation and fades out immediately
        play(idle, true);
        parent.add( new AlphaTweener( this, 0, 1.25f ) {
            @Override
            protected void onComplete() {
                HookSprite.this.killAndErase();
                parent.erase( this );
            }
        } );
    }

    public void linkVisuals(Char ch ){

        if (ch == null) return;

        updateTier( ((Hook)ch).tier );

    }

    public void updateTier(int tier){

        idle = tierIdles[tier];
        run = idle.clone();
        attack = idle.clone();
        die = idle.clone();

        //always render first
        if (parent != null) {
            parent.sendToBack(this);
        }

        resetColor();
        if (ch != null) place(ch.pos);
        idle();

    }

    private float baseY = Float.NaN;

    @Override
    public void place(int cell) {
        super.place(cell);
        baseY = y;
    }

    @Override
    public void update() {
        super.update();
        if (!paused){
            if (Float.isNaN(baseY)) baseY = y;
            y = baseY + (float) Math.sin(Game.timeTotal) * 3;
            shadowOffset = 0.25f - 0.8f*(float) Math.sin(Game.timeTotal) * 3;
        }
    }
}
