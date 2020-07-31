/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BlackMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.NewDM300;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.watabou.noosa.Camera;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public class MimicSprite extends MobSprite {

	private Animation hiding;
	protected Animation slam;
	protected Animation throww;

	{
		//adjust shadow slightly to account for 1 empty bottom pixel (used for border while hiding)
		perspectiveRaise    = 5 / 16f; //5 pixels
		shadowWidth         = 1f;
		shadowOffset        = -0.4f;
	}

	protected int texOffset(){
		return 0;
	}

	public MimicSprite() {
		super();

		int c = texOffset();

		texture( Assets.Sprites.MIMIC );

		TextureFilm frames = new TextureFilm( texture, 16, 16 );

		hiding = new Animation( 1, true );
		hiding.frames( frames, 0+c, 0+c, 0+c, 0+c, 0+c, 1+c);

		idle = new Animation( 5, true );
		idle.frames( frames, 2+c, 2+c, 2+c, 3+c, 3+c );

		run = new Animation( 10, true );
		run.frames( frames, 2+c, 3+c, 4+c, 5+c, 5+c, 4+c, 3+c );

		attack = new Animation( 10, false );
		attack.frames( frames, 2+c, 6+c, 7+c, 8+c );

		throww = attack.clone();
		zap = attack.clone();
        slam = new Animation( 5, true );
        slam.frames( frames, 0+c, 0+c, 0+c, 0+c, 0+c, 1+c, 1+c, 1+c, 1+c);

		die = new Animation( 5, false );
		die.frames( frames, 9+c, 10+c, 11+c );

		play( idle );
	}
	
	@Override
	public void linkVisuals(Char ch) {
		super.linkVisuals(ch);
		if (ch.alignment == Char.Alignment.NEUTRAL) {
			hideMimic();
		}
	}

	public void hideMimic(){
		play(hiding);
		hideSleep();
	}

	@Override
	public void showSleep() {
		if (curAnim == hiding){
			return;
		}
		super.showSleep();
	}

	public static class Golden extends MimicSprite{
		@Override
		protected int texOffset() {
			return 16;
		}
	}

	public static class Crystal extends MimicSprite{
		@Override
		protected int texOffset() {
			return 32;
		}
	}

    public static class Black extends MimicSprite{

        private Emitter superchargeSparks;

        @Override
        protected int texOffset() {
            return 48;
        }

        public void throww( int cell ){
            turnTo( ch.pos , cell );
            play( throww);
            Sample.INSTANCE.play( Assets.Sounds.MIMIC );
            Camera.main.shake( 3, 0.7f );
        }

        public void zap( int cell ) {

            turnTo( ch.pos , cell );
            play( zap );

            MagicMissile.boltFromChar( parent,
                    MagicMissile.CORROSION,
                    this,
                    cell,
                    new Callback() {
                        @Override
                        public void call() {
                            ((BlackMimic)ch).onZapComplete();
                        }
                    } );
            Sample.INSTANCE.play( Assets.Sounds.PUFF );
        }

        public void slam( int cell ){
            turnTo( ch.pos , cell );
            play( slam );
            Sample.INSTANCE.play( Assets.Sounds.ROCKS );
            Camera.main.shake( 10, 0.7f );
        }

        private boolean exploded = false;

        @Override
        public void onComplete( Animation anim ) {

            if (anim == zap || anim == slam || anim == throww){
                idle();
            }

            if (anim == slam){
                ((BlackMimic)ch).onSlamComplete();
            }

            if (anim == throww){
                ((BlackMimic)ch).onThrowComplete();
            }

            super.onComplete( anim );

            if (anim == die && !exploded) {
                exploded = true;
                Sample.INSTANCE.play(Assets.Sounds.BLAST);
                emitter().burst( BlastParticle.FACTORY, 100 );
                killAndErase();
            }
        }

        @Override
        public void place(int cell) {
            if (parent != null) parent.bringToFront(this);
            super.place(cell);
        }

        @Override
        public void link(Char ch) {
            super.link(ch);

            superchargeSparks = emitter();
            superchargeSparks.autoKill = false;
            superchargeSparks.pour(SparkParticle.STATIC, 0.05f);
            superchargeSparks.on = false;

            if (ch instanceof NewDM300 && ((NewDM300) ch).isSupercharged()){
                superchargeSparks.on = true;
            }
        }

        @Override
        public void update() {
            super.update();

            if (superchargeSparks != null){
                superchargeSparks.visible = visible;
                if (ch instanceof NewDM300
                        && ((NewDM300) ch).isSupercharged() != superchargeSparks.on){
                    superchargeSparks.on = ((NewDM300) ch).isSupercharged();
                }
            }
        }

        @Override
        public void die() {
            super.die();
            if (superchargeSparks != null){
                superchargeSparks.on = false;
            }
        }

        @Override
        public void kill() {
            super.kill();
            if (superchargeSparks != null){
                superchargeSparks.killAndErase();
            }
        }
    }

}
