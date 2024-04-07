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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.PsycheChest;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.fishingrods.FishingRod;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.KeyToTruth;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.blacksmith.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBlacksmith;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collection;

public class Blacksmith extends NPC {
	
	{
		spriteClass = BlacksmithSprite.class;

		properties.add(Property.IMMOVABLE);
	}
	
	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			die(null);
			Notes.remove( Notes.Landmark.TROLL );
			return true;
		}
		if (Dungeon.level.visited[pos] && !Quest.started()){
			Notes.add( Notes.Landmark.TROLL );
		}
		return super.act();
	}
	
	@Override
	public boolean interact(Char c) {
		
		sprite.turnTo( pos, c.pos );

		if (c != Dungeon.hero){
			return true;
		}
		if (Dungeon.hero.belongings.getItem(KeyToTruth.class) != null){
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndQuest(Blacksmith.this, Messages.get(Blacksmith.class, "key")){
						@Override
						public void hide() {
							super.hide();
							Dungeon.hero.belongings.getItem(KeyToTruth.class).detach(Dungeon.hero.belongings.backpack);
							Blacksmith.this.destroy();

							Blacksmith.this.sprite.die();

							Quest.completed = true;

							Badges.truth();
						}
					});
				}
			});
			return true;
		}

		if (!Quest.given) {

			String msg1 = "";
			String msg2 = "";

			if (Quest.type == Quest.OLD){
				//pre-v2.2.0 saves
				msg1 = Quest.alternative ? Messages.get(Blacksmith.this, "blood_1") : Messages.get(Blacksmith.this, "gold_1");
			} else {

				switch (Dungeon.hero.heroClass){
					case WARRIOR:   msg1 += Messages.get(Blacksmith.this, "intro_quest_warrior"); break;
					case MAGE:      msg1 += Messages.get(Blacksmith.this, "intro_quest_mage"); break;
					case ROGUE:     msg1 += Messages.get(Blacksmith.this, "intro_quest_rogue"); break;
					case HUNTRESS:  msg1 += Messages.get(Blacksmith.this, "intro_quest_huntress"); break;
					case DUELIST:   msg1 += Messages.get(Blacksmith.this, "intro_quest_duelist"); break;
					case RAT_KING:  msg1 += Messages.get(Blacksmith.this, "intro_quest_ratking"); break;
					//case CLERIC: msg1 += Messages.get(Blacksmith.this, "intro_quest_cleric"); break;
				}

				msg1 += "\n\n" + Messages.get(Blacksmith.this, "intro_quest_start");

				switch (Quest.type){
					case Quest.CRYSTAL: msg2 += Messages.get(Blacksmith.this, "intro_quest_crystal"); break;
					case Quest.GNOLL:   msg2 += Messages.get(Blacksmith.this, "intro_quest_gnoll"); break;
					case Quest.FUNGI:   msg2 += Messages.get(Blacksmith.this, "intro_quest_fungi"); break;
				}

			}

			final String msg1Final = msg1;
			final String msg2Final = msg2;
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndQuest(Blacksmith.this, msg1Final) {
						@Override
						public void hide() {
							super.hide();

							Quest.given = true;
							Quest.completed = false;
							Notes.add( Notes.Landmark.TROLL );

							Item pick = Quest.pickaxe != null ? Quest.pickaxe : new Pickaxe();
							if (pick.doPickUp( Dungeon.hero )) {
								GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", pick.name()) ));
							} else {
								Dungeon.level.drop( pick, Dungeon.hero.pos ).sprite.drop();
							}
							Quest.pickaxe = null;

							if (msg2Final != ""){
								GameScene.show(new WndQuest(Blacksmith.this, msg2Final));
							}

						}
					} );
				}
			});

		} else if (!Quest.completed) {

			if (Quest.type == Quest.OLD) {
				if (Quest.alternative) {

					Pickaxe pick = Dungeon.hero.belongings.getItem(Pickaxe.class);
					if (pick == null) {
						tell(Messages.get(this, "lost_pick"));
					} else if (!pick.bloodStained) {
						tell(Messages.get(this, "blood_2"));
					} else {
						if (pick.isEquipped(Dungeon.hero)) {
							boolean wasCursed = pick.cursed;
							pick.cursed = false; //so that it can always be removed
							pick.doUnequip(Dungeon.hero, false);
							pick.cursed = wasCursed;
						}
						pick.detach(Dungeon.hero.belongings.backpack);
						Quest.pickaxe = pick;
						tell(Messages.get(this, "completed"));

						Quest.completed = true;
						Statistics.questScores[2] = 3000;
					}

				} else {

					Pickaxe pick = Dungeon.hero.belongings.getItem(Pickaxe.class);
					DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
					if (pick == null) {
						tell(Messages.get(this, "lost_pick"));
					} else if (gold == null || gold.quantity() < 15) {
						tell(Messages.get(this, "gold_2"));
					} else {
						if (pick.isEquipped(Dungeon.hero)) {
							boolean wasCursed = pick.cursed;
							pick.cursed = false; //so that it can always be removed
							pick.doUnequip(Dungeon.hero, false);
							pick.cursed = wasCursed;
						}
						pick.detach(Dungeon.hero.belongings.backpack);
						Quest.pickaxe = pick;
						gold.detachAll(Dungeon.hero.belongings.backpack);
						tell(Messages.get(this, "completed"));

						Quest.completed = true;
						Statistics.questScores[2] = 3000;
					}

				}
			} else {

				String msg = Messages.get(this, "reminder") + "\n\n";
				switch (Quest.type){
					case Quest.CRYSTAL: msg += Messages.get(Blacksmith.this, "reminder_crystal"); break;
					case Quest.GNOLL:   msg += Messages.get(Blacksmith.this, "reminder_gnoll"); break;
					case Quest.FUNGI:   msg += Messages.get(Blacksmith.this, "reminder_fungi"); break;
				}
				tell(msg);

			}
		} else if (Quest.type == Quest.OLD && Quest.reforges == 0) {

			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show( new WndBlacksmith.WndReforge( Blacksmith.this, null ) );
				}
			});

		} else if (Quest.rewardsAvailable()) {

			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					//in case game was closed during smith reward selection
					if (Quest.smithRewards != null && Quest.smiths > 0){
						GameScene.show( new WndBlacksmith.WndSmith( Blacksmith.this, Dungeon.hero ) );
					} else {
						GameScene.show(new WndBlacksmith(Blacksmith.this, Dungeon.hero));
					}
				}
			});

		} else {
			
			tell( Messages.get(this, "get_lost",
					getGold()));
			
		}

		return true;
	}
	
	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( Blacksmith.this, text ) );
			}
		});
	}
	
	public static String verify( Item item1, Item item2 ) {
		
		if (item1 == item2 && (item1.quantity() == 1 && item2.quantity() == 1)) {
			return Messages.get(Blacksmith.class, "same_item");
		}

//		if ((item1 instanceof Weapon && !(item2 instanceof Weapon)) ||
//                (item1 instanceof Armor && !(item2 instanceof Armor)) ||
//                (item1 instanceof Wand && !(item2 instanceof Wand)) ||
//                (item1 instanceof Ring && !(item2 instanceof Ring))) {
//			return Messages.get(Blacksmith.class, "diff_type");
//		}

		if (!item1.isIdentified() || !item2.isIdentified()) {
			return Messages.get(Blacksmith.class, "un_ided");
		}

		if (item1.cursed || item2.cursed ||
				(item1 instanceof Armor && ((Armor) item1).hasCurseGlyph()) ||
				(item2 instanceof Armor && ((Armor) item2).hasCurseGlyph()) ||
				(item1 instanceof Weapon && ((Weapon) item1).hasCurseEnchant()) ||
				(item2 instanceof Weapon && ((Weapon) item2).hasCurseEnchant())) {
			return Messages.get(Blacksmith.class, "cursed");
		}

		if (item1.level() < 0 || item2.level() < 0) {
			return Messages.get(Blacksmith.class, "degraded");
		}

		if (!item1.isUpgradable() || !item2.isUpgradable()) {
			return Messages.get(Blacksmith.class, "cant_reforge");
		}

		return null;
	}

	public static void upgrade( Item item1, Item item2 ) {

		Item first, second;
        first = item1;
        second = item2;
		if (item1 instanceof MeleeWeapon && item2 instanceof MeleeWeapon){
		    if ((((MeleeWeapon) item2).tier > ((MeleeWeapon) item1).tier)){
                first = item2;
                second = item1;
            }
        } else if (item1 instanceof MissileWeapon && item2 instanceof MissileWeapon){
            if ((((MissileWeapon) item2).tier > ((MissileWeapon) item1).tier)){
                first = item2;
                second = item1;
            }
        } else if (item1 instanceof Armor && item2 instanceof Armor){
            if ((((Armor) item2).tier > ((Armor) item1).tier)){
                first = item2;
                second = item1;
            }
        }
		if (second.level() > first.level() && !(first instanceof FishingRod)) {
		    Item temp = first;
			first = second;
			second = temp;
		}

		Sample.INSTANCE.play( Assets.Sounds.EVOKE );
		ScrollOfUpgrade.upgrade( Dungeon.hero );
		Item.evoke( Dungeon.hero );

		if (second.isEquipped( Dungeon.hero )) {
			((EquipableItem)second).doUnequip( Dungeon.hero, false );
		}
		second.detach( Dungeon.hero.belongings.backpack );

		if (second instanceof Armor){
			BrokenSeal seal = ((Armor) second).checkSeal();
			if (seal != null){
				Dungeon.level.drop( seal, Dungeon.hero.pos );
			}
		}

		if (first.isEquipped( Dungeon.hero )) {
			((EquipableItem)first).doUnequip( Dungeon.hero, true );
		}
		if (first instanceof MissileWeapon && first.quantity() > 1){
			first = first.split(1);
		}
		long level = first.level();
		//adjust for curse infusion
		if (first instanceof Weapon && ((Weapon) first).curseInfusionBonus) level--;
		if (first instanceof Armor && ((Armor) first).curseInfusionBonus) level--;
		if (first instanceof Wand && ((Wand) first).curseInfusionBonus) level--;
		first.level(level+second.level()+1); //prevents on-upgrade effects like enchant/glyph removal
		if (first instanceof MissileWeapon && !Dungeon.hero.belongings.contains(first)) {
			if (!first.collect()){
				Dungeon.level.drop( first, Dungeon.hero.pos );
			}
		}
		Dungeon.gold -= getGold();
		Dungeon.hero.spendAndNext( 2f );
		Badges.validateItemLevelAquired( first );
		Item.updateQuickslot();

		Notes.remove( Notes.Landmark.TROLL );
	}

	public static long getGold() {
		return (45 + 15L * Dungeon.escalatingDepth()) * 25;
	}

	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}
	
	@Override
	public void damage( long dmg, Object src ) {
		//do nothing
	}

	@Override
	public boolean add( Buff buff ) {
		return false;
	}
	
	@Override
	public boolean reset() {
		return true;
	}

	public static class Quest {

		private static int type = 0;
		public static final int OLD = 0;
		public static final int CRYSTAL = 1;
		public static final int GNOLL = 2;
		public static final int FUNGI = 3; //The fungi quest is not implemented, only exists partially in code
		//pre-v2.2.0
		private static boolean alternative; //false for mining gold, true for bat blood

		//quest state information
		private static boolean spawned;
		private static boolean given;
		private static boolean started;
		private static boolean bossBeaten;
		private static boolean completed;

		//reward tracking. Stores remaining favor, the pickaxe, and how many of each reward has been chosen
		public static int favor;
		public static Item pickaxe;
		public static int reforges; //also used by the pre-v2.2.0 version of the quest
		public static int hardens;
		public static int upgrades;
		public static int smiths;

		//pre-generate these so they are consistent between seeds
		public static ArrayList<Item> smithRewards;
		
		public static void reset() {
			type        = 0;
			alternative = false;

			spawned		= false;
			given		= false;
			started     = false;
			bossBeaten  = false;
			completed	= false;

			favor       = 0;
			pickaxe     = new Pickaxe().identify();
			((Pickaxe)pickaxe).tier += 5*Dungeon.cycle;
			reforges    = 0;
			hardens     = 0;
			upgrades    = 0;
			smiths      = 0;

			smithRewards = null;
		}
		
		private static final String NODE	= "blacksmith";

		private static final String TYPE    	= "type";
		private static final String ALTERNATIVE	= "alternative";

		private static final String SPAWNED		= "spawned";
		private static final String GIVEN		= "given";
		private static final String STARTED		= "started";
		private static final String BOSS_BEATEN	= "boss_beaten";
		private static final String COMPLETED	= "completed";

		private static final String FAVOR	    = "favor";
		private static final String PICKAXE	    = "pickaxe";
		private static final String REFORGES	= "reforges";
		private static final String HARDENS	    = "hardens";
		private static final String UPGRADES	= "upgrades";
		private static final String SMITHS	    = "smiths";
		private static final String SMITH_REWARDS = "smith_rewards";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				node.put( TYPE, type );
				node.put( ALTERNATIVE, alternative );

				node.put( GIVEN, given );
				node.put( STARTED, started );
				node.put( BOSS_BEATEN, bossBeaten );
				node.put( COMPLETED, completed );

				node.put( FAVOR, favor );
				if (pickaxe != null) node.put( PICKAXE, pickaxe );
				node.put( REFORGES, reforges );
				node.put( HARDENS, hardens );
				node.put( UPGRADES, upgrades );
				node.put( SMITHS, smiths );

				if (smithRewards != null) node.put( SMITH_REWARDS, smithRewards );
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {

			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {
				type = node.getInt(TYPE);
				alternative	=  node.getBoolean( ALTERNATIVE );

				given = node.getBoolean( GIVEN );
				started = node.getBoolean( STARTED );
				bossBeaten = node.getBoolean( BOSS_BEATEN );
				completed = node.getBoolean( COMPLETED );

				favor = node.getInt( FAVOR );
				if (node.contains(PICKAXE)) {
					pickaxe = (Item) node.get(PICKAXE);
				} else {
					pickaxe = null;
				}
				if (node.contains("reforged")){
					//pre-v2.2.0 saves
					reforges = node.getBoolean( "reforged" ) ? 1 : 0;
				} else {
					reforges = node.getInt( REFORGES );
				}
				hardens = node.getInt( HARDENS );
				upgrades = node.getInt( UPGRADES );
				smiths = node.getInt( SMITHS );

				if (node.contains( SMITH_REWARDS )){
					smithRewards = new ArrayList<>((Collection<Item>) ((Collection<?>) node.getCollection( SMITH_REWARDS )));
				}

			} else {
				reset();
			}
		}
		
		public static ArrayList<Room> spawn( ArrayList<Room> rooms ) {
			if (!spawned && (Dungeon.depth == PsycheChest.questDepth ||
					(Dungeon.depth > 11 && Random.Int( 15 - Dungeon.depth ) == 0))) {
				
				rooms.add(new BlacksmithRoom());
				spawned = true;

				//Currently cannot roll the fungi quest, as it is not fully implemented
				type = Random.IntRange(1, 2);
				alternative = false;
				
				given = false;
				generateRewards( true );

			}
			return rooms;
		}

		public static Class<BlacksmithWeapon>[] smithWeapons = new Class[]{StarlightSmasher.class, RegrowingSlasher.class, FiringSnapper.class, GleamingStaff.class, FantasmalStabber.class};

		public static void generateRewards( boolean useDecks ){
			smithRewards = new ArrayList<>();
			smithRewards.add(Reflection.newInstance(smithWeapons[Random.Int(smithWeapons.length)]));
			smithRewards.add(Reflection.newInstance(smithWeapons[Random.Int(smithWeapons.length)]));
			while (smithRewards.get(0).getClass() == smithRewards.get(1).getClass()) {
				smithRewards.remove(1);
				smithRewards.add(Reflection.newInstance(smithWeapons[Random.Int(smithWeapons.length)]));
			}

			//30%:+0, 45%:+1, 20%:+2, 5%:+3
			int rewardLevel;
			float itemLevelRoll = Random.Float();
			if (itemLevelRoll < 0.3f){
				rewardLevel = 0;
			} else if (itemLevelRoll < 0.75f){
				rewardLevel = 1;
			} else if (itemLevelRoll < 0.95f){
				rewardLevel = 2;
			} else {
				rewardLevel = 3;
			}

			rewardLevel = (int) Math.pow(rewardLevel, Dungeon.cycle + 1);

			for (Item i : smithRewards){
				i.level(rewardLevel);
				if (i instanceof Weapon) {
					((Weapon) i).enchant(null);
				}
				i.cursed = false;
			}
		}

		public static int Type(){
			return type;
		}

		public static boolean given(){
			return given;
		}

		public static boolean started(){
			return started;
		}

		public static void start(){
			started = true;
		}

		public static boolean beatBoss(){
			return bossBeaten = true;
		}

		public static boolean bossBeaten(){
			return bossBeaten;
		}

		public static boolean completed(){
			return given && completed;
		}

		public static void complete(){
			completed = true;

			favor = 0;
			DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
			if (gold != null){
				favor += Math.min(3000, gold.quantity()*75);
				gold.detachAll(Dungeon.hero.belongings.backpack);
			}

			Pickaxe pick = Dungeon.hero.belongings.getItem(Pickaxe.class);
			if (pick.isEquipped(Dungeon.hero)) {
				boolean wasCursed = pick.cursed;
				pick.cursed = false; //so that it can always be removed
				pick.doUnequip(Dungeon.hero, false);
				pick.cursed = wasCursed;
			}
			pick.detach(Dungeon.hero.belongings.backpack);
			Quest.pickaxe = pick;

			if (bossBeaten) favor += 1500;

			Statistics.questScores[2] = favor;
		}

		public static boolean rewardsAvailable(){
			return favor > 0
					|| (Quest.smithRewards != null && Quest.smiths > 0)
					|| (pickaxe != null && Statistics.questScores[2] >= 2500);
		}

		//if the blacksmith is generated pre-v2.2.0, and the player never spawned a mining test floor
		public static boolean oldQuestMineBlocked(){
			return type == OLD && !Dungeon.levelHasBeenGenerated(Dungeon.depth, 1);
		}

		public static boolean oldBloodQuest(){
			return type == OLD && alternative;
		}

		public static boolean oldMiningQuest(){
			return type == OLD && !alternative;
		}
	}
}
