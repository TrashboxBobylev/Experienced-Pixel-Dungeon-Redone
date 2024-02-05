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

package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class CheeseChunk extends Food {
	
	{
		image = ItemSpriteSheet.CHEESE_CHUNK;
		energy = Hunger.STARVING*2f;
	}
	
	@Override
	protected void satisfy(Hero hero) {
		super.satisfy( hero );
		Buff.affect(hero, WellFed.class).reset();
		Buff.affect(hero, Haste.class, Haste.DURATION * 0.25f);
		Buff.affect(hero, Bless.class, Bless.DURATION * 0.25f);
		Buff.affect(hero, AdrenalineSurge.class).reset(2, 100);
		Buff.affect(hero, Adrenaline.class, Adrenaline.DURATION * 0.25f);
		for (Heap h : Dungeon.level.heaps.valueList()){
			if (h.type == Heap.Type.HEAP) {
				Item item = h.peek();
				if (item.doPickUp(hero, h.pos)) {
					h.pickUp();
					hero.spend(-Item.TIME_TO_PICK_UP); //casting the spell already takes a turn
					GLog.i( Messages.capitalize(Messages.get(hero, "you_now_have", item.name())) );
				} else {
					GLog.w(Messages.get(this, "cant_grab"));
					h.sprite.drop();
				}
			}
		}
	}
	
	@Override
	public long value() {
		return 20 * quantity;
	}

	public static class oneMeat extends Recipe.SimpleRecipe{
		{
			inputs =  new Class[]{Cheese.class, Torch.class};
			inQuantity = new int[]{1, 1};

			cost = 12;

			output = CheeseChunk.class;
			outQuantity = 3;
		}
	}
}
