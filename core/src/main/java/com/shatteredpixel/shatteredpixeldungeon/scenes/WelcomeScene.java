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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.watabou.utils.FileUtils;

public class WelcomeScene extends PixelScene {

	private static int LATEST_UPDATE = ShatteredPixelDungeon.v0_8_1;

	@Override
	public void create() {
		super.create();

		final int previousVersion = SPDSettings.version();
			ShatteredPixelDungeon.switchNoFade(TitleScene.class);

	}

	private void updateVersion(int previousVersion){
		
		//update rankings, to update any data which may be outdated
		if (previousVersion < LATEST_UPDATE){
			try {
				Rankings.INSTANCE.load();
				for (Rankings.Record rec : Rankings.INSTANCE.records.toArray(new Rankings.Record[0])){
					try {
						Rankings.INSTANCE.loadGameData(rec);
						Rankings.INSTANCE.saveGameData(rec);
					} catch (Exception e) {
						//if we encounter a fatal per-record error, then clear that record
						Rankings.INSTANCE.records.remove(rec);
						ShatteredPixelDungeon.reportException(e);
					}
				}
				Rankings.INSTANCE.save();
			} catch (Exception e) {
				//if we encounter a fatal error, then just clear the rankings
				FileUtils.deleteFile( Rankings.RANKINGS_FILE );
				ShatteredPixelDungeon.reportException(e);
			}
		}
		
		//give classes to people with saves that have previously unlocked them
		if (previousVersion <= ShatteredPixelDungeon.v0_7_0c){
			Badges.loadGlobal();
			Badges.addGlobal(Badges.Badge.UNLOCK_MAGE);
			Badges.addGlobal(Badges.Badge.UNLOCK_ROGUE);
			if (Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_3)){
				Badges.addGlobal(Badges.Badge.UNLOCK_HUNTRESS);
			}
			Badges.saveGlobal();
		}
		
		if (previousVersion <= ShatteredPixelDungeon.v0_6_5c){
			Journal.loadGlobal();
			Document.ALCHEMY_GUIDE.addPage("Potions");
			Document.ALCHEMY_GUIDE.addPage("Stones");
			Document.ALCHEMY_GUIDE.addPage("Energy_Food");
			Journal.saveGlobal();
		}
		
		SPDSettings.version(ShatteredPixelDungeon.versionCode);
	}
	
}
