/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;

import java.util.ArrayList;

public class RageShield extends Buff {

	public static final float DURATION = 50f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}
	
	private int left;
    private float max;
	private static final String LEFT	= "left";
	private static final String MAX = "max";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEFT, left );
		bundle.put( MAX, max);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		left = bundle.getInt(LEFT);
		max = bundle.getFloat(MAX);
	}
	
	@Override
	public int icon() {
		return BuffIndicator.ARMOR;
	}

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1f, 0f, 0f);
    }

    @Override
	public float iconFadePercent() {
		return Math.max(0, (max - left) / max);
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String heroMessage() {
		return Messages.get(this, "heromsg");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", left);
	}
	
	public void set(float left){
		this.left = (int) left;
		this.max = (int) left;
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {
            boolean[] FOV = new boolean[Dungeon.level.length()];
            Point c = Dungeon.level.cellToPoint(target.pos);
            ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), FOV, Dungeon.level.losBlocking, 8);
            ArrayList<Char> affected = new ArrayList<>();

            for (int i = 0; i < FOV.length; i++) {
                if (FOV[i]) {
                    Char ch = Actor.findChar(i);
                    if (ch != null && (ch.alignment != target.alignment && ch.alignment != Char.Alignment.NEUTRAL)){
                        affected.add(ch);
                    }
                }
            }

            for (Char ch : affected){
                if (left > 0 && ch.alignment != target.alignment && target.HP < target.HT) {
                    target.sprite.parent.add(new Beam.HealthRay(target.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(ch.pos)));
                    float drain = ch.HT / 40f + 1;
                    if (Dungeon.level.adjacent(target.pos, ch.pos)){
                        drain = ch.HT / 8f + 1;
                    }
                    ch.damage(Math.round(drain), this);
                    left -= drain;
                    Buff.prolong(ch, Amok.class, 2f);
                    target.HP = Math.min(target.HP + Math.round(drain), target.HT);
                    target.sprite.showStatus( CharSprite.POSITIVE, Integer.toString(Math.round(drain)) );
                    if (left <= 0){
                        detach();
                        break;
                    }
                }
            }
			spend( TICK );

		} else {
			detach();
		}
		return true;
	}

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.RAGESHIELDED);
        else target.sprite.remove(CharSprite.State.RAGESHIELDED);
    }
}
