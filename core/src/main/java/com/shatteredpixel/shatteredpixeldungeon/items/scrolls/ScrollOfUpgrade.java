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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class ScrollOfUpgrade extends InventoryScroll {

    public static final String AC_UPGRADE = "UPGRADE";
	public static final String AC_UPGRADE_AMOUNT = "UPGRADE_AMOUNT";

	{
		icon = ItemSpriteSheet.Icons.SCROLL_UPGRADE;
		preferredBag = Belongings.Backpack.class;

		unique = true;

		talentFactor = 2f;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return item.isUpgradable();
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_UPGRADE) || action.equals(AC_UPGRADE_AMOUNT)){
			if (hero.buff(MagicImmune.class) != null){
				GLog.w( Messages.get(this, "no_magic") );
			} else if (hero.buff( Blindness.class ) != null) {
				GLog.w( Messages.get(this, "blinded") );
			} else if (hero.buff(UnstableSpellbook.bookRecharge.class) != null
					&& hero.buff(UnstableSpellbook.bookRecharge.class).isCursed()){
				GLog.n( Messages.get(this, "cursed") );
			} else {
				if (action.equals(AC_UPGRADE)) {
					curUser = hero;
					curItem = detachAll(hero.belongings.backpack);
					GameScene.selectItem(itemSelector2);
				} else {
					curUser = hero;
					GameScene.show(new WndTextInput("Enter amount of upgrades to be used:", null, "", 15, false,
							"Accept", "Cancel") {
						@Override public void onSelect(boolean positive, String text) {
							if(!positive) return;
							long number = 0;
							try {
								number = Long.parseLong(text);
							} catch (NumberFormatException e){
								GLog.w("No valid number was entered.");
								return;
							}
							if (number != 0){
								curItem = null;
								curItem = split(number);
								updateQuickslot();
								if (curItem != null){
									curItem.onDetach( );
								}
								GameScene.selectItem(itemSelector2);
							}
						}
					});
				}
			}
		}
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions( hero );
		if (isIdentified() && !anonymous) {
			actions.add(AC_UPGRADE);
			if (quantity() > 1)
				actions.add(AC_UPGRADE_AMOUNT);
		}
		return actions;
	}

	@Override
	protected void onItemSelected( Item item ) {

		upgrade( curUser );

		upgradeItem(item);
		Talent.onUpgradeScrollUsed( Dungeon.hero );
	}

	private void upgradeItem(Item item) {
		Degrade.detach( curUser, Degrade.class );

		//logic for telling the user when item properties change from upgrades
		//...yes this is rather messy
		if (item instanceof Weapon){
			Weapon w = (Weapon) item;
			boolean wasCursed = w.cursed;
			boolean wasHardened = w.enchantHardened;
			boolean hadCursedEnchant = w.hasCurseEnchant();
			boolean hadGoodEnchant = w.hasGoodEnchant();

			w.upgrade();

			if (w.cursedKnown && hadCursedEnchant && !w.hasCurseEnchant()){
				removeCurse( Dungeon.hero );
			} else if (w.cursedKnown && wasCursed && !w.cursed){
				weakenCurse( Dungeon.hero );
			}
			if (wasHardened && !w.enchantHardened){
				GLog.w( Messages.get(Weapon.class, "hardening_gone") );
			} else if (hadGoodEnchant && !w.hasGoodEnchant()){
				GLog.w( Messages.get(Weapon.class, "incompatible") );
			}

		} else if (item instanceof Armor){
			Armor a = (Armor) item;
			boolean wasCursed = a.cursed;
			boolean wasHardened = a.glyphHardened;
			boolean hadCursedGlyph = a.hasCurseGlyph();
			boolean hadGoodGlyph = a.hasGoodGlyph();

			a.upgrade();

			if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph()){
				removeCurse( Dungeon.hero );
			} else if (a.cursedKnown && wasCursed && !a.cursed){
				weakenCurse( Dungeon.hero );
			}
			if (wasHardened && !a.glyphHardened){
				GLog.w( Messages.get(Armor.class, "hardening_gone") );
			} else if (hadGoodGlyph && !a.hasGoodGlyph()){
				GLog.w( Messages.get(Armor.class, "incompatible") );
			}

		} else if (item instanceof Wand || item instanceof Ring) {
			boolean wasCursed = item.cursed;

			item.upgrade();

			if (item.cursedKnown && wasCursed && !item.cursed){
				removeCurse( Dungeon.hero );
			}

		} else {
			item.upgrade();
		}

		Badges.validateItemLevelAquired(item);
		Statistics.upgradesUsed++;
		Badges.validateMageUnlock();
	}

	public static void upgrade( Hero hero ) {
		hero.sprite.emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );
	}

	public static void weakenCurse( Hero hero ){
		GLog.p( Messages.get(ScrollOfUpgrade.class, "weaken_curse") );
		hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 5 );
	}

	public static void removeCurse( Hero hero ){
		GLog.p( Messages.get(ScrollOfUpgrade.class, "remove_curse") );
		hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
	}
	
	@Override
	public long value() {
		return isKnown() ? 50 * quantity : super.value();
	}

	@Override
	public long energyVal() {
		return isKnown() ? 10 * quantity : super.energyVal();
	}

	protected WndBag.ItemSelector itemSelector2 = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return inventoryTitle();
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return preferredBag;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return usableOnItem(item);
		}

		@Override
		public void onSelect( Item item ) {

			//FIXME this safety check shouldn't be necessary
			//it would be better to eliminate the curItem static variable.
			if (!(curItem instanceof InventoryScroll)){
				return;
			}

			if (item != null) {
				// time for some copypaste
				Degrade.detach( curUser, Degrade.class );

				if (item instanceof Weapon){
					Weapon w = (Weapon) item;
					boolean wasCursed = w.cursed;
					boolean hadCursedEnchant = w.hasCurseEnchant();

					w.upgrade(curItem.quantity());

					if (w.cursedKnown && hadCursedEnchant && !w.hasCurseEnchant()){
						removeCurse( Dungeon.hero );
					} else if (w.cursedKnown && wasCursed && !w.cursed){
						weakenCurse( Dungeon.hero );
					}

				} else if (item instanceof Armor){
					Armor a = (Armor) item;
					boolean wasCursed = a.cursed;
					boolean hadCursedGlyph = a.hasCurseGlyph();

					a.upgrade(curItem.quantity());

					if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph()){
						removeCurse( Dungeon.hero );
					} else if (a.cursedKnown && wasCursed && !a.cursed){
						weakenCurse( Dungeon.hero );
					}

				} else if (item instanceof Wand || item instanceof Ring) {
					boolean wasCursed = item.cursed;

					item.upgrade(curItem.quantity());

					if (item.cursedKnown && wasCursed && !item.cursed){
						removeCurse( Dungeon.hero );
					}
				} else {
					item.upgrade(curItem.quantity());
				}
				((InventoryScroll)curItem).readAnimation();

				Sample.INSTANCE.play( Assets.Sounds.READ );
				Badges.validateItemLevelAquired(item);
				Statistics.upgradesUsed += curItem.quantity();
				Badges.validateMageUnlock();

			} else if (identifiedByUse && !((Scroll)curItem).anonymous) {

				((InventoryScroll)curItem).confirmCancelation();

			} else if (!((Scroll)curItem).anonymous) {

				curItem.collect( curUser.belongings.backpack );

			}
		}
	};
}
