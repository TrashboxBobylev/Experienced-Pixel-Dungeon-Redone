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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatKingSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collection;

public class RatKing extends NPC {

    public boolean ghastly = false;

	{
		spriteClass = RatKingSprite.class;
		
		state = SLEEPING;

		HP = HT = 2000;
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}
	
	@Override
	public float speed() {
		return 2f;
	}
	
	@Override
	protected Char chooseEnemy() {
		return null;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
	}
	
	@Override
	public void add( Buff buff ) {
	    if (buff instanceof Barter || buff instanceof MirrorImage.MirrorInvis) super.add(buff);
	}
	
	@Override
	public boolean reset() {
		return true;
	}

	//***This functionality is for when rat king may be summoned by a distortion trap

	@Override
	protected void onAdd() {
		super.onAdd();
		if (Dungeon.depth != 5 && !ghastly){
			yell(Messages.get(this, "confused"));
		}
	}

	@Override
	protected boolean act() {
        if (!ghastly) {
            if (Dungeon.depth < 5){
                if (pos == Dungeon.level.exit){
                    destroy();
                    sprite.killAndErase();
                } else {
                    target = Dungeon.level.exit;
                }
            } else if (Dungeon.depth > 5){
                if (pos == Dungeon.level.entrance){
                    destroy();
                    sprite.killAndErase();
                } else {
                    target = Dungeon.level.entrance;
                }
            }
        }
        Heap heap = Dungeon.level.heaps.get(pos );
        Barter barter = Buff.affect(this, Barter.class);
		if (heap != null){
		    Item item = heap.pickUp();
		    barter.stick(item);
            CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );
            Sample.INSTANCE.play( Assets.Sounds.PUFF );
        }
		if (barter.items.size() > 0){
		        barter.items.remove(barter.items.size() - 1);
            Item item;
            do {
		            item = Generator.random();
                } while (item instanceof Gold);
                item.cast(this, Dungeon.hero.pos);
                spend(2f);
        }
		return super.act();
	}

	//***

	@Override
	public boolean interact(Char c) {
		sprite.turnTo( pos, c.pos );

		if (c != Dungeon.hero){
			return super.interact(c);
		}
        if (!ghastly) {
            if (state == SLEEPING) {
                notice();
                yell(Messages.get(this, "not_sleeping"));
                state = WANDERING;
            } else {
                yell(Messages.get(this, "what_is_it"));
            }
        }
		return true;
	}
	
	@Override
	public String description() {
		return ((RatKingSprite)sprite).festive ?
				Messages.get(this, "desc_festive")
				: ghastly ? Messages.get(this, "ghastly") : super.description();
	}

    public static class Barter extends Buff {

        private ArrayList<Item> items = new ArrayList<>();

        public void stick(Item heh){
            for (Item item : items){
                if (item.isSimilar(heh)){
                    item.merge(heh);
                    return;
                }
            }
            items.add(heh);
        }

        @Override
        public void detach() {
            for (Item item : items)
                Dungeon.level.drop( item, target.pos).sprite.drop();
            super.detach();
        }

        private static final String ITEMS = "items";

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put( ITEMS , items );
            super.storeInBundle(bundle);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            items = new ArrayList<>((Collection<Item>) ((Collection<?>) bundle.getCollection(ITEMS)));
            super.restoreFromBundle( bundle );
        }


    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("h", ghastly);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        ghastly = bundle.getBoolean("h");
    }
}
