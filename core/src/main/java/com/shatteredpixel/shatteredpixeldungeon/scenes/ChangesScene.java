/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.Scene;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class ChangesScene extends PixelScene {
	
	public static int changesSelected = 4;

	private NinePatch rightPanel;
	private IconTitle changeTitle;
	private RenderedTextBlock changeBody;
	
	@Override
	public void create() {
		super.create();

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
				new float[]{1, 1},
				false);

		int w = Camera.main.width;
		int h = Camera.main.height;

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 9 );
		title.hardlight(Window.TITLE_COLOR);
		title.setPos(
				(w - title.width()) / 2f,
				(20 - title.height()) / 2f
		);
		align(title);
		add(title);

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		NinePatch panel = Chrome.get(Chrome.Type.TOAST);

		int pw = 135 + panel.marginLeft() + panel.marginRight() - 2;
		int ph = h - 36;

		if (h >= PixelScene.MIN_HEIGHT_FULL && w >= PixelScene.MIN_WIDTH_FULL) {
			panel.size( pw, ph );
			panel.x = (w - pw) / 2f - pw/2 - 1;
			panel.y = title.bottom() + 5;

			rightPanel = Chrome.get(Chrome.Type.TOAST);
			rightPanel.size( pw, ph );
			rightPanel.x = (w - pw) / 2f + pw/2 + 1;
			rightPanel.y = title.bottom() + 5;
			add(rightPanel);

			changeTitle = new IconTitle(Icons.get(Icons.CHANGES), Messages.get(this, "right_title"));
			changeTitle.setPos(rightPanel.x + rightPanel.marginLeft(), rightPanel.y + rightPanel.marginTop());
			changeTitle.setSize(pw, 20);
			add(changeTitle);

			String body = Messages.get(this, "right_body");
			if (Messages.lang() != Languages.ENGLISH){
				body += "\n\n_" + Messages.get(this, "lang_warn") + "_";
			}
			changeBody = PixelScene.renderTextBlock(body, 6);
			changeBody.maxWidth(pw - panel.marginHor());
			changeBody.setPos(rightPanel.x + rightPanel.marginLeft(), changeTitle.bottom()+2);
			add(changeBody);

		} else {
			panel.size( pw, ph );
			panel.x = (w - pw) / 2f;
			panel.y = title.bottom() + 5;
		}
		align( panel );
		add( panel );
		
		final ArrayList<ChangeInfo> changeInfos = new ArrayList<>();

		switch (changesSelected){
			case 0: default:
				v1_X_Changes.addAllChanges(changeInfos);
				break;
			case 1:
				v0_9_X_Changes.addAllChanges(changeInfos);
				break;
			case 2:
				v0_8_X_Changes.addAllChanges(changeInfos);
				break;
			case 3:
				v0_7_X_Changes.addAllChanges(changeInfos);
				break;
			case 5:
				v0_5_X_Changes.addAllChanges(changeInfos);
				v0_4_X_Changes.addAllChanges(changeInfos);
				v0_3_X_Changes.addAllChanges(changeInfos);
				v0_2_X_Changes.addAllChanges(changeInfos);
				v0_1_X_Changes.addAllChanges(changeInfos);
				break;
            case 4:
                ExpPDChanges.addAllChanges(changeInfos);
                break;

		}

		ScrollPane list = new ScrollPane( new Component() ){

			@Override
			public void onClick(float x, float y) {
				for (ChangeInfo info : changeInfos){
					if (info.onClick( x, y )){
						return;
					}
				}
			}

		};
		add( list );

		Component content = list.content();
		content.clear();

		float posY = 0;
		float nextPosY = 0;
		boolean second = false;
		for (ChangeInfo info : changeInfos){
			if (info.major) {
				posY = nextPosY;
				second = false;
				info.setRect(0, posY, panel.innerWidth(), 0);
				content.add(info);
				posY = nextPosY = info.bottom();
			} else {
				if (!second){
					second = true;
					info.setRect(0, posY, panel.innerWidth()/2f, 0);
					content.add(info);
					nextPosY = info.bottom();
				} else {
					second = false;
					info.setRect(panel.innerWidth()/2f, posY, panel.innerWidth()/2f, 0);
					content.add(info);
					nextPosY = Math.max(info.bottom(), nextPosY);
					posY = nextPosY;
				}
			}
		}

		content.setSize( panel.innerWidth(), (int)Math.ceil(posY) );

		list.setRect(
				panel.x + panel.marginLeft(),
				panel.y + panel.marginTop() - 1,
				panel.innerWidth() + 2,
				panel.innerHeight() + 2);
		list.scrollTo(0, 0);

        RedButton btnEXP = new RedButton("ExpPD"){
            @Override
            protected void onClick() {
                super.onClick();
                if (changesSelected != 4) {
                    changesSelected = 4;
                    ShatteredPixelDungeon.seamlessResetScene();
                }
            }
        };
        if (changesSelected == 4) btnEXP.textColor(Window.TITLE_COLOR);
        btnEXP.setRect(list.left()-4f, list.bottom()+5, 27, 14);
        add(btnEXP);

		RedButton btn0_8 = new RedButton("v0.8"){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 0) {
					changesSelected = 0;
					ShatteredPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected == 0) btn0_8.textColor(Window.TITLE_COLOR);
		btn0_8.setRect(btnEXP.right() + 1, btnEXP.top(), 27, 14);
		add(btn0_8);
		
		RedButton btn0_7 = new RedButton("v0.7"){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 1) {
					changesSelected = 1;
					ShatteredPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected == 1) btn0_7.textColor(Window.TITLE_COLOR);
		btn0_7.setRect(btn0_8.right() + 1, btn0_8.top(), 27, 14);
		add(btn0_7);
		
		RedButton btn0_6 = new RedButton("v0.6"){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 2) {
					changesSelected = 2;
					ShatteredPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected == 2) btn0_6.textColor(Window.TITLE_COLOR);
		btn0_6.setRect(btn0_7.right() + 1, btn0_8.top(), 27, 14);
		add(btn0_6);

		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );

		fadeIn();
	}

	private void updateChangesText(Image icon, String title, String message){
		if (changeTitle != null){
			changeTitle.icon(icon);
			changeTitle.label(title);
			changeTitle.setPos(changeTitle.left(), changeTitle.top());

			int pw = 135 + rightPanel.marginHor() - 2;
			changeBody.text(message, pw - rightPanel.marginHor());
			while (changeBody.height() > rightPanel.height()-25
					&& changeBody.right() + 5 < Camera.main.width){
				changeBody.maxWidth(changeBody.maxWidth()+5);
			}
			int ph = Camera.main.height - 36;
			rightPanel.size(changeBody.maxWidth() + rightPanel.marginHor(), Math.max(ph, changeBody.height()+18+rightPanel.marginVer()));
			changeBody.setPos(changeBody.left(), changeTitle.bottom()+2);

		} else {
			addToFront(new ChangesWindow(icon, title, message));
		}
	}

	public static void showChangeInfo(Image icon, String title, String message){
		Scene s = ShatteredPixelDungeon.scene();
		if (s instanceof ChangesScene){
			((ChangesScene) s).updateChangesText(icon, title, message);
			return;
		}
		s.addToFront(new ChangesWindow(icon, title, message));
	}

	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchNoFade(TitleScene.class);
	}

}
