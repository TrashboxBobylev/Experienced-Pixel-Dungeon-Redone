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

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class RingOfEnergy extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_ENERGY;
	}

	public String statsInfo() {
		if (isIdentified()){
			String info = Messages.get(this, "stats",
					new DecimalFormat("#.###").format(100f * (1.10f + soloVisualBonus()*0.0125f - 1f)));
			if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
				info += "\n\n" + Messages.get(this, "combined_stats",
						Messages.decimalFormat("#.##", 100f * (1.10f + combinedBuffedBonus(Dungeon.hero)*0.0125f - 1f)));
			}
			return info;
		} else {
			return Messages.get(this, "typical_stats",
					new DecimalFormat("#.###").format(10f));
		}
	}

	public String upgradeStat1(long level){
		if (cursed && cursedKnown) level = Math.min(-1, level-3);
		return Messages.decimalFormat("#.##", 100f * (1.10f + level*0.0125f - 1f)) + "%";
	}

	@Override
	protected RingBuff buff( ) {
		return new Energy();
	}
	
	public static float wandChargeMultiplier( Char target ){
        float multiplier = 1f;
        if (getBuffedBonus(target, Energy.class) > 0) multiplier = 1.1f;
        if (getBuffedBonus(target, Energy.class) > 1) multiplier += getBuffedBonus(target, Energy.class)*0.0125;
        return multiplier;
	}

	public static float artifactChargeMultiplier( Char target ){
        float multiplier = 1f;
        if (getBuffedBonus(target, Energy.class) > 0) multiplier = 1.1f;
        if (getBuffedBonus(target, Energy.class) > 1) multiplier += getBuffedBonus(target, Energy.class)*0.0125;
        return multiplier;
	}

	public static float armorChargeMultiplier( Char target ){
		float multiplier = 1f;
		if (getBuffedBonus(target, Energy.class) > 0) multiplier = 1.1f;
		if (getBuffedBonus(target, Energy.class) > 1) multiplier += getBuffedBonus(target, Energy.class)*0.0125;
		return multiplier;
	}

	public class Energy extends RingBuff {
	}
}
