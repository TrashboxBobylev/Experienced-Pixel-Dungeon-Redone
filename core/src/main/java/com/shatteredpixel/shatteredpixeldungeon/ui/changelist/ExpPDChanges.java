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

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.items.BlackPsycheChest;
import com.shatteredpixel.shatteredpixeldungeon.items.ExpGenerator;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.fishingrods.BasicFishingRod;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfAdrenalineSurge;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class ExpPDChanges {
	
	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){

        ChangeInfo changes = new ChangeInfo("ExpPD-2.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 6th, 2020\n" +
                        "_-_ 2 days after Experienced Pixel Dungeon 2.1\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.SCROLL_TIWAZ, null), new ScrollOfUpgrade().trueName(),
                "The whole stack of SoU can be applied at once with special action."));
        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.EXOTIC_CRIMSON, null), new PotionOfAdrenalineSurge().trueName(),
                "This potion replaces Adrenaline Surge and allows to spawn Rat King on any depth."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other",
                "_-_ Added escalation data for cycle 3.\n" +
                        "_-_ Fixed artifacts not charging at very high levels.\n" +
                        "_-_ Fixed fishing not working entirely.\n" +
                        "_-_ Fixed transmutation crash for high-tier items.\n" +
                        "_-_ Tome of Mastery on rebeating Tengu is named Tome of Remastery.\n" +
                        "_-_ Adjusted inventory space and window.\n" +
                        "_-_ Blacksmith now transfers upgrades to higher tiered items.\n" +
                        "_-_ Power Plants are stackable now.\n" +
                        "_-_ Wand's charges are shown in percentage when in inventory, but exact charges are shown in description\n" +
                        "_-_ Fixed Mage's Staff ignoring his charge bonus."));


        changes = new ChangeInfo("ExpPD-2.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 4th, 2020\n" +
                        "_-_ 2 days after Experienced Pixel Dungeon 2.0\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.KING, 0, 0, 16, 16), "Dwarf King",
                "Fixed rounding error, when Dwarf King didn'tget damaged enough to progress boss fight." ));

        changes.addButton( new ChangeButton(new ItemSprite(new BasicFishingRod()), "Fishing",
                "_-_ Hook will disappear after leaving the depth or field of view properly (the bug was when you can't cast new hook).\n\n" +
                        "_-_ Hook can't be casted into walls.\n\n" +
                        "_-_ Now you can get some equipment (maybe).\n\n" +
                        "_-_ Fishing rods now give experience if enough rare item is caught."));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ARTIFACT_HOLDER, null), "Artifacts",
                "Upgrading past +10 should work for more than one upgrade."));

        changes.addButton( new ChangeButton(new ItemSprite(new ExpGenerator()), "EXP Generator",
                "Added power plant for passive experience generation. Can be bought at shops."));

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.TROLL, 0, 0, 13, 16), "Blacksmith",
                "_-_ Removed the limit for reforging.\n\n" +
                        "_-_ (was in previous update but whatever) Blacksmith can transfer upgrades with items of same equipment type.\n\n" +
                        "_-_ Bat quest can't appear on dungeon's cycles." ));

        changes.addButton( new ChangeButton(new ItemSprite(new WandOfMagicMissile()), "Wands",
                "_-_ Removed the limitation for wand charges.\n\n" +
                        "_-_ Mage's Staff gives 33% additional charges instead of just one.\n\n" +
                        "_-_ Magic Charge buff now adds levels to all wands rather than overriding them."));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SWORD, new ItemSprite.Glowing( 0x8844CC )), "Enchanting",
                "_-_ Removed the erasure mechanic, so enchantments stay forever on equipment.\n\n" +
                        "_-_ As result, Magical Infusion is no longer obtainable."));

        changes.addButton( new ChangeButton(new ItemSprite(new VelvetPouch()), "Dungeon's Cycles changes",
                "_-_ Armor kit and Tome of Mastery can be reobtained on dungeon's cycles.\n\n" +
                        "_-_ Armor on dungeon's cycles shouldn't crash the game with wrong tier images.\n\n" +
                        "_-_ Storage bags cannot reappear on dungeon's cycles, their capacity is increased to 40.\n\n" +
                        "_-_ Sad Ghost gives correctly tiered items on dungeon's cycles.\n\n" +
                        "_-_ Darts now respond to dungeon's cycles overall empowerment; Crossbow is now viable weapon for mid-game."));

        changes.addButton( new ChangeButton(Icons.get(Icons.INFO), "Updates",
                "Added GitHub powered updates."));

        changes = new ChangeInfo("ExpPD-2.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 2th, 2020\n" +
                "_-_ 286 days after ExperiencedPixel Dungeon v1.3\n" +
                "_-_ 34 days after ShPD 0.8.1 source release\n" +
                "\n" +
                "Dev commentary will be added here in the future."));

        changes.addButton( new ChangeButton(new Image(Assets.Interfaces.BUFFS_LARGE, 80, 32, 16, 16), "Experience changes",
                "All limits are removed, you can farm EXP forever!" ));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.PSYCHE_CHEST, null), "Grind Switcher",
                "Added new item into starting inventory - Fate Lock. It allows to toggle 'grinding' mode, where you get SoU from experience, and mobs drop random items.\n\n" +
                        "Also it allows to reset the level for new loot with exchange of 50% max HP."));
        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.POTION_GOLDEN, null), new PotionOfExperience().trueName(),
                "Reworked both regular and exotic versions of Potion of Experience.\n\n" +
                        "_-_ _Potion of Experience_ now provides 2x EXP gain boost and additional item on killing mobs while in grinding mode.\n\n" +
                        "_-_ _Potion of Holy Furor_ is now _Potion of Overload_ that gives effect of spawning new monsters on killing older monsters."));
        changes.addButton( new ChangeButton(new ItemSprite(new BasicFishingRod()), "Fishing",
                "Ring of Wealth have been replaced by fishing mechanic.\n\n" +
                        "_-_ There is totally 4 fishing rods, appearing at different stages of the game.\n\n" +
                        "_-_ To begin fishing, CAST fishing rod to water. Once hook will signal about item, UNCAST the fishing rod to get your loot.\n\n" +
                        "_-_The amount of loot and farming speed are determined by rod's tier and upgrades."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.KEEPER, 0, 0, 14, 14), "Shop changes",
                "Added more item categories in the shop." ));
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ARTIFACT_HOLDER, null), "Artifacts",
                "You can upgrade artifacts with scrolls, once they reached +10, except for Chalice of Blood."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.RATKING, 0, 0, 16, 17), "Bartering",
                "Added bartering with Rat King.\n\n" +
                        "_-_ To start, throw something to Rat King's position. He will exchange it for something totally different.\n\n" +
                        "_-_ However, he doesn't give gold and need to rest 1 turn to exchange again." ));
        changes.addButton( new ChangeButton(new ItemSprite(new BlackPsycheChest()), "Dungeon Cycles",
                "Amulet of Yendor is replaced by Black Fate Lock.\n\n" +
                        "_-_ You can choose to reset dungeon to its unexplored state, or go to superboss area.\n\n" +
                        "_-_ When reset, you appear on first floor of dungeon, with more powerful loot and enemies.\n\n" +
                        "_-_All variables are reset on each resetting, so you can get shops, potions of strength and quests again."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.MIMIC, 0, 48, 16, 16), "Black Mimic",
                "Black Mimic holds the Amulet and will not give it back without tedious and dangerous fight! Can you beat the ultimate mob, which uses abilities of every Shattered boss?" ));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Increased hero's backpack size.\n\n" +
                        "_-_ Removed Patreon and languages buttons.\n\n" +
                        "_-_ Removed hero and challenge locks."));
    }

}
