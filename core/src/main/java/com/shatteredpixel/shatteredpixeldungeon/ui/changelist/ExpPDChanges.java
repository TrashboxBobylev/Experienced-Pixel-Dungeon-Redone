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

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.BlackPsycheChest;
import com.shatteredpixel.shatteredpixeldungeon.items.ExpGenerator;
import com.shatteredpixel.shatteredpixeldungeon.items.TicketToArena;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.fishingrods.BasicFishingRod;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Cheese;
import com.shatteredpixel.shatteredpixeldungeon.items.food.CheeseChunk;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.IdentificationBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Vampirism;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.GambleBag;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.IdealBag;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.TenguTreasureBag;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfEarthblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greataxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Longsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Whip;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.blacksmith.StarlightSmasher;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.OofSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class ExpPDChanges {

	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){

            ChangeInfo changes = new ChangeInfo("ExpPD-2.19", true, "");
            changes.hardlight(0xFF4242);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN.get(), "Developer Commentary",
                    "_-_ Released September 29th, 2024\n" +
                            "_-_ 47 days after Experienced Pixel Dungeon 2.18.2\n" +
                            "_-_ 1518 days after Experienced Pixel Dungeon 2.0\n" +
                            "_-_ 1840 days after Experienced Pixel Dungeon 1.0\n\n" +
                    Messages.get(Dungeon.class, "last_words")));

            changes.addButton( new ChangeButton(Icons.SHPX.get(), "ShPD content",
                    "Implemented Shattered's 2.5.2, with following additions:\n\n" +
                            "_-_ Upgrade application window has been overhauled to better show Experienced's scaling and support multi-upgrading and fishing rods.\n" +
                            "_-_ The amount of items gathered from Rat King's bartering is now tracked in the journal.\n" +
                            "_-_ Added all new items and monsters into journal, with tracking their usages.\n" +
                            "_-_ Journal and upgrade info text supports 64-bit numbers."));

            changes.addButton( new ChangeButton(new IdentificationBomb(),
                    "Added a new \"bomb\", that explodes inside of hero's inventory to destroy all items, that are not identified."));

            changes.addButton( new ChangeButton(Icons.BUFFS.get(), "Buffs and Nerfs",
                    "_-_ Buffed Smoke Bomb:\n" +
                            "   _-_ now has ninja log with max stats appear\n" +
                            "_-_ Fishing rods can have their efficiency extend to 64-bit limit\n" +
                            "_-_ Fishing rods are now 1 upgrade stronger\n" +
                            "_-_ Nerfed amount of effective tiers the weapons and armor get in cycles"
            ));

            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                    "_-_ Changed deferred damage to properly support 64-bit numbers\n" +
                            "_-_ Alchemist's Toolkit can only gain 2 billion charge at once"));

            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                    "_-_ Fixed pickaxe being weaker than indended in later cycles\n" +
                            "_-_ Fixed stalling when receiving overwhelming amount of energy for Alchemist's Toolkit and Ethereal Chains\n" +
                            "_-_ Clayball should be now less prone to crashes\n" +
                            "_-_ Further reduced Bbat's approaching speed from 100 to 25\n" +
                            "_-_ Fixed crash with crystal guardian being invisible when damaged\n" +
                            "_-_ Fixed crash with Rusty Shield somehow going for null with armor\n" +
                            "_-_ Fixed certain artifacts being capped by their non-upgraded charge cap"));

            changes = new ChangeInfo("ExpPD-2.18.2", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN.get(), "Developer Commentary",
                    "_-_ Released August 12th, 2024\n" +
                            "_-_ 71 days after Experienced Pixel Dungeon 2.18.1"));

            changes.addButton( new ChangeButton(Icons.BUFFS.get(), "Buffs and Nerfs",
                    "_-_ Changed Greatsword:\n" +
                            "   _-_ decreased the chance to spawn stone knight from 100% to 50%\n" +
                            "   _-_ increased the timespan of knights from 150 to 250 turns\n" +
                            "   _-_ now can be used by ghost\n" +
                            "_-_ Changed OOF Thieves:\n" +
                            "   _-_ added cycle 5 stats\n" +
                            "   _-_ now spawns 100x less frequently (and 25x less frequently), but gives a lot of level ups, the amount of which scales with cycling\n" +
                            "   _-_ now drops ideal bags at 100% initial chance instead of big lucky bags at 50% initial chance\n" +
                            "_-_ Mace's disintegration beam now scales with cycling\n" +
                            "_-_ Capped Ethereal Chains' regeneration at 1 charge per turn\n" +
                            "_-_ Changed equipment drops from fishing:\n" +
                            "   _-_ changed the modifier from 12 to 6^(cycle+1)\n" +
                            "   _-_ are no longer capped at 2 billion upgrades\n" +
                            "_-_ Limited bonus fishing strength at 15000\n" +
                            "_-_ Reduced Fate Lock's stat increase modifier from 1.12x to 1.045x and removed speed boost\n" +
                            "_-_ Tweaked Throwing Mining Tool:\n" +
                            "   _-_ travels slightly slower and costs 2 turns to throw instead of 1\n" +
                            "   _-_ now only mines tiles visible in hero's FOV, otherwise shows the visual of hero throwing returning tool in direction of tile\n" +
                            "   _-_ no longer stunlocks player if trying to hit coords outside the map\n" +
                            "   _-_ now can trigger traps\n" +
                            "_-_ Changed Elixir of Luck's droprate:\n" +
                            "   _-_ increased \"chance\" from 1/150 to 1/135\n" +
                            "   _-_ is no longer affected by luck"
            ));

            changes.addButton(new ChangeButton(new Image(new MimicSprite.Black()), "Darkened Mimic",
                "_-_ Tweaked the cycle stat scaling from 12 to 7, but increased base stat modifier by 75%-125%\n" +
                        "_-_ Now has 33% damage reduction with one pylon down and 67% damage reduction with two pylons down\n" +
                        "_-_ Reduced supercharge warning time from 3 turns to 1\n" +
                        "_-_ Ability cooldown decreases by 2 turns with each pylon down\n" +
                        "_-_ Reduced max spare time on crushing rocks attack\n" +
                        "_-_ Out-of-reach attack can be now either corrosive gas or monsters\n" +
                        "_-_ With Badder Bosses challenge:\n" +
                        "   _-_ increased summon amount from 1x to 1x-3x\n" +
                        "   _-_ can use out-of-reach attack instantly\n" +
                        "_-_ Music fades out to nothing after being defeated"
            ));

            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                "_-_ Changed refined lucky bag's chance from \"35%\" to 8.33%\n" +
                        "_-_ Tweaked Soul Mark's healing to properly support 64-bit numbers\n" +
                        "_-_ Tweaked Chalice of Blood's healing to properly support 64-bit numbers\n" +
                        "_-_ Tweaked Retribution/Psionic Blast scrolls to properly support 64-bit numbers\n" +
                        "_-_ OOF Thief now spawns 100x less frequently (and 25x less frequently), but gives a lot of level ups, the amount of which scales with cycling\n" +
                        "_-_ Gauntlet's duelist ability now only plays animation once\n" +
                        "_-_ Telekinetic Grab now stops at first item, that is impossible to pick up\n" +
                        "_-_ Added Battlemage's description for unstable wand\n" +
                        "_-_ Fishing rods now retract their hooks, if they disappear from inventory by any means\n" +
                        "_-_ Added issue tracker button to feedback screen"));

            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                "_-_ Fixed crash with fishing rod's hook having to disappear during descending\n" +
                        "_-_ Fixed crash with Elixir of Luck being in trinket creation set\n" +
                        "_-_ Fixed slime's equipment drops\n" +
                        "_-_ Fixed crashes with Clay trying to damage NPCs or mobs that cannot be damaged or debuffed\n" +
                        "_-_ Fixed missile weapons stacking even if their levels are different\n" +
                        "_-_ Fixed dupe glitch with extracting X*Y upgrades out of Y +X missiles\n" +
                        "_-_ Fixed Scroll of Midas being non-consumable\n" +
                        "_-_ Fixed Potion of Overload and Scroll of Midas having no name or description\n" +
                        "_-_ Fixed Blacksmith consuming double favor on reforging, than intended"));

            changes = new ChangeInfo("ExpPD-2.18.1", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN.get(), "Developer Commentary",
                    "_-_ Released June 02nd, 2024\n" +
                            "_-_ 7 days after Experienced Pixel Dungeon 2.18"));

            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                    "_-_ Increased refined lucky bag's chance from \"35%\" to \"60%\"\n" +
                            "_-_ Increased Vampiric enchantment's healing by 4x and chance by 2x\n" +
                            "_-_ Tweaked health bars to properly support 64-bit numbers\n" +
                            "_-_ Significantly increased damage and protection stats of equipment in cycles, again\n" +
                            "_-_ Added a new thing for Faith is my Armor"));

            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                    "_-_ Fixed mob HP being capped at 32-bit limit\n" +
                            "_-_ Fixed Ring of Tenacity being missing\n" +
                            "_-_ Fixed incorrect item sprites from last update\n" +
                            "_-_ Fixed crashes from Exotic Crystals trinket generating incorrect versions of scrolls\n" +
                            "_-_ Fixed crash with Telekinetic Grab's lack of validity checks\n" +
                            "_-_ Fixed Regrowth Slasher's ghosts being possible to cleanse\n" +
                            "_-_ Fixed Experienced's spells not being possible to create\n" +
                            "_-_ Fixed Cheese Chunk not being possible to create"));

            changes = new ChangeInfo("ExpPD-2.18", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN.get(), "Developer Commentary",
                    "_-_ Released May 26th, 2024\n" +
                            "_-_ 45 days after Experienced Pixel Dungeon 2.17.2"));
            changes.addButton( new ChangeButton(Icons.SHPX.get(), "ShPD content",
                "Implemented Shattered's 2.4.1, with following additions:\n\n" +
                        "_-_ Combat rolls use 64-bit numbers, both normal and inversed normal.\n" +
                        "_-_ Changed Experienced's alchemy items to fit for alchemy overhaul.\n" +
                        "_-_ Changed Experienced's new and changed weapons to fit for duelist overhaul.\n" +
                        "_-_ Black Mimic and related content is now called Darkened Mimic to account for Shattered's addition of ebony mimics.\n" +
                        "_-_ Experienced's Rat Skull is now called Remorphed Rat Skull to account for Shattered's addition of rat skull trinket."));
            changes.addButton( new ChangeButton(Icons.BUFFS.get(), "Buffs and Nerfs",
                    "_-_ Buffed war hammers:\n" +
                            "   _-_ increased attack speed from 0.4 to 0.5\n" +
                            "   _-_ changed pitch to have intended effect\n" +
                            "   _-_ reduced attacker's max paralysis duration by 1 turn\n" +
                            "_-_ Changed OOF Thieves:\n" +
                            "   _-_ decreased stealing counter constant from 3 to 4 (should steal slower now)\n" +
                            "   _-_ added a tip on how to recover stolen items\n" +
                            "_-_ Reduced upgrade scaling on Sai and Scimitar duelist abilities by 99.6%\n" +
                            "_-_ Reduced amount of weapon charges used by Blacksmith weapons from 3 to 2\n" +
                            "_-_ Increased amount of gold given by Scroll of Midas by ~3x\n" +
                            "_-_ Reduced Fate Lock's stat increase modifier from 1.16x to 1.12x\n" +
                            "_-_ Decreased amount of rerolls on burning Lucky Bags from 3 to 2\n" +
                            "_-_ Telekinetic Grab now grabs stuff in 3x3"
            ));
            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                    "_-_ Inter-level teleports are no longer allowed in Experienced's subworlds\n" +
                            "_-_ Tweaked desktop inventory:\n" +
                            "   _-_ gold and energy are now at the bottom to give them more space\n" +
                            "   _-_ added button for Incredibly Cheesy Cheest\n" +
                            "_-_ Tweaked score multipliers:\n" +
                            "   _-_ increased max progression score from 250k to 600k\n" +
                            "   _-_ treasure score is now divided by 1000, but increased max treasure score from 100k to 1000k\n" +
                            "_-_ Cycles can now be seen on save loading screen\n" +
                            "_-_ The \"city\" entrance in Darkened Mimic's arena now leads up as well"
            ));
            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                    "_-_ Fixed fishing hook going inside walls instead of ground\n" +
                            "_-_ Fixed grandmaster gold collecting badge to be properly larger than preceding badges\n" +
                            "_-_ Fixed Broken Enderium Blade and Energite Bottle costing nothing\n" +
                            "_-_ Fixed Scroll of Midas affecting fishing hook\n" +
                            "_-_ Fixed run statistics saving as 64-bit numbers, but loading as 32-bit numbers\n" +
                            "_-_ Fixed treasure bags using as much turns as picking up all items in them individually\n" +
                            "_-_ Fixed cycle's score multiplier not being loaded in rankings after the first time\n" +
                            "_-_ Fixed fate lock taking more HP than asked by code and pseudo-killing player\n" +
                            "_-_ Fixed war hammers stunning player for infinite amount of time\n" +
                            "_-_ Fixed prices for all Blacksmith's rewards only scaling from amount of reforges"
            ));


            changes = new ChangeInfo("ExpPD-2.17.2", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN.get(), "Developer Commentary",
                    "_-_ Released April 11th, 2024\n" +
                            "_-_ 2 days after Experienced Pixel Dungeon 2.17.1"));
            changes.addButton( new ChangeButton(Icons.BUFFS.get(), "Buffs and Nerfs",
                    "_-_ Changed Ring of Elements:\n" +
                            "   _-_ now appears 2x less frequently in Refined Lucky Bag\n" +
                            "   _-_ increased elemental protection from 80% to 98%\n" +
                            "_-_ Nerfed harden mechanic:\n" +
                            "   _-_ now 10x less potent\n" +
                            "_-_ Massively increased stat escalation of weapons from later cycles and added one for cycle 5\n" +
                            "_-_ Buffed the probability of getting Refined Lucky Bag from \"20%\" to \"35%\""
            ));
            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                    "_-_ Changed Dwarf King:\n" +
                            "   _-_ increased max HP by 3x\n" +
                            "   _-_ no longer has second phase (screw Evan for making it so hard to maintain\n" +
                            "_-_ Capped some of AoE traps to not last forever on later cycles\n" +
                            "_-_ Massively increased virtual depth escalation with cycles to make debuffs and gases more potent\n" +
                            "_-_ Capped Blazing enchantment's particle amount at 100"
            ));
            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                    "_-_ Fixed Dwarf King becoming unbeatable when affected by Fate Lock's stat boost\n" +
                            "_-_ Fixed harden boost being applied to every weapon and armor regardless of them being hardened or not\n" +
                            "_-_ Fixed hero's accuracy and evasion being decreased with luck\n" +
                            "_-_ Fixed Scroll of Determination's text being not available"
            ));

            changes = new ChangeInfo("ExpPD-2.17.1", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN.get(), "Developer Commentary",
                "_-_ Released April 9th, 2024\n" +
                        "_-_ 2 days after Experienced Pixel Dungeon 2.17"));
            changes.addButton( new ChangeButton(Icons.BUFFS.get(), "Buffs and Nerfs",
                "_-_ Changed Ring of Haste:\n" +
                        "   _-_ now 3x less likely to appear as other rings\n" +
                        "   _-_ now slows you down\n" +
                        "_-_ Glyph of Swiftness is now a curse and slows player down\n" +
                        "_-_ Glyph of Flow is 5000x less potent; base speed boost reduced from _2x_ to _1.25x_\n" +
                        "_-_ Buffed Ring of Elements:\n" +
                        "   _-_ now reduces _80%_ of elemental damage instead of 50%\n" +
                        "_-_ Buffed Warhammer and Starlight Smasher:\n" +
                        "   _-_ increased attack delay from _3 turns_ to _2.5 turns_\n" +
                        "   _-_ decreased attacker's stun by 1 turn and allowed it to be affected by furor\n" +
                        "   _-_ decreased Starlight Smasher's special attack's delay from _5 turns_ to _3.5 turns_\n" +
                        "   _-_ decreased Starlight Smasher's max damage from _10x_ of base to _8.5x_ of base\n" +
                        "_-_ Reworked/nerfed Directive perk:\n" +
                        "   _-_ now grants 1.5 turns of haste instead of 3 free steps"));
            changes.addButton( new ChangeButton(new ItemSprite(new IdealBag()), "New Treasure Bags",
                "_-_ Chance to get Refined Lucky Bag is _increased to 20%_, but the mechanic with worst rolls to get it works correctly now\n" +
                        "_-_ _Refined Lucky Bag_ is now less likely to give unstackable items like equipment\n" +
                        "_-_ _Refined Lucky Bag's_ chance to drop exclusive item is now affected by luck and increased from 4% to 4.5%"));
            changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.PSYCHE_CHEST), "Fate Lock",
                "Changed Fate Lock's reset mechanic:\n\n" +
                        "_-_ Now uses 45% of player's health, down from 50%\n" +
                        "_-_ Enemies get _8%_ faster and _16%_ stronger with each reset\n" +
                        "_-_ The only way to reduce this boost is to go to new cycle\n" +
                        "_-_ Run's current duration also affects enemy stats"));
            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                "_-_ Arena's guaranteed gold from each mob now directly goes into player's purse"
            ));
            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                "_-_ Fixed a crash caused by fishing attempts\n" +
                        "_-_ Fixed Rat King not being rendered on ascension's surface scene"
            ));
            changes = new ChangeInfo("ExpPD-2.17.0", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN.get(), "Developer Commentary",
                    "_-_ Released April 7th, 2024\n" +
                            "_-_ 88 days after Experienced Pixel Dungeon 2.16.3\n" +
                            "_-_ 142 days after Experienced Pixel Dungeon 2.16"));
            changes.addButton( new ChangeButton(Icons.SHPX.get(), "ShPD content",
                "Implemented Shattered's 2.3.2, with following additions:\n\n" +
                        "_-_ Added new floating text icons for Experienced's permanent boosts.\n" +
                        "_-_ Rat King drops cheese chunk as his remains item.\n" +
                        "_-_ Gnoll quest enemies now have stats for dungeon cycles.\n" +
                        "_-_ Upgraded Android Gradle Plugin from 3.6.4 to latest version (finally!)."));
        changes.addButton( new ChangeButton(Icons.PLUS.get(), "Prolonging numbers",
                "The most significant change in this update is tweaking certain numbers from _stored in 32-bit variables to 64-bit variables_, increasing their max value from ~2 billion to ~9 quintillion:\n" +
                        "_-_ gold and alchemical energy\n" +
                        "_-_ mob experience value\n" +
                        "_-_ current and max health of living entities\n" +
                        "_-_ dealt damage from most of sources\n" +
                        "_-_ amount of stored and gained experience\n" +
                        "_-_ upgrades and amount of items\n" +
                        "_-_ wand and artifact charges\n" +
                        "_-_ equipment stats like damage, blocking and other effects\n" +
                        "_-_ limits on buffs like barksking and healing\n" +
                        "_-_ statistics and ranking stats\n\n" +
                        "This is still potentially unstable overhaul to the codebase."));
        changes.addButton( new ChangeButton(new ItemSprite(new IdealBag()), "New Treasure Bags",
                "Added two new treasure bags, which are obtained by burning lucky bags:\n\n" +
                        "_-_ _Burnt Bag_ contains less items than normal bags, and they can contain burnt meat instead of loot.\n" +
                        "_-_ _Refined Lucky Bag_ can appear instead of Burnt Bag with 10% chance (or is it?) and contains as many items as 10 Lucky Bags combined! " +
                        "There are some new silly, yet useful loot that can drop from Refined Lucky Bag, including fabled elemental ring..."));
        changes.addButton( new ChangeButton(new ItemSprite(new BlackPsycheChest()), "Cycling",
                "_-_ Dart traps and necromancers now scale their damage with dungeon cycles\n" +
                        "_-_ Implemented cycle 5, with new fishing rod and even more stat increases\n" +
                        "_-_ Buffed Black Mimic's stats to exponentially scale with cycling"));
        changes.addButton( new ChangeButton(Icons.BUFFS.get(), "Buffs and Nerfs",
                "_-_ Buffed Bbat:\n" +
                        "   _-_ now has extra range\n" +
                        "   _-_ decreased cooldown to 700 turns\n" +
                        "_-_ Tweaked Pickaxe:\n" +
                        "   _-_ now gets tiers with cycles like any other weapon\n" +
                        "   _-_ now has 50% more damage at base, but gets 50% less damage from upgrading\n" +
                        "_-_ \"Nerfed\" Iron Will:\n" +
                        "   _-_ no longer triggers from debuffs or hunger\n" +
                        "   _-_ this thing was very annoying to some users, which is understandable\n" +
                        "_-_ Buffed Blacksmith's harden favor from 0.15% boost per upgrade to 0.2% boost per upgrade\n" +
                        "_-_ Weapons and armors in later cycles scale better both in base damage and scaling",

                        "_-_ Changed Preparation:\n" +
                        "   _-_ now detaches when Dirk/Fantasmal Stabber is unequipped\n" +
                        "   _-_ slightly increased Dirk's max damage\n" +
                        "   _-_ reduced damage of missile weapon by 35% when used with Preparation\n" +
                        "_-_ Scroll of Determination is now consumed and identified correctly\n" +
                        "_-_ \"Excalibur's swing\" ability can longer be used with no weapon energy\n" +
                        "_-_ Ring effect levels are now capped at 200 billion\n" +
                        "_-_ Significantly nerfed \"Scrolls of Upgrade from experience\" mechanic:\n" +
                        "   _-_ changed experience requirements from 100/175/250/325/500/575 to 100/200/1250/11750/75000/500000\n" +
                        "_-_ Ring of Elements can no longer be obtained by normal means and be upgraded, but increased its base power to 50% reduction from 20%"));

            changes.addButton( new ChangeButton(Icons.PREFS.get(), "Unsorted and other changes",
                            "_-_ Significantly reduced Crystal Spire's health on cycles to account for pickaxe's damage, but still encouraging to upgrade it\n" +
                            "_-_ Skeletons deal 150% of their damage roll on exploding instead of static 6-12 damage\n" +
                            "_-_ Upgrade amount text now auto-resizes for large upgrade values\n" +
                            "_-_ Changed inventory UI to more comfortably fit all slots on landscape and desktop resolutions\n" +
                            "_-_ Tremendously optimized Scroll of Upgrade's usage for large numbers\n" +
                            "_-_ Projecting Runic Blade's projectile can pierce walls in same way as Projecting missile weapons do\n" +
                            "_-_ Yog-Dzewa's lasers now kill most of the players in one hit with Badder Bosses challenge"
            ));

            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                    "_-_ Fixed fishing rod bobbers staying on the screen even after being used\n" +
                            "_-_ Fixed OOF thief's desperation invulnerability not actually working\n" +
                            "_-_ Fixed Longsword's Duelist ability being usable at no charges\n" +
                            "_-_ Fixed cycle 4 DM-300 having too little health"
            ));

            changes = new ChangeInfo("ExpPD-2.16.3", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                    "_-_ Released January 10th, 2024\n" +
                            "_-_ 8 days after Experienced Pixel Dungeon 2.16.2"));
            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                    "_-_ Fixed the crash with slimes by removing the luck part.\n" +
                            "_-_ Fixed OOF thief issues with stealing only important items and nothing else.\n" +
                            "_-_ Attempted to fix the statue attack animation crash."
            ));

            changes = new ChangeInfo("ExpPD-2.16.2", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                    "_-_ Released January 2nd, 2024\n" +
                            "_-_ 44 days after Experienced Pixel Dungeon 2.16.1"));
            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                    "_-_ Implemented Chinese translation by catandA.\n" +
                            "_-_ Fully removed Shattered's hardening behavior.\n" +
                            "_-_ Regrowing Slasher's barkskin no longer stacks as a buff many times.\n" +
                            "_-_ Shortened the description of Rat King's crown.\n" +
                            "_-_ Tweaked Incredibly Cheesy Cheest:\n" +
                            "* now has its own icon\n" +
                            "* can no longer be bought in multiple quantities\n" +
                            "_-_ Buffed Bbat:\n" +
                            "* now flies\n" +
                            "* increased HP from 10(+2) to 11(+3)\n" +
                            "* increased evasion from 15(+2) to 16(+3)\n" +
                            "* increased accuracy from 12(+2) to 13(+3)\n" +
                            "_-_ Some very important items, like Ticket to Arena and Psyche Locks, can no longer be stolen or \"oofed\" at all."
            ));
            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                    "_-_ Fixed the Incredibly Cheesy Cheest duplication glitch.\n" +
                            "_-_ Fixed Sandals of Nature displaying !!!NO TEXT FOUND!!! when upgraded.\n" +
                            "_-_ Fixed Chalice of Blood displaying incorrect regeneration speed before +10.\n" +
                            "_-_ Fixed the lack of rare enchantments/glyphs when enchanting things."
            ));

            changes = new ChangeInfo("ExpPD-2.16.1", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                    "_-_ Released November 19th, 2023\n" +
                            "_-_ 2 days after Experienced Pixel Dungeon 2.16\n\n" +
                            "HOTFIXES TIME!!!"));
            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                    "_-_ Updated some of innate hero perk icons to make more sense.\n" +
                            "_-_ Removed the artifact upgrading to equivalent level when transmuted."
            ));
            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                    "_-_ Fixed the crash with fifth innate hero perks not having an icon."
            ));

            changes = new ChangeInfo("ExpPD-2.16", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                    "_-_ Released November 17th, 2023\n" +
                            "_-_ 38 days after Experienced Pixel Dungeon 2.15.4\n" +
                            "_-_ 126 days after Experienced Pixel Dungeon 2.15\n" +
                            "\n" +
                            "This release ports Shattered PD 2.2.1's source code and actually adds some new content."));
            changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered ports",
                    "Ported changes from Shattered 2.2.1.\n\n" +
                            "_-_ This includes a brand new quest in the caves, almost 20 minutes of new music, and substantial improvements to the prison and sewers quests."));
            changes.addButton( new ChangeButton(new BlacksmithSprite(), "Blacksmith Quest",
                    "Implemented new blacksmith quest, with some changes:\n\n" +
                            "_-_ Increased amount of favor granted by 50%.\n" +
                            "_-_ All crystal quest enemies and boss now have dungeon cycles stats defined.\n" +
                            "_-_ Crystal Spire has shorter attack cooldown and does doubled damage.\n\n" +
                            "_-_ Reforge favor is significantly cheaper and still uses Experienced's rules.\n" +
                            "_-_ Harden favor is reworked to grant percentage boost to weapon's damage and armor's defense, that scales with upgrades.\n" +
                            "_-_ Upgrade favor is replaced by Extract favor, which allows to \"extract\" all upgrades from certain item in form of scrolls.\n" +
                            "_-_ Smith favor now costs 2500 points (so it is effectively only usable once) and grants player a choice from unique blacksmith weapons instead of randomly generated equipment.\n" +
                            "_-_ Cash out favor now depends on cycling, giving more gold with higher progression."));
            changes.addButton( new ChangeButton(new ItemSprite(new StarlightSmasher()), "Blacksmith Weapons",
                    "Added five unique weapons, obtainable through new blacksmith quest, with unique effects and ability to scale up their damage in dungeon cycling.\n" +
                            "_-_ Starlight Smasher: huge crystal hammer, that can charge through hitting enemies to be thrown for devastating 3x3 attack.\n" +
                            "_-_ Regrowing Slasher: plant-themed sword, that can heal and buff you and your allies with plant effects.\n" +
                            "_-_ Firing Snapper: firey and throny whip, that can be swung through enemies and blast them with explosions and flames.\n" +
                            "_-_ Gleaming Staff: gold-covered staff, that protects its user and can create lucky bags from hitting enemies.\n" +
                            "_-_ Fantasmal Stabber: tiny and sharp dagger, that stabs very quickly, pierces through armor and recharges magical equipment while doing so."));
            changes.addButton( new ChangeButton(new OofSprite(), "OOF Thief",
                    "Implemented a new thief-like enemy, _the OOF thief_:\n\n" +
                            "_-_ It spawns in Arena, or replaces any normal enemy with 1/2222 chance past cycle 0.\n" +
                            "_-_ It is 2.5x stronger than normal thieves.\n" +
                            "_-_ It attacks 3 times in a row and moves at 3x speed, but frequently stumbles while doing so.\n" +
                            "_-_ It attempts a steal every 4 hits and doesn't run away while successfully stealing an item.\n" +
                            "_-_ It can steal any unequipped item, including hero items and bags with all their content.\n" +
                            "_-_ After hitting its target 12 times, OOF thief will teleport away.\n" +
                            "_-_ Once its HP is brought to 0, it enters desperation mode, setting its HP to 1 and becoming invulnerable for 3 turns; while in desperation mode, OOF moves and attacks twice as fast.\n" +
                            "_-_ It doesn't drop any EXP, but drops big lucky bags with 50% chance.\n\n" +
                            "_-_ Stolen items can be recovered from shops for 5x of their normal price. Items stolen by OOF thieves have different text on buy button when seen in shops."
                    ));
            changes.addButton( new ChangeButton(new Image(Assets.Environment.TILES_ARENA,0, 0, 16, 16), "Arena changes",
                    "_-_ Now has its own tileset and music track.\n" +
                            "_-_ Reduced amount of gold arena enemies drop by 35%.\n" +
                            "_-_ OOF thieves can spawn on it."));
            changes.addButton( new ChangeButton(HeroSprite.avatar(HeroClass.RAT_KING, 6), "Rat King Bartering",
                    "_-_ The rewards from bartering will not be thrown if they cannot reach hero's position.\n" +
                            "_-_ Immovable characters can no longer bounce off items into Rat King.\n" +
                            "_-_ Fixed the crash with Rat King trying to send his rewards as he is leaving current level."));
            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                    "_-_ Removed Rat King's smaller XP requirement perk.\n" +
                            "_-_ Added lines for some NPCs, when they encounter Rat King.\n" +
                            "_-_ Added new \"cheesy cheest\" bag for artifacts and rings, that can be obtained after getting Cheese for first time.\n" +
                            "_-_ Added a music track for Black Mimic fight.\n" +
                            "_-_ Added a way to get rid of corpse dust.\n" +
                            "_-_ Changed feedback message because of recent events."
            ));
            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                    "_-_ Fixed the crash with Restored Nature perk attempting to access out-of-bounds areas.\n" +
                            "_-_ Fixed action indicator button not working properly for Champion and Monk subclasses and Preparation effect.\n" +
                            "_-_ Fixed Hunter getting Preparation after subclassing.\n" +
                            "_-_ Attempted to fix the crash with statues by marking Greatsword's knights as non-natural statues.\n" +
                            "_-_ Attempted to fix things that are still present after 2.15.4 (blerghhhhhhhhhhh)."
            ));

        changes = new ChangeInfo("ExpPD-2.15.4", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released October 10th, 2023\n" +
                        "_-_ 41 days after Experienced Pixel Dungeon 2.15.3"));
        changes.addButton( new ChangeButton(new ItemSprite(new CheeseChunk()), "Cheese and related things",
                "_-_ Nerfed Cheese's positive effects by 25%.\n" +
                        "_-_ Now has the same effect as Magnetic Meal.\n" +
                        "_-_ Added Cheese Chunks, that serve as trigger for Magnetic Meal-like effects, with weaker effects of Cheese.\n" +
                        "_-_ Cheese chunks can be obtained from Rat King leaving levels or by cooking Cheese with torches."));
        changes.addButton( new ChangeButton(HeroSprite.avatar(HeroClass.RAT_KING, 6), "Rat King Bartering",
                        "- The amount of items from bartering now scales with Dungeon cycles.\n" +
                        "- The amount of bartering needed for Cheese now scales with bartering's levelling.\n" +
                        "- The base amount of bartering to get Cheese has been reduced to 30.\n" +
                        "- The bartering levelling is now capped at 15."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                "_-_ Artifact Recharge boost from Bbat's being damaged is now capped at 50 for their entire HP bar.\n" +
                        "_-_ Tengu's shurikens are now unbreakable.\n" +
                        "_-_ Nerfed Rusty Shield: \n" +
                        "* its boost now decays 2x faster\n" +
                        "* reduced the amount of magic armor it gives by 2x\n" +
                        "_-_ Fishing Rods are now actually prioritized in reforging.\n" +
                        "_-_ Removed ghastly property of Rat King, due to it being unused."
        ));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                "_-_ Attempted to fix the crash from having too much luck when getting weapon drops from ghost and slime.\n" +
                        "_-_ Fixed the crash with Runic Blade's cooldown.\n" +
                        "_-_ Fixed Tengu's mask having missing string in higher Dungeon cycles."
        ));

        changes = new ChangeInfo("ExpPD-2.15.3", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 30th, 2023\n" +
                        "_-_ 23 days after Experienced Pixel Dungeon 2.15.2"));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                "_-_ Updated Android target version to API 33.\n" +
                        "_-_ Added ability to specify how many upgrades you want to use at once."
        ));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                "_-_ Fixed the exploit with rat king being able to give massive amount of loot if trapped.\n" +
                        "_-_ Reverted the buff to Magic Charge buff, which caused wands to be very overpowered.\n" +
                        "_-_ Actually fixed the inability to ascend from Black Mimic level.\n" +
                        "_-_ Actually fixed the Scroll of Determination hitting NPCs."
        ));
        changes = new ChangeInfo("ExpPD-2.15.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 7th, 2023\n" +
                        "_-_ 22 days after Experienced Pixel Dungeon 2.15.1"));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                "_-_ Nerfed Scroll of Determination's efficiency by 2x.\n" +
                        "_-_ Changed Arena's respawn rate to be both more manageable and less going overboard with time.\n" +
                        "_-_ Arena no longer applies Wrath of Excalibur to its spawns, preventing gaining massive amounts of EXP for free.\n" +
                        "_-_ Rusty Shield's magic protection now degrades every 2 turns instead of 10.\n" +
                        "_-_ Increased the damage of Electrical Explosive's by 4x.\n" +
                        "_-_ Black Mimic now attempts to move and attack at same speed as hero does.\n" +
                        "_-_ Black Mimic is now immune to Paralysis.\n" +
                        "_-_ Rewritten Black Fate Lock's dialogue to fix typos and misunderstandings.\n" +
                        "_-_ Ticket to Arena now mentions how to return from Arena."
        ));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                "_-_ Fixed regeneration always healing to max health.\n" +
                        "_-_ Fixed the crash with Guardian Knights having invalid weapon.\n" +
                        "_-_ Fixed the inability to start ascension challenge.\n" +
                        "_-_ Fixed the levels not resetting on going for new cycle.\n" +
                        "_-_ Fixed the ability to switch actions where it is unnecessary."
        ));

        changes = new ChangeInfo("ExpPD-2.15.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released July 16th, 2023\n" +
                        "_-_ 2 days after Experienced Pixel Dungeon 2.15"));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bugfixes",
                "_-_ Fixed Dwarf King phase 2 not progressing at all.\n" +
                        "_-_ Fixed the crash with weapons being replaced with Elixir of Luck in some cases.\n" +
                        "_-_ Fixed the crash with Runic Blade recharge effect trying to appear on dying.\n" +
                        "_-_ Fixed the crash with some of Disintegration usages.\n" +
                        "_-_ Fixed the crash with new Shattered weapons transmutation."
        ));

        changes = new ChangeInfo("ExpPD-2.15", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released July 14th, 2023\n" +
                        "_-_ 97 days after Experienced Pixel Dungeon 2.14.3\n" +
                        "_-_ 338 days after Experienced Pixel Dungeon 2.14\n" +
                        "\n" +
                        "This release ports everything up to Shattered 2.1.4 and fixes some stale bugs and balance issues."));
        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered ports",
                "Ported changes from Shattered 2.1.\n\n" +
                        "_-_ This includes new weapons, new rare variants, new testing mining level and Shopkeeper dialogue/buyback."));
        changes.addButton( new ChangeButton(new ItemSprite(new TicketToArena()), "Arena Changes",
                        "_-_ Now exists in its own subworld and has its own level icon.\n" +
                        "_-_ Increased ticket's base cost by 75%.\n" +
                        "_-_ Mob spawning speed increases with each mob killed.\n" +
                        "_-_ Mobs drop 50% less bonus gold."));
        changes.addButton( new ChangeButton(new ItemSprite(new Greatsword()), "Weapon changes",
                "_Greatsword:_\n" +
                        "_-_ Resprited.\n" +
                        "_-_ Stone knights now constantly lose 0.67% of their max HP per turn.\n" +
                        "\n" +
                        "_Runic Blade:_\n" +
                        "_-_ Blade's magic deals double damage.\n" +
                        "_-_ Decreased runic cooldown from 40 to 30.\n" +
                        "_-_ _Runic Slash_ ability no longer crashes the game, its enchantment boost is also increased from +175% to +225%."));
        changes.addButton( new ChangeButton(new Image(Assets.Sprites.MIMIC, 0, 48, 16, 16), "Black Mimic changes",
                "_-_ Now exists in its own subworld and has its own level icon.\n" +
                        "_-_ All Black Mimic's stats now scale with player's level and max HP, regardless of cycle. The only cycle change left for now is rewarded XP and 72% damage reduction during 4th cycle.\n" +
                        "_-_ Black Mimic's clones have their own sprite shading, no longer announce their attacks and have less chance to spawn another clone.\n" +
                        "_-_ Black Mimic's crushing rocks attack slam takes much less time as animation."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                "_-_ Recoded how Dwarf King's phase progressing and HP for cycles is handled, hopefully fixing softlock issues.\n" +
                        "_-_ Yog-Dzewa now can steal life both via healing off taken damage from player and minions retrieving some of their attack damage as lifesteal for Yog.\n" +
                        "_-_ Scroll of Determination's shielding no longer targets its host's allies and NPCs like shopkeepers.\n" +
                        "_-_ Reworked Ring of Might to give 2 HP per upgrade level instead of 0.25% of max HP per level.\n" +
                        "_-_ Increased the scaling in Scroll of Upgrade's drop XP requirement per cycle from 25 to 75.\n" +
                        "_-_ \"USE ALL UPGRADES\" option for Scroll of Upgrade now should cause less lag on using."
        ));

        changes = new ChangeInfo("ExpPD-2.14.3", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released April 09th, 2023\n" +
                        "_-_ 2 days after Experienced Pixel Dungeon 2.14.2"));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                "_-_ Fixed the crash for Wandmaker Cheese quest part.\n" +
                        "_-_ Fixed the crash for some levels with too much traps that there is no space for them.\n" +
                        "_-_ Fixed the crash with fishing hooks following player.\n" +
                        "_-_ Fixed the crash with some weapon transmutation."
        ));

        changes = new ChangeInfo("ExpPD-2.14.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released April 08th, 2023\n" +
                        "_-_ 8 days after Experienced Pixel Dungeon 2.14.1"));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                "_-_ Fixed the crash for Preparation buff.\n" +
                        "_-_ Fixed the crash for Rat King, after he is being selected after other hero classes.\n" +
                        "_-_ Giving Cheese to Wandmaker now takes out other items, related to his quest.\n" +
                        "_-_ Buffed bartering with Rat King.\n" +
                        "_-_ Added info about Scroll of Divination into guide."
        ));

        changes = new ChangeInfo("ExpPD-2.14.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released March 31st, 2023\n" +
                        "_-_ 7 days after Experienced Pixel Dungeon 2.14"));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                        "_-_ Fixed Throwing Mining Tool crashing the game when thrown out of map.\n" +
                                "_-_ Magnetic Meal now only picks up items from regular heaps.\n" +
                                "_-_ Fixed Wrath of Excalibur buff not granting bonus experience from enemies.\n" +
                                "_-_ Fixed Electrical Explosive inflicting Wrath of Excalibur on player's allies and player themselves.\n" +
                                "_-_ Fixed fishing rods not listing their full fishing power.\n" +
                                "_-_ Fixed misc crashes on Black Mimic boss.\n" +
                                "_-_ Fate Lock now can properly reset and respawn questgiver NPCs.\n" +
                                "_-_ Fixed Broken Seal not being reapplied on rat king's armor, when he gets class armor.\n" +
                                "_-_ Fixed crash with Mace if its beam cannot reach its target somehow.\n" +
                                "_-_ Fixed Overload buff not working on Arena monsters.\n" +
                                "_-_ Significantly optimized getting Scrolls of Upgrade from Fate Lock's grinding functionality."
                ));


        changes = new ChangeInfo("ExpPD-2.14", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released March 24th, 2023\n" +
                        "_-_ 50 days after Experienced Pixel Dungeon 2.11.4\n" +
                        "_-_ 225 days after Experienced Pixel Dungeon 2.11\n" +
                        "\n" +
                        "This release ports Shattered 2.0 and finally adds some new content into the game."));
        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered ports",
                "Ported changes from Shattered 2.0.\n\n" +
                        "_-_ This includes Duelist, Pickaxe rework, misc item buffs and improvements."));
        changes.addButton( new ChangeButton(new ItemSprite(new TicketToArena()), "Arena Changes",
                        "Overhauled arena to make it more exciting to play:\n\n"+
                        "_-_ Increased spawn rate from 400% to 500%.\n" +
                        "_-_ Added the mechanic of increasing mob's aggression and power with amount of monsters already spawned.\n" +
                        "_-_ Arena shop items are no longer actually consumed.\n" +
                        "_-_ Removed reduced view distance and EXP nerf."));
        changes.addButton( new ChangeButton(new Image(Assets.Interfaces.BUFFS_LARGE, 80, 32, 16, 16), "Perks",
                        "_-_ Added Ratforcements, Magnetic Meal, Friendly Bees and Restored Nature perks." ));
        changes.addButton( new ChangeButton(new ItemSprite(new TenguTreasureBag()), "Treasure Bag additions",
                "Added some new stuff into boss treasure bags on second cycle:\n\n" +
                        "_-_ _Electrical Explosive_ (Tengu) is reusable bomb that arcs over enemies and charges up, but accumulates the chance to break.\n" +
                        "_-_ _Throwing Mining Tool_ (DM-300) is special tool that can be thrown into walls or enemies to destroy them. It cannot pierce through walls.\n" +
                        "_-_ _Creature Summoning Beacon_ (Dwarf King) is lost technology that allows to increase enemy spawn rate and their limit to ludicrous numbers. Use with care."));
        changes.addButton( new ChangeButton(new ItemSprite(new Longsword()), "Weapon changes",
                        "_-_ Most of weapons changed by Experienced Redone also have their Duelist weapon ability changed to fit better with the mod.\n" +
                                "_-_ Longsword has been overhauled to damage several enemies at once, akin to Minecraft's sweeping edge swords; its Duelist ability was also changed to cast a cone of holy energy that inflicts special debuff, which increases the damage done to enemies and their experience drops.\n" +
                                "_-_ Mage's Staff gets rapier lunge as Duelist ability, for Rat King users."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.DUELIST, 1), HeroClass.DUELIST.title(),
                        "_-_ Duelist gets her exclusive perks, both for base class and subclasses.\n" +
                                "_-_ Rat King now can use weapon abilities and Champion and Monk powers."));
        changes.addButton( new ChangeButton(Icons.get(Icons.BUFFS), "Other balance tweaks",
                        "_-_ Blacksmith now requires a payment in gold to be usable. It scales with dungeon cycles.\n" +
                                "_-_ Nerfed Rat King's experience bar to 4(+4.5 per level).\n" +
                                "_-_ DM300's pylons damage now scales with Dungeon cycles.\n" +
                                "_-_ Item prices now scale with Dungeon cycles.\n" +
                                "_-_ Bee's attack speed now scales with Dungeon cycles.\n" +
                                "_-_ Dwarf King is now immune to being killed full health by Dirk's preparation.\n" +
                                "_-_ Monk's max energy is capped at 50."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                        "_-_ Updated the in-game guide with new information.\n" +
                                "_-_ Capped all wands that have scaled their on-hit particles with level at 1000 particles.\n" +
                                "_-_ Attempting to fix the issue with transmuting equipped artifacts and getting identical ones.\n" +
                                "_-_ Attempting to fix cloak of shadows' recharge at high levels.\n" +
                                "_-_ Fixed the issue with missile weapons causing a crash when being transmuted.\n" +
                                "_-_ Fixed the issue where DK would get stuck due to arithmetic issues with summoning phase.\n" +
                                "_-_ Decreased Dwarf King's HP in cycle 4 to 180 millions.\n" +
                                "_-_ Added the info about fishing rod's stats into description.\n" +
                                "_-_ Actually made 2.11.2's fishing rod reforge fix work.\n" +
                                "_-_ Fixed blacksmith not taking different kinds of items.\n" +
                                "_-_ Bbat now instantly crosses any distances when going back to hero.\n" +
                                "_-_ Attempt to fix the issue with ability to use certain subclass abilities as Rat King when shouldn't.\n" +
                                "_-_ Black mimic's level is considered a boss level.\n" +
                                "_-_ Fadeleaf now chooses true depth while teleporting warden.\n" +
                                "_-_ Elixir of Luck no longer can replace categories of items, given by questgivers.\n" +
                                "_-_ Increased the chance of Elixir of Luck's appearing to 0.67%."));

        changes = new ChangeInfo("ExpPD-2.11.4", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released February 02th, 2023\n" +
                        "_-_ 112 days after Experienced Pixel Dungeon 2.11.3\n" +
                        "\n" +
                        "This is first release in Google Play!"));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                "_-_ Fixed badges for Wand of Avalanche quest.\n" +
                        "_-_ Fixed rare crash when Elixir of Luck attempts to replace melee weapon in a shop.\n" +
                        "_-_ Fixed the rare crash with traps and Integer unboxing.\n" +
                        "_-_ Fixed Scrolls of Determination and Midas disappearing on reload.\n" +
                        "_-_ Added more actions into subclass action indicator recycling.\n" +
                        "_-_ Reworked Iron Will into Iron Reflection: now damages enemies with shockwaves when player is hurt.\n" +
                        "_-_ Determination Shield buff no longer gets used when player's HP is at full.\n" +
                        "_-_ Bigger Bags perk is now named in more clear way.\n" +
                        "_-_ Scroll of Dread is back and has fixed icon."));

        changes = new ChangeInfo("ExpPD-2.11.3", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released October 14th, 2022\n" +
                        "_-_ 29 days after Experienced Pixel Dungeon 2.11.2\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered ports",
                    "Ported changes from Shattered 1.4.0.\n\n" +
                            "_-_ This includes Arcana, new landscape hero select, sandals and berserker rework."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                "_-_ Level sizes are more varied.\n" +
                        "_-_ Significantly buffed scaling of most rings.\n" +
                        "_-_ Increased the base duration for fishing from 0-20 to 4-12.\n" +
                        "_-_ Arena mobs spawn 33% faster."));

        changes = new ChangeInfo("ExpPD-2.11.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released September 15th, 2022\n" +
                        "_-_ 3 days after Experienced Pixel Dungeon 2.11.1\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(Icons.get(Icons.CHALLENGE_COLOR), "Other Changes",
                "Tweaked some challenges:" +
                        "_-_ Food is only 10% as effective instead of 33% (On Diet)\n" +
                        "_-_ Mobs do not sleep and will not fight for you (Swarm Intelligence)\n" +
                        "_-_ SoUs no longer drop and farming them takes more exp (Forbidden Runes)"));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                "_-_ Fixed certain items causing stack overflow crash.\n" +
                        "_-_ Fishing rods get upgrades regardless of their position in blacksmith's window.\n" +
                        "_-_ Nerfed item drop perks for Rogue and Huntress.\n" +
                        "_-_ Fixed some of new scoring bugs."));

        changes = new ChangeInfo("ExpPD-2.11.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released September 12th, 2022\n" +
                        "_-_ 30 days after Experienced Pixel Dungeon 2.11\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
            "_-_ Fixed Black Mimic breaking itself on death.\n" +
                    "_-_ Fixed perks blocking the game if you get beyond level 166.\n" +
                    "_-_ Fixed dirk having ShPD values for its ability.\n" +
                    "_-_ Fixed preparation triggering from the cloak.\n" +
                    "_-_ Fixed collect-related code for items, which fixes phantom gold and other issues.\n" +
                    "_-_ Fixed pylon on Black Mimic level not outputting their gases.\n" +
                    "_-_ Fixed scroll of upgrade causing freezes at high levels.\n" +
                    "_-_ Fixed scroll of upgrade being pseudo-identified.\n" +
                    "_-_ Fixed Desktop interface not fully displaying the inventory.\n" +
                    "_-_ Fixed Rat King having 2 velvet pouches.\n" +
                    "_-_ Fixed experience and overload potions not working.\n" +
                    "_-_ Fixed Bounty Hunter perk not working.\n" +
                    "_-_ Fixed toolkit not being able to warm up on +11 and more.\n" +
                    "_-_ Fixed being able to actually descend into arena from Black Mimic level.\n" +
                    "_-_ Fixed avatar-related crash.\n" +
                    "_-_ Fixed amount of traps in cycles being too high sometimes.\n" +
                    "_-_ Greataxe can be thrown from quickslot and no longer hurts allies.\n" +
                    "_-_ Nerfed Whip's minimal damage."));

        changes = new ChangeInfo("ExpPD-2.11", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                    "_-_ Released August 11th, 2022\n" +
                            "_-_ 247 days after Experienced Pixel Dungeon 2.10.2\n" +
                            "_-_ 289 days after Experienced Pixel Dungeon 2.10\n" +
                            "\n" +
                            "Welcome to 2nd anniversary of ExpPD! This release implements many changes from Shattered done over this time period,"+
                            "bringing engine's version to 1.3.2. Enjoy all new features while making builds with new item reworks and class perks!"));
            changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered ports",
                    "Basically everything between 0.9.2 and 1.3.2.\n\n"+
                            "_-_ Armor abilities.\n" +
                            "_-_ Gladiator and Freerunner reworks.\n" +
                            "_-_ Alchemy rework.\n" +
                            "_-_ New music.\n" +
                            "_-_ New desktop UI.\n" +
                            "_-_ Liquid Metal and Arcane Resin.\n" +
                            "_-_ Hostile Champions and Badder Bosses challenges.\n" +
                            "_-_ Ankh overhaul.\n" +
                            "_-_ Exotic items reworks.\n" +
                            "_-_ New special rooms.\n" +
                            "_-_ Armband rework.\n" +
                            "_-_ Dreamfoil rework.\n" +
                            "_-_ Ascension challenge."
            ));
            changes.addButton( new ChangeButton(new Image(Assets.Interfaces.BUFFS_LARGE, 80, 32, 16, 16), "Perks",
                    "_-_ Reworked how frequently perks are gained to make them more accessible.\n" +
                            "_-_ Nerfed Iron Will to make you take less damage.\n" +
                            "_-_ Added Steel Will perk." ));

            changes.addButton( new ChangeButton(new ItemSprite(new Whip()), "Weapon changes",
                    "_-_ Overhauled Whip, Round Shield, Stone Gauntlet and Greatshield.\n" +
                            "_-_ Buffed Warhammer.\n" +
                            "_-_ Nerfed Sai.\n" +
                            "_-_ Nerfed all missile weapons, including Spirit Bow."));
            changes.addButton( new ChangeButton(HeroSprite.avatar(HeroClass.WARRIOR, 6), "Class tweaks",
                    "_-_ To make classes more appealing to play compared to Rat King, each one has many new perks to help them progress in dungeon.\n" +
                            "_-_ This also includes subclasses."));
            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other Changes",
                    "_-_ Fixed Ring of Elements's misleading description.\n" +
                            "_-_ Fixed treasure bags dropping higher tier weapons.\n" +
                            "_-_ Treasure bags no longer require turns to pick each item.\n" +
                            "_-_ Changed the coloring of buttons.\n" +
                            "_-_ Potion of Mastery buffs weapons and armor.\n" +
                            "_-_ Guide Entries now have a scroll bar.\n" +
                            "_-_ Changed score calculation and badges to fit Experienced's progression.\n" +
                            "_-_ You cannot fall into chasms on Black Mimic stage and arena.\n" +
                            "_-_ Added crash handling mechanism.\n" +
                            "_-_ Fixed crash for warhammer's attack."));



            changes = new ChangeInfo("ExpPD-2.10.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released December 8th, 2021\n" +
                        "_-_ 41 day after Experienced Pixel Dungeon 2.10.1\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Changes",
                "_-_ Fixed softlocks from chalice by overhauling regeneration.\n\n" +
                        "_-_ Rarity of items inside of categories is longer affected luck (should remove prevalence of 2 types of wands or rings).\n\n" +
                        "_-_ Fixed rings giving much less benefits than shown in their description.\n\n" +
                        "_-_ Fixed RK's mirror images.\n\n" +
                        "_-_ Added an icon for older Android versions."));
        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered ports",
                "_-_ New level gen algorithm.\n" +
                        "_-_ Added HP number to health bar.\n" +
                        "_-_ Quick-use UI.\n" +
                        "_-_ Items with cone AoEs can trickshot.\n" +
                        "_-_ Web can burn and wands can shoot through web.\n" +
                        "_-_ Overhauled On Diet, Faith is my Armor and Pharmo."
        ));

        changes = new ChangeInfo("ExpPD-2.10.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released October 28th, 2021\n" +
                        "_-_ 1 day after Experienced Pixel Dungeon 2.10\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Changes",
                "_-_ Fixed the crash for rat king beating Tengu."));
        changes = new ChangeInfo("ExpPD-2.10", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released October 27th, 2021\n" +
                        "_-_ 53 days after Experienced Pixel Dungeon 2.9.4\n" +
                        "_-_ 361 day after Experienced Pixel Dungeon 2.9\n\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(HeroSprite.avatar(HeroClass.RAT_KING, 6), "New class!",
                "His Ratiness joins the battle for greed and loot, with his signature ability being everything he wants to be and faster leveling from RKD!"));
        changes.addButton( new ChangeButton(new Image(Assets.Interfaces.BUFFS_LARGE, 80, 32, 16, 16), "Perks",
                "Each 10th level you will get a random neat bonus, called Perk. Right now there is 12 of them." ));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Changes",
                "_-_ Fixed certain issues with infinity turns.\n\n" +
                        "_-_ Levels are bigger at higher cycles.\n\n" +
                        "_-_ Artifacts are no longer enforced to be unique.\n\n" +
                        "_-_ Massively buffed most of melee weapons.\n\n" +
                        "_-_ Greataxe uses one turn regardless of targets.\n\n" +
                        "_-_ Arena mobs give 50% XP instead of 0.\n\n" +
                        "_-_ Fishing no longer uses luck and no longer exclusively uses purple tier at higher levels."));

            changes = new ChangeInfo("ExpPD-2.9.4", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                    "_-_ Released September 4th, 2021\n" +
                            "_-_ 17 days after Experienced Pixel Dungeon 2.9.3\n" +
                            "\n" +
                            "Dev commentary will be added here in the future."));
            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Changes",
                    "_-_ Fixed certain issues with arena ticket.\n\n" +
                            "_-_ Bbat upgrades when being dead.\n\n" +
                            "_-_ Overpopulation spell can scale much further.\n\n" +
                            "_-_ Wands scale their damage with hero's level.\n\n" +
                            "_-_ Added cycle indicator."));

        changes = new ChangeInfo("ExpPD-2.9.3", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 18th, 2021\n" +
                        "_-_ 223 days after Experienced Pixel Dungeon 2.9.2\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Changes",
                "_-_ Fixed bugs with Scroll of Transmutation.\n\n" +
                        "_-_ Fixed bug with Preparation bugging out bosses.\n\n" +
                        "_-_ Fixed certain missiles having lower damage than intended.\n\n" +
                        "_-_ Fixed Scroll of Upgrade showing the upgrade option even if you didn't identify it.\n\n" +
                        "_-_ Fixed Scroll of Affection making bosses allied.\n\n" +
                        "_-_ Fixed Wealth effect triggering on opening containers.\n\n" +
                        "_-_ Hugely buffed Black Mimic's stats, fixed his clones counting as boss defeat.\n\n" +
                        "_-_ Scroll of Determination lasts two times longer.\n\n" +
                        "_-_ Fixed certain artifacts not recharging at all on high upgrade levels.\n\n" +
                        "_-_ Significantly buffed fishing: you need to wait less and rare drops are more frequent.\n\n" +
                        "_-_ Alchemy pots give more energy on higher dungeon cycles."));

            changes = new ChangeInfo("ExpPD-2.9.2", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                    "_-_ Released January 7th, 2021\n" +
                            "_-_ 4 days after Experienced Pixel Dungeon 2.9.1\n" +
                            "\n" +
                            "Dev commentary will be added here in the future."));
            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Changes",
                    "_-_ Fixed bug with Ring of Tenacity.\n\n" +
                            "_-_ Fixed bug with Mace not shooting.\n\n" +
                            "_-_ Every throwing weapon has double damage. Even Spirit Bow. Yeth.\n\n" +
                            "_-_ Dirk now accumulates Preparation. No, you can't confess in love to rat and kill yourself with it.\n\n" +
                            "_-_ Leather Gloves now temporarily get more power, if you deal 0 damage."));

            changes = new ChangeInfo("ExpPD-2.9.1", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                    "_-_ Released January 3th, 2021\n" +
                            "_-_ 64 days after Experienced Pixel Dungeon 2.9\n" +
                            "\n" +
                            "Dev commentary will be added here in the future."));
            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other",
                    "_-_ Fixed bug with shopkeeper not giving key.\n\n" +
                            "_-_ Clayball is sold in each store after key of truth.\n\n" +
                            "_-_ Grinding is now enabled by default."));

            changes = new ChangeInfo("ExpPD-2.9", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                    "_-_ Released October 31th, 2020\n" +
                            "_-_ 47 days after Experienced Pixel Dungeon 2.8.1\n" +
                            "\n" +
                            "Dev commentary will be added here in the future."));

            changes.addButton( new ChangeButton(new ItemSprite(new WandOfEarthblast()), "Brand new wand!",
                    "Added the legendary overpowered Wand of Avalanche. To find it, you need to complete big backtracking quest with 6 parts!\n\n" +
                            "To begin, get cheese from Rat King."));

            changes.addButton( new ChangeButton(new ItemSprite(new Cheese()), "WoA quest content",
                    "Added 6 items, used in Wand of Avalanche quest, 5 of which are usable as equipment or consumable."));

            changes.addButton( new ChangeButton(new ItemSprite(new ElixirOfMight()), "Luck mechanic",
                    "Added incrementable luck. You can drink elixirs of luck to improve your chances to attack and dodge, item rarity, damage rolls and many other things.\n\n" +
                            "_-_ Elixir of Luck can replace any random item with 0.5% chance or be brewed from Strength potion."));

            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other",
                    "_-_ Buffed Grim's damage and Greatsword's knights HP.\n\n" +
                            "_-_ Added little animation quirk with fast weapons."));

            changes.addButton( new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                    "Fixed:\n" +
                            "_-_ Chalice of Blood being broken beyond +30.\n"+
                                "_-_ Potential transmutation crashes."));

	        changes = new ChangeInfo("ExpPD-2.8.1", true, "");
            changes.hardlight(Window.TITLE_COLOR);
            changeInfos.add(changes);
            changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                    "_-_ Released September 13th, 2020\n" +
                            "_-_ 16 days after Experienced Pixel Dungeon 2.8\n" +
                            "\n" +
                            "Dev commentary will be added here in the future."));
            changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other",
                    "_-_ Fixed tenacity and might.\n\n" +
                            "_-_ Fixed beacons of returning breaking time and space.\n\n" +
                            "_-_ Fixed bags on arena and arena's ticket price."));

        changes = new ChangeInfo("ExpPD-2.8", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 28th, 2020\n" +
                        "_-_ 12 days after Experienced Pixel Dungeon 2.7.1\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(new ItemSprite(new GambleBag()), "Treasure Bags",
                "Fixed the crash, when item from backpack is null."));
        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.RING_RUBY, null), "Rings",
                "Refactored multiplier code to match vanilla's behaviour."));
        changes.addButton( new ChangeButton(new ItemSprite(new Greataxe()), "Greataxe",
                "Greataxe now properly hits several enemies."));
        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.MACE, null), "Overhauled Weapons",
                "_-_ Mace shoots laser beams on attacking.\n\n" +
                        "_-_ Warhammer is very powerful, but very slow."));
        changes.addButton( new ChangeButton(new ItemSprite(new TicketToArena()), "Arena",
                "Replaced passive exp generation with arena.\n\n" +
                        "_-_You can buy one-time tickets in any shops.\n\n" +
                        "_-_ You can meet a wide variety of mobs, which have no exp, but drop gold and valueable items.\n\n" +
                        "_-_ There is shop with bags to buy.\n\n\n" +
                        "Experience Gens in inventory will be replaced by tickets, generators in world will be replaced by sheeps."));

        changes = new ChangeInfo("ExpPD-2.7.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 16th, 2020\n" +
                        "_-_ 3 hours after Experienced Pixel Dungeon 2.7\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other",
                "_-_ Fixed rings having effects when they shouldn't.\n\n" +
                        "_-_ Fixed power plants being not functional on +0.\n\n" +
                        "_-_ Fixed new bags having incorrect icons."));

        changes = new ChangeInfo("ExpPD-2.7", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 16th, 2020\n" +
                        "_-_ 0 days after Experienced Pixel Dungeon 2.6\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(new ItemSprite(new GambleBag()), "Treasure Bags",
                "_-_ Boss treasure bags contain more money.\n\n" +
                        "_-_ Lucky bag contains more items and have reduced price.\n\n" +
                        "_-_ Alchemy bag have more items and increased price.\n\n" +
                        "_-_ Added big lucky bag and elite bag."));
        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.RING_RUBY, null), "Rings",
                "Ring's scaling is reworked to be linear."));
        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.EXOTIC_ODAL, null), "Scrolls",
                "_-_ Scroll of Confusion is now _Scroll of Determination_ that applies life-stealing shield to user.\n\n" +
                        "_-_ Scroll of Polymorph is now _Scroll of Midas_, that turns every visible enemy into money."));
        changes.addButton( new ChangeButton(new ItemSprite(new Vampirism()), "Spells",
                "Overpopulation and Flame Empower are consumed on using."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other",
                "_-_ Resetting floor resets RNG.\n\n" +
                        "_-_ Power Plants are getting upgraded instead of being stacked.\n\n" +
                        "_-_ Tridents now drop some gold on damaging enemies."));

         changes = new ChangeInfo("ExpPD-2.6", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 16th, 2020\n" +
                        "_-_ 3 days after Experienced Pixel Dungeon 2.5.1\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(new ItemSprite(new GambleBag()), "Treasure Bags",
                "Added treasure bags.\n\n" +
                        "_-_ Once boss is defeated once, on next fights it will drop treasure bag with unique drops, loads of money and unique items for dungeon cycles.\n\n" +
                        "_-_ You can find Gamble Bag in shops and you can catch alchemy items bag with fishing rod."));
        changes.addButton( new ChangeButton(new ItemSprite(new Vampirism()), "Spells",
                "Added three new spells: Vampirism, Overpopulation and Fire Empower. More info in in-game guide."));
        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.RING_RUBY, null), "Rings",
                "Ring's scaling is extremely nerfed again to somewhat negate their power creep. (why) (I hate furor)"));
        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.BASIC_HOOK, null), "Fishing",
                "Reduced upgrades on fished items."));

        changes = new ChangeInfo("ExpPD-2.5.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 13th, 2020\n" +
                        "_-_ 0 days after Experienced Pixel Dungeon 2.5\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other",
                "_-_ Increased amount of possible Marked debuffs.\n\n" +
                        "_-_ Fixed crash when power plants are attacked by magic.\n\n" +
                        "_-_ Power Plants require to be accessible by mobs to work."));

        changes = new ChangeInfo("ExpPD-2.5", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 13th, 2020\n" +
                        "_-_ 1 days after Experienced Pixel Dungeon 2.4.1\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));


        changes.addButton( new ChangeButton(new Image(Assets.Sprites.BBAT, 0, 0, 15, 15), "Bbat",
                "_-_ Fixed crash on bbat's death." ));

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.ROGUE, 0, 90, 12, 15), "Hunter subclass",
                "Replaced Assassin with Hunter.\n\n" +
                        "_-_ Instead of waiting in shadows, Hunter need to aggro bbat into enemies to mark them.\n\n" +
                        "_-_ Marking enemies increases bbat's damage to them and allows Hunter to teleport and inflict debuffs." ));

        changes.addButton( new ChangeButton(new ItemSprite(new ExpGenerator()), "EXP Generator",
                "_-_ Reverted satiety and sleep consumption, but you need to defend power plants from mobs.\n\n" +
                        "_-_ If monster manages to kill plant, monster will gain a huge amount of armor and attack speed.\n\n" +
                        "_-_ Power Plants attract mobs in 32x32 sphere."));

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other",
                "_-_ Adjusted amount of Yog's lasers immensely.\n\n" +
                        "_-_ Significantly increased inventory size and removed hidden slots.\n\n" +
                        "_-_ Added cycle 4 Yog laser damage."));



        changes = new ChangeInfo("ExpPD-2.4.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 12th, 2020\n" +
                        "_-_ 1 days after Experienced Pixel Dungeon 2.4\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.BBAT, 0, 0, 15, 15), "Bbat",
                "Added bbat minion to rogue to decrease early-game difficulty." ));

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other",
                "_-_ Greatsword no longer spawns knights, when owned by statue.\n\n" +
                        "_-_ Kunai have text for buff and actually works.\n\n" +
                        "_-_ Armored Statue doesn't crash with tiered armor.\n\n" +
                        "_-_ Guardian Trap no longer depends on escalating depth.\n\n" +
                        "_-_ Cycles no longer preserve between runs (will not affect runs before this update).\n\n" +
                        "_-_ Hunger can damage you on locked floor when drained by power plants.\n\n" +
                        "_-_ Honeypot cannot be obtained from fishing."));

        changes = new ChangeInfo("ExpPD-2.4", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 11th, 2020\n" +
                        "_-_ 1 days after Experienced Pixel Dungeon 2.3\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));

        changes.addButton( new ChangeButton(new ItemSprite(new ExpGenerator()), "EXP Generator",
                "Fixed interruptions when unneeded."));

        changes.addButton( new ChangeButton(new ItemSprite(new VelvetPouch()), "Inventory",
                "Adjusted inventory size."));

        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.GREATSWORD, null), "Overhauled Weapons",
                "Added new abilities for some underused weapons and items. For more details, visit In-Game Guide."));

        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.SWORD, new ItemSprite.Glowing( 0x000000 )), "Grim",
                "Significantly nerfed Grim: now it does additional damage instead of instakill."));

        changes.addButton( new ChangeButton(new ItemSprite(new BlackPsycheChest()), "Dungeon Cycles",
                "You can enter fourth dungeon cycle."));

        changes.addButton( new ChangeButton(new ItemSprite(new ScrollOfSirensSong()), "Scroll of Affection",
                "Scroll of Affection turns enemies into allies instead of charming."));

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other",
                "_-_ Corruption is affected by Bless buff.\n\n" +
                        "_-_ Shield weapons now grow with dungeon's cycles.\n\n" +
                        "_-_ Significantly reduced amount of particles for some wand, to reduce lag.\n\n" +
                        "_-_ Added new fishing rod for new dungeon cycle.\n\n" +
                        "_-_ Various small adjustments to UI.\n\n" +
                        "_-_ Fishing is fully fixed, for real."));

        changes = new ChangeInfo("ExpPD-2.3", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 10th, 2020\n" +
                        "_-_ 4 days after Experienced Pixel Dungeon 2.2\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton(Icons.get(Icons.NEWS), "Guide",
                "Added in-game guide, which you can access in title menu."));
        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Shattered 0.8.2 changes",
                "_-_ New title screen.\n" +
                        "_-_ New settings window.\n" +
                        "_-_ 3rd accessory (misc) slot.\n" +
                        "_-_ Questgivers are added to journal, when hero firstly sees them.\n" +
                        "_-_ Disintegration's range benefits from magic charge.\n" +
                        "_-_ Artifacts benefit from Ring of Energy.\n" +
                        "_-_ Knockback effects closing door only on melee attacks.\n\n" +
                        "Fixes:\n" +
                        "_-_ Tengu's freezing the game when jumping.\n" +
                        "_-_ Boss drop enemies softlocking the game on death.\n"+
                        "_-_ Bees refusing to retarget when target is invulnerable\n" +
                        "_-_ Potion splashes cleaning fire/ooze from enemies.\n" +
                        "_-_ Blast wave attempting to throw enemies that move after being damaged.\n" +
                        "_-_ Corrupting enchant attempting to corrupt dead enemies.\n" +
                        "_-_ Magic missile charge buff not visually applying to the mage's staff.\n" +
                        "_-_ Specific cases where using an item wouldn't cancel the current cell selector.\n" +
                        "_-_ Large enemies rarely appearing in enclosed spaces due to levelgen.\n" +
                        "_-_ Lotus vfx persisting after the sprite is destroyed in rare case.\n"
                        ));

        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.RING_RUBY, null), "Rings",
                "Ring's scaling is extremely nerfed to somewhat negate their power creep."));

        changes.addButton( new ChangeButton(new ItemSprite(new ExpGenerator()), "EXP Generator",
                "Exp generators now drain your satiety and interrupt your sleep when possible."));

        changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), "Other",
                "_-_ Number of traps, items and mobs now is much lower on dungeon's cycles.\n\n" +
                        "_-_ Fishing is fully fixed, also drops food."));


        changes = new ChangeInfo("ExpPD-2.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.get(Icons.BOBBY_IS_VERY_STRANGE_PERSON_BECAUSE_HE_TRIES_TO_REFERENCE_HIMSELF_IN_NEW_SHATTERED_CREDITS_SCREEN), "Developer Commentary",
                "_-_ Released August 6th, 2020\n" +
                        "_-_ 2 days after Experienced Pixel Dungeon 2.1\n" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.SCROLL_TIWAZ, null), new ScrollOfUpgrade().trueName(),
                "The whole stack of SoU can be applied at once with special action."));
        changes.addButton( new ChangeButton( new ItemSprite(ItemSpriteSheet.EXOTIC_CRIMSON, null), new PotionOfMastery().trueName(),
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
