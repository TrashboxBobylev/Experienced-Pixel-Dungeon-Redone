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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.KingBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class Longsword extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.LONGSWORD;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		internalTier = tier = 4;
	}

	@Override
	public long max(long lvl) {
		return  5*(tier+1) +    //25
				lvl*(tier+1);   //+5
	}

	@Override
	public long proc(Char attacker, Char defender, long damage) {
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
						long dmg = Math.round(damage*0.6f);
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
	public float abilityChargeUse(Hero hero, Char target) {
		return hero.belongings.secondWep() == this ?
				Buff.affect(hero, Charger.class).secondCharges :
				Buff.affect(hero, Charger.class).charges;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	public static class HolyExpEffect extends Buff {

		public int stacks = 0;

		@Override
		public int icon() {
			return BuffIndicator.BLESS;
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add(CharSprite.State.LONGSWORD);
			else target.sprite.remove(CharSprite.State.LONGSWORD);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc",
					Math.round((Math.pow(1.08, stacks) - 1f)*100),
					Math.round((Math.pow(1.15, stacks) - 1f)*100));
		}

		public static String STACKS = "stacks";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(STACKS, stacks);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			stacks = bundle.getInt(STACKS);
		}
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null) {
			return;
		}

		Ballistica aim = new Ballistica(hero.pos, target, Ballistica.WONT_STOP);
		Char enemy = Actor.findChar(target);

		float chargeUse = abilityChargeUse(hero, enemy);

		if (chargeUse <= 0) {
			GLog.w(Messages.get(this, "ability_no_charge"));
			return;
		}

		int maxDist = 2 + Math.round(chargeUse);
		int dist = Math.min(aim.dist, maxDist);

		ConeAOE cone = new ConeAOE(aim,
				dist,
				90,
				Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

		for (Ballistica ray : cone.outerRays){
			((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
					MagicMissile.HOLY_EXP_CONE,
					hero.sprite,
					ray.path.get(ray.dist),
					null
			);
		}

		hero.sprite.zap(target);
		MagicMissile.boltFromChar(curUser.sprite.parent,
				MagicMissile.HOLY_EXP_CONE,
				curUser.sprite,
				aim.path.get(dist / 2),
				new Callback() {
					@Override
					public void call() {
						beforeAbilityUsed(hero, enemy);
						for (int cell: cone.cells){
							Char ch = Actor.findChar( cell );
							if (ch != null) {
								LongswordWound.hit(ch.pos, 0, 0xf2e153);
								Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 2f, 0.65f);
								for (int i = 0; i < 1 + chargeUse/3; i++)
									Buff.affect(ch, HolyExpEffect.class).stacks++;
							}
						}

						Invisibility.dispel();
						hero.spendAndNext(hero.attackDelay());
						afterAbilityUsed(hero);
					}
				});
		Sample.INSTANCE.play( Assets.Sounds.ZAP );

	}

}
