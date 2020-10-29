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

package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfIcyTouch;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class Cheese extends Food {
	
	{
		image = ItemSpriteSheet.CHEESE;
		energy = Hunger.STARVING*10f;
	}
	
	@Override
	protected void satisfy(Hero hero) {
		super.satisfy( hero );
		Buff.affect(hero, WellFed.class).reset();
		Buff.affect(hero, Levitation.class, Levitation.DURATION);
		Buff.affect(hero, Invisibility.class, Invisibility.DURATION);
		Buff.affect(hero, Haste.class, Haste.DURATION);
		Buff.affect(hero, EarthImbue.class, EarthImbue.DURATION);
		Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
		Buff.affect(hero, FrostImbue.class, FrostImbue.DURATION);
		Buff.affect(hero, MagicalSight.class, MagicalSight.DURATION);
		Buff.affect(hero, MindVision.class, MindVision.DURATION);
		Buff.affect(hero, Bless.class, Bless.DURATION);
		Buff.affect(hero, AdrenalineSurge.class).reset(2, 1000);
		Buff.affect(hero, Adrenaline.class, Adrenaline.DURATION);
		Buff.affect(hero, BlobImmunity.class, BlobImmunity.DURATION);
		Buff.affect(hero, Recharging.class, Recharging.DURATION);
		Buff.affect(hero, ToxicImbue.class).set(ToxicImbue.DURATION);
	}
	
	@Override
	public int price() {
		return 40 * quantity;
	}

	@Override
	public boolean doPickUp(Hero hero) {
		if (super.doPickUp(hero)){
			Badges.validateCheese();
			return true;
		}
		return false;
	}
}
