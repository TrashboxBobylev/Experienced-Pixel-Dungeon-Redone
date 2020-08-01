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
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.BlackPsycheChest;
import com.shatteredpixel.shatteredpixeldungeon.items.PsycheChest;
import com.shatteredpixel.shatteredpixeldungeon.items.fishingrods.BasicFishingRod;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
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
		
		ChangeInfo changes = new ChangeInfo( "v0.1.X", true, "");
		changes.hardlight( Window.TITLE_COLOR);
		changeInfos.add(changes);

		add_v0_1_0_Changes(changeInfos);
	}
	
	public static void add_v0_1_0_Changes( ArrayList<ChangeInfo> changeInfos ){
		
		ChangeInfo changes = new ChangeInfo("ExpPD-2.0", false, "");
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
