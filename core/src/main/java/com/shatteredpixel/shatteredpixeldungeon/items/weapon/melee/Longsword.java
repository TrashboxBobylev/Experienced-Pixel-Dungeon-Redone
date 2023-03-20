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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.KingBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class Longsword extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.LONGSWORD;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		internalTier = tier = 4;
	}

	@Override
	public int max(int lvl) {
		return  5*(tier+1) +    //25
				lvl*(tier+1);   //+5
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int[] targets = new int[2];
		int direction = -1;
		int direction1 = -1, direction2 = -1;
		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++){
			if (Actor.findChar(attacker.pos + PathFinder.NEIGHBOURS8[i]) == defender){
				direction = i;
			}
		}
		if (direction != -1) {
			switch (direction) {
				case 0:
					direction1 = 4;
					direction2 = 6;
					break;
				case 1:
				case 6:
					direction1 = 3;
					direction2 = 4;
					break;
				case 2:
					direction1 = 3;
					direction2 = 6;
					break;
				case 3:
				case 4:
					direction1 = 1;
					direction2 = 6;
					break;
				case 5:
					direction1 = 1;
					direction2 = 4;
					break;
				case 7:
					direction1 = 1;
					direction2 = 3;
					break;
			}
			targets[0] = defender.pos + PathFinder.NEIGHBOURS8[direction1];
			targets[1] = defender.pos + PathFinder.NEIGHBOURS8[direction2];
			LongswordWound.hit(defender.pos, 315, 0xf2e153);
			for (int pos: targets){
				LongswordWound.hit(pos, 45, 0xf2e153);
				if (Actor.findChar(pos) != null){
					Char ch = Actor.findChar(pos);
					if (ch.alignment != attacker.alignment){
						int dmg = Math.round(damage*0.6f);
						Sample.INSTANCE.play(Assets.Sounds.HIT_STAB, 1f, 0.75f);
						if (enchantment != null && attacker.buff(MagicImmune.class) == null) {
							dmg = enchantment.proc( this, attacker, defender, damage );
						}
						if (attacker instanceof Hero){
							for (Item item: ((Hero) attacker).belongings.backpack){
								if (item instanceof KingBlade){
									dmg = new Unstable().proc(this,attacker,defender,damage);
									dmg = new Unstable().proc(this,attacker,defender,damage);
								}
							}
						}
						ch.damage(dmg, this);
					}
				}
			}
		}

		return super.proc(attacker, defender, damage);
	}

	public static class LongswordWound extends Wound {
		int color;

		@Override
		public void update() {
			super.update();

			hardlight(color);
		}

		public static void hit(int pos, float angle, int color ) {
			Group parent = Dungeon.hero.sprite.parent;
			LongswordWound w = (LongswordWound)parent.recycle( LongswordWound.class );
			parent.bringToFront( w );
			w.reset( pos );
			w.angle = angle;
			w.color = color;
		}
	}

	@Override
	public float abilityChargeUse( Hero hero ) {
		if (hero.buff(Sword.CleaveTracker.class) != null){
			return 0;
		} else {
			return super.abilityChargeUse( hero );
		}
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		Sword.cleaveAbility(hero, target, 1.23f, this);
	}

}
