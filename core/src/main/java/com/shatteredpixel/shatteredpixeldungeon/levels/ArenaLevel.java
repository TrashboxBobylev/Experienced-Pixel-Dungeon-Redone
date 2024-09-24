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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Overload;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RageShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MobSpawner;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.BiggerGambleBag;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.GambleBag;
import com.shatteredpixel.shatteredpixeldungeon.items.treasurebags.QualityBag;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ArenaShopLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;

public class ArenaLevel extends Level {
	
	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.ARENA, true);
	}

	private static final int WIDTH = 32;
	private static final int HEIGHT = 32;

	private static final int ROOM_LEFT		= WIDTH / 2 - 3;
	private static final int ROOM_RIGHT		= WIDTH / 2 + 1;
	private static final int ROOM_TOP		= HEIGHT / 2 - 2;
	private static final int ROOM_BOTTOM	= HEIGHT / 2 + 2;
	
	private int arenaDoor;
	private boolean enteredArena = false;
	private boolean keyDropped = false;
	private ArenaShopLevel shop;
	
	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_ARENA;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_ARENA;
	}
	
	private static final String DOOR	= "door";
	private static final String ENTERED	= "entered";
	private static final String DROPPED	= "droppped";
	private static final String SHOP = "shop";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( DOOR, arenaDoor );
		bundle.put( ENTERED, enteredArena );
		bundle.put( DROPPED, keyDropped );
		bundle.put( SHOP, shop);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		arenaDoor = bundle.getInt( DOOR );
		enteredArena = bundle.getBoolean( ENTERED );
		keyDropped = bundle.getBoolean( DROPPED );
		shop = (ArenaShopLevel) bundle.get(SHOP);
	}
	
	@Override
	protected boolean build() {
		
		setSize(WIDTH, HEIGHT);

		Rect space = new Rect();

		space.set(
				Random.IntRange(2, 6),
				Random.IntRange(2, 6),
				Random.IntRange(width-6, width-2),
				Random.IntRange(height-6, height-2)
		);

		Painter.fillEllipse( this, space, Terrain.EMPTY );

		exit = space.left + space.width()/2 + (space.top - 1) * width();
		
		map[exit] = Terrain.WALL_DECO;

		Painter.fill( this, ROOM_LEFT - 1, ROOM_TOP - 1,
			ROOM_RIGHT - ROOM_LEFT + 3, ROOM_BOTTOM - ROOM_TOP + 3, Terrain.WALL );
		Painter.fill( this, ROOM_LEFT, ROOM_TOP + 1,
			ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP, Terrain.EMPTY );

		Painter.fill( this, ROOM_LEFT, ROOM_TOP,
			ROOM_RIGHT - ROOM_LEFT + 1, 1, Terrain.EMPTY_DECO );
		shop = new ArenaShopLevel();
		shop.set(ROOM_LEFT-1, ROOM_TOP-1, ROOM_RIGHT+1, ROOM_BOTTOM+1);
		
		arenaDoor = Random.Int( ROOM_LEFT, ROOM_RIGHT ) + (ROOM_BOTTOM + 1) * width();
		map[arenaDoor] = Terrain.DOOR;
		
		int entrance = Random.Int( ROOM_LEFT + 1, ROOM_RIGHT - 1 ) +
			Random.Int( ROOM_TOP + 1, ROOM_BOTTOM - 1 ) * width();
		transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
		map[entrance] = Terrain.PEDESTAL;

		{
			itemsToSpawn = new ArrayList<>();
			for (int i = 0; i < 6; i++) itemsToSpawn.add(new GambleBag());
			for (int i = 0; i < 6; i++) itemsToSpawn.add(new BiggerGambleBag());
			for (int i = 0; i < 6; i++) itemsToSpawn.add(new QualityBag());

			Point itemPlacement = new Point(cellToPoint(arenaDoor));
			if (itemPlacement.y == ROOM_TOP-1){
				itemPlacement.y++;
			} else if (itemPlacement.y == ROOM_BOTTOM+1) {
				itemPlacement.y--;
			} else if (itemPlacement.x == ROOM_LEFT-1){
				itemPlacement.x++;
			} else {
				itemPlacement.x--;
			}

			for (Item item : itemsToSpawn) {

				if (itemPlacement.x == ROOM_LEFT && itemPlacement.y != ROOM_TOP){
					itemPlacement.y--;
				} else if (itemPlacement.y == ROOM_TOP && itemPlacement.x != ROOM_RIGHT){
					itemPlacement.x++;
				} else if (itemPlacement.x == ROOM_RIGHT && itemPlacement.y != ROOM_BOTTOM){
					itemPlacement.y++;
				} else {
					itemPlacement.x--;
				}

				int cell = pointToCell(itemPlacement);

				if (heaps.get( cell ) != null) {
					do {
						cell = pointToCell(new Point( Random.IntRange( ROOM_TOP, ROOM_RIGHT ),
								Random.IntRange( ROOM_TOP, ROOM_BOTTOM )));
					} while (heaps.get( cell ) != null || findMob( cell ) != null);
				}

				drop( item, cell ).type = Heap.Type.FOR_ARENA_SALE;
			}
		}
		
		boolean[] patch = Patch.generate( width, height, 0.30f, 6, true );
		for (int i=0; i < length(); i++) {
			if (map[i] == Terrain.EMPTY && patch[i]) {
				map[i] = Terrain.WATER;
			}
		}

		for (int i=0; i < length(); i++) {
			if (map[i] == Terrain.EMPTY && Random.Int( 6 ) == 0) {
				map[i] = Terrain.INACTIVE_TRAP;
				Trap t = new SummoningTrap().reveal();
				t.active = false;
				setTrap(t, i);
			}
		}
		
		for (int i=width() + 1; i < length() - width(); i++) {
			if (map[i] == Terrain.EMPTY) {
				int n = 0;
				if (map[i+1] == Terrain.WALL) {
					n++;
				}
				if (map[i-1] == Terrain.WALL) {
					n++;
				}
				if (map[i+width()] == Terrain.WALL) {
					n++;
				}
				if (map[i-width()] == Terrain.WALL) {
					n++;
				}
				if (Random.Int( 8 ) <= n) {
					map[i] = Terrain.EMPTY_DECO;
				}
			}
		}
		
		for (int i=0; i < length() - width(); i++) {
			if (map[i] == Terrain.WALL
					&& DungeonTileSheet.floorTile(map[i + width()])
					&& Random.Int( 3 ) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}

		
		return true;
	}
	
	@Override
	protected void createMobs() {
	}

	public static class ArenaCounter extends CounterBuff {}
	
	@Override
	public MobSpawner spawner() {
        return new ArenaRespawner();
    }

    public static class ArenaBuff extends Buff {

    }
	
	@Override
	protected void createItems() {
		Random.pushGenerator(Random.Long());
		ArrayList<Item> bonesItems = Bones.get();
		if (bonesItems != null) {
			int pos;
			do {
				pos = randomRespawnCell(null);
			} while (pos == entrance());
			for (Item i : bonesItems) {
				drop(i, pos).setHauntedIfCursed().type = Heap.Type.REMAINS;
			}
		}
		Random.popGenerator();
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.GRASS:
				return Messages.get(CityLevel.class, "grass_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(CityLevel.class, "high_grass_name");
			case Terrain.WATER:
				return Messages.get(CityLevel.class, "water_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc( int tile ) {
		switch (tile) {
			case Terrain.ENTRANCE:
				return Messages.get(CityLevel.class, "entrance_desc");
			case Terrain.EXIT:
				return Messages.get(CityLevel.class, "exit_desc");
			case Terrain.HIGH_GRASS:
				return Messages.get(CityLevel.class, "high_grass_desc");
			case Terrain.WALL_DECO:
				return Messages.get(CityLevel.class, "wall_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(CityLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		CityLevel.addCityVisuals(this, visuals);
		return visuals;
	}

	public static class ArenaRespawner extends MobSpawner {

		{
			actPriority = BUFF_PRIO; //as if it were a buff.
		}

		@Override
		protected boolean act() {
			float count = 0;

			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
				if (mob.alignment == Char.Alignment.ENEMY && !mob.properties().contains(Char.Property.MINIBOSS)) {
					count += mob.spawningWeight();
				}
			}

			ArenaCounter counter = Dungeon.hero.buff(ArenaCounter.class);
			float timerBasis = 6.5f;

			if (count < 100) {

				Mob mob = Dungeon.level.createMob();
				mob.state = mob.WANDERING;
				mob.pos = Dungeon.level.randomRespawnCell( mob );
				if (Dungeon.hero.isAlive() && mob.pos != -1 && Dungeon.level.distance(Dungeon.hero.pos, mob.pos) >= 4) {
					GameScene.add( mob );
					mob.beckon( Dungeon.hero.pos );
					Buff.affect(mob, ArenaBuff.class);
					if (counter != null){
						counter.countUp(Actor.TICK);
						int power = (int) counter.count();
						timerBasis = 6.5f + power / 40f;
						if (power >= 5){
							Buff.affect(mob, Stamina.class, power * 3);
							mob.aggro(Dungeon.hero);
						}
						if (power >= 10){
							Buff.affect(mob, ElixirOfAquaticRejuvenation.AquaHealing.class)
									.set(power*5);
						}
						if (power >= 13){
							Buff.affect(mob, Overload.class, 200f);
						}
						if (power >= 20){
							Buff.affect(mob, RageShield.class).set(power*4);
						}
					}
				}
			}
			spend(Dungeon.level.respawnCooldown() / timerBasis);
			return true;
		}
	}
}
