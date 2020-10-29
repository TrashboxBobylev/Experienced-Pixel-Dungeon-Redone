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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class WandOfEarthblast extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_AVALANCHE;

		collisionProperties = Ballistica.STOP_TARGET;
	}

	public int min(int lvl){
		return (int) ((4+lvl*2) * ((chargesPerCast())));
	}

	public int max(int lvl){
		return (int) ((25+5*lvl) * ((chargesPerCast())));
	}

	ConeAOE cone;

	@Override
    public void onZap(Ballistica bolt) {

		ArrayList<Char> affectedChars = new ArrayList<>();
		for( int cell : cone.cells ){

			//ignore caster cell
			if (cell == bolt.sourcePos){
				continue;
			}

			Char ch = Actor.findChar( cell );
			if (ch != null) {
				affectedChars.add(ch);
			}
		}

		for (int cell : cone.cells){
			CellEmitter.bottom(cell).burst(Speck.factory(Speck.ROCK), 10);
			if (cell != Dungeon.level.entrance && cell != Dungeon.level.exit && !Dungeon.level.openSpace[cell]){
				Level.set( cell, Terrain.EMPTY);
				Dungeon.level.buildFlagMaps();
				Dungeon.level.cleanWalls();
				GameScene.updateMap();
			}
			Char ch = Actor.findChar( cell );
			if (ch != null) {
				affectedChars.add(ch);
			}
		}
		for (Char ch : affectedChars){
			int dmg = damageRoll();
			switch (Dungeon.level.distance(ch.pos, Dungeon.hero.pos)){
				case 3: dmg *= 0.66f; break;
				case 5: dmg *= 0.33f; break;
				case 7: dmg *= 0.16f; break;
				case 9: dmg *= 0.1f; break;
				case 12: dmg *= 0.04f; break;
			}
			processSoulMark(ch, chargesPerCast());
			ch.damage(Math.round(dmg), this);
			if (ch.isAlive()) {
				Buff.affect(ch, Paralysis.class, Paralysis.DURATION * chargesPerCast());
			}
		}
		Sample.INSTANCE.play(Assets.Sounds.ROCKS);
		Camera.main.shake( 3, 0.7f );
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		//acts like blazing enchantment
		new Blazing().proc( staff, attacker, defender, damage);
	}

	@Override
	protected void fx( Ballistica bolt, Callback callback ) {
		//need to perform flame spread logic here so we can determine what cells to put flames in.

		// unlimited distance
		int d = 3 + (chargesPerCast()-1);
		int dist = Math.min(bolt.dist, d);

		cone = new ConeAOE( bolt.sourcePos, bolt.path.get(dist),
				d,
				90,
				collisionProperties);

		//cast to cells at the tip, rather than all cells, better performance.
		for (Ballistica ray : cone.rays){
			((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
					MagicMissile.EARTH,
					curUser.sprite,
					ray.path.get(ray.dist),
					null
			);
		}

		//final zap at half distance, for timing of the actual wand effect
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.EARTH,
				curUser.sprite,
				bolt.path.get(dist/2),
				callback );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
		Sample.INSTANCE.play( Assets.Sounds.ROCKS );
	}

	@Override
	protected int chargesPerCast() {
		//consumes 30% of current charges, rounded up, with a minimum of one.
		return Math.max(1, (int)Math.ceil(curCharges*0.3f));
	}

	@Override
	public String statsDesc() {
		if (levelKnown)
			return Messages.get(this, "stats_desc", chargesPerCast(), Math.round(min()*Dungeon.fireDamage), Math.round(max()*Dungeon.fireDamage)) + "\n\n" + Messages.get(Wand.class, "charges", curCharges, maxCharges);
		else
			return Messages.get(this, "stats_desc", chargesPerCast(), min(0), max(0));
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0xEE7722 );
		particle.am = 0.5f;
		particle.setLifespan(0.6f);
		particle.acc.set(0, -40);
		particle.setSize( 0f, 3f);
		particle.shuffleXY( 1.5f );
	}

	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			boolean truth = false;
			boolean fire = false;
			boolean earth = false;
			boolean luck = false;

			if (Badges.isUnlocked(Badges.Badge.WAND_QUEST_6)) truth = true;

			for (Item ingredient : ingredients){
				if (ingredient.quantity() > 0) {
					if (ingredient instanceof WandOfFireblast) {
						fire = true;
					} else if (ingredient instanceof WandOfLivingEarth) {
						earth = true;
					} else if (ingredient instanceof Cheese) {
						luck = true;
					}
				}
			}

			return truth && fire && earth && luck;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 1;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;

			for (Item ingredient : ingredients){
				ingredient.quantity(ingredient.quantity() - 1);
			}

			return sampleOutput(null);
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return new WandOfEarthblast().identify();
		}
	}

}
