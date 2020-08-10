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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.BlackMimicLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.NewCavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM300Sprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.shatteredpixel.shatteredpixeldungeon.levels.BlackMimicLevel.mainArena;

public class BlackMimic extends Mob {

	{
		//TODO improved sprite
		spriteClass = MimicSprite.Black.class;

		HP = HT = 3000;
		EXP = 2000;
		defenseSkill = 20;

		properties.add(Property.BOSS);
		properties.add(Property.INORGANIC);
		properties.add(Property.DEMONIC);
		properties.add(Property.UNDEAD);
		properties.add(Property.LARGE);
        switch (Dungeon.cycle){
            case 1:
                HP = HT = 15000;
                defenseSkill = 125;
                EXP = 6000;
                break;
            case 2:
                HP = HT = 600000;
                defenseSkill = 360;
                EXP = 500000;
                break;
            case 3:
                HP = HT = 30000000;
                defenseSkill = 1300;
                EXP = 25000000;
                break;
            case 4:
                HP = HT = 2000000000;
                defenseSkill = 23000;
                EXP = 2100000000;
                break;
        }
	}

	@Override
	public int damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Random.NormalIntRange(100, 156);
            case 2: return Random.NormalIntRange(524, 731);
            case 3: return Random.NormalIntRange(3400, 6124);
            case 4: return Random.NormalIntRange(600000, 800000);
        }
		return Random.NormalIntRange( 29, 49 );
	}

	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 160;
            case 2: return 564;
            case 3: return 1500;
            case 4: return 30000;
        }
		return 38;
	}

	@Override
	public int drRoll() {
        switch (Dungeon.cycle){
            case 1: return Random.NormalIntRange(60, 100);
            case 2: return Random.NormalIntRange(300, 520);
            case 3: return Random.NormalIntRange(2000, 3600);
            case 4: return Random.NormalIntRange(423000, 520000);
        }
		return Random.NormalIntRange(0, 20);
	}

	public int pylonsActivated = 0;
	public boolean supercharged = false;
	public boolean chargeAnnounced = false;
	public boolean isCopy = false;

	private int turnsSinceLastAbility = -1;
	private int abilityCooldown = Random.NormalIntRange(MIN_COOLDOWN, MAX_COOLDOWN);

	private static final int MIN_COOLDOWN = 1;
	private static final int MAX_COOLDOWN = 6;

	private int lastAbility = 0;
	private static final int NONE = 0;
	private static final int GAS = 1;
	private static final int ROCKS = 2;
	private static final int BOMB = 3;
    private static final int SUMMON = 4;
    private static final int MIRROR = 5;

	private static final String PYLONS_ACTIVATED = "pylons_activated";
	private static final String SUPERCHARGED = "supercharged";
	private static final String CHARGE_ANNOUNCED = "charge_announced";

	private static final String TURNS_SINCE_LAST_ABILITY = "turns_since_last_ability";
	private static final String ABILITY_COOLDOWN = "ability_cooldown";

	private static final String LAST_ABILITY = "last_ability";
    private static final String COPY = "fool";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PYLONS_ACTIVATED, pylonsActivated);
		bundle.put(SUPERCHARGED, supercharged);
		bundle.put(CHARGE_ANNOUNCED, chargeAnnounced);
		bundle.put(TURNS_SINCE_LAST_ABILITY, turnsSinceLastAbility);
		bundle.put(ABILITY_COOLDOWN, abilityCooldown);
		bundle.put(LAST_ABILITY, lastAbility);
		bundle.put(COPY, isCopy);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		pylonsActivated = bundle.getInt(PYLONS_ACTIVATED);
		supercharged = bundle.getBoolean(SUPERCHARGED);
		chargeAnnounced = bundle.getBoolean(CHARGE_ANNOUNCED);
		turnsSinceLastAbility = bundle.getInt(TURNS_SINCE_LAST_ABILITY);
		abilityCooldown = bundle.getInt(ABILITY_COOLDOWN);
		lastAbility = bundle.getInt(LAST_ABILITY);
		isCopy = bundle.getBoolean(COPY);

		if (turnsSinceLastAbility != -1){
			BossHealthBar.assignBoss(this);
			if (!supercharged && pylonsActivated == 2) BossHealthBar.bleed(true);
		}
	}

	@Override
	protected boolean act() {
		GameScene.add(Blob.seed(pos, 0, FallingRocks.class));
		GameScene.add(Blob.seed(pos, 0, CorrosiveGas.class));



		//ability logic only triggers if DM is not supercharged
		if (!supercharged || isCopy){
			if (turnsSinceLastAbility >= 0) turnsSinceLastAbility++;

			//in case DM-300 hasn't been able to act yet
			if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
				fieldOfView = new boolean[Dungeon.level.length()];
				Dungeon.level.updateFieldOfView( this, fieldOfView );
			}

			//determine if DM can reach its enemy
			boolean canReach;
			if (enemy == null){
				if (Dungeon.level.adjacent(pos, Dungeon.hero.pos)){
					canReach = true;
				} else {
					canReach = (Dungeon.findStep(this, Dungeon.hero.pos, Dungeon.level.openSpace, fieldOfView, true) != -1);
				}
			} else {
				if (Dungeon.level.adjacent(pos, enemy.pos)){
					canReach = true;
				} else {
					canReach = (Dungeon.findStep(this, enemy.pos, Dungeon.level.openSpace, fieldOfView, true) != -1);
				}
			}

			if (state != HUNTING){
				if (Dungeon.hero.invisible <= 0 && canReach){
					beckon(Dungeon.hero.pos);
				}
			} else {

				if (enemy == null) enemy = Dungeon.hero;

				if (!canReach){

					if (fieldOfView[enemy.pos] && turnsSinceLastAbility >= MIN_COOLDOWN){

						lastAbility = GAS;
						turnsSinceLastAbility = 0;
						spend(TICK);

						GLog.w(Messages.get(this, "vent"));
						if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
							sprite.zap(enemy.pos);
							return false;
						} else {
							ventGas(enemy);
							Sample.INSTANCE.play(Assets.Sounds.GAS);
							return true;
						}

					}

				} else {
					if (turnsSinceLastAbility > abilityCooldown) {

						if (lastAbility == NONE) {
							//50/50 either ability
							lastAbility = Random.IntRange(1, 5);
						} else if (lastAbility == GAS) {
							//more likely to use rocks
							lastAbility = Random.IntRange(2, 5);
						} else if (lastAbility == ROCKS){
							//more likely to use gas
							lastAbility = Random.IntRange(3, 5);
						} else {
                            lastAbility = Random.IntRange(0, 5);
                        }

						//doesn't spend a turn if enemy is at a distance
						if (Dungeon.level.adjacent(pos, enemy.pos)){
							spend(TICK);
						}

						turnsSinceLastAbility = 0;
						abilityCooldown = Random.NormalIntRange(MIN_COOLDOWN, MAX_COOLDOWN);

						if (lastAbility == GAS) {
							GLog.w(Messages.get(this, "vent"));
							if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
								sprite.zap(enemy.pos);
								return false;
							} else {
								ventGas(enemy);
								Sample.INSTANCE.play(Assets.Sounds.GAS);
								return true;
							}
						} else if (lastAbility == ROCKS) {
							GLog.w(Messages.get(this, "rocks"));
							if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                                ((MimicSprite.Black)sprite).slam(enemy.pos);
								return false;
							} else {
								dropRocks(enemy);
								Sample.INSTANCE.play(Assets.Sounds.ROCKS);
								return true;
							}
						} else if (lastAbility == BOMB){
                            GLog.w(Messages.get(this, "bomb"));
                            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                                ((MimicSprite.Black)sprite).throww(enemy.pos);
                                return false;
                            } else {
                                NewTengu.throwBomb(this, Dungeon.hero);
                                return true;
                            }
                        } else if (lastAbility == SUMMON) {
                            GLog.w(Messages.get(this, "summon"));
                            DistortionTrap trap = new DistortionTrap();
                            do {
                                trap.pos = Dungeon.level.pointToCell(Random.element(mainArena.getPoints()));
                            } while (!Dungeon.level.openSpace[trap.pos] || Dungeon.level.map[trap.pos] == Terrain.EMPTY_SP);
                            trap.activate();

                            return true;
                        } else {
                            ArrayList<Integer> respawnPoints = new ArrayList<>();

                            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                                int p = pos + PathFinder.NEIGHBOURS8[i];
                                if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                                    respawnPoints.add( p );
                                }
                            }
                            if (!respawnPoints.isEmpty()) {
                                if (Random.Float() < 0.5f) {
                                    BlackMimic mimic = new BlackMimic();
                                    int index = Random.index( respawnPoints );
                                    mimic.isCopy = true;
                                    GameScene.add( mimic );
                                    ScrollOfTeleportation.appear( mimic, respawnPoints.get( index ) );
                                } else {
                                    GoldenMimic mimic = new GoldenMimic();
                                    mimic.setLevel( Dungeon.escalatingDepth() );
                                    int index = Random.index( respawnPoints );
                                    GameScene.add( mimic );
                                    ScrollOfTeleportation.appear( mimic, respawnPoints.get( index ) );
                                }
                            }
                        }
					}
				}
			}
		} else {

			if (!chargeAnnounced){
				yell(Messages.get(this, "supercharged"));
				chargeAnnounced = true;
			}

			if (state == WANDERING && Dungeon.hero.invisible <= 0){
				beckon(Dungeon.hero.pos);
				state = HUNTING;
				enemy = Dungeon.hero;
			}

		}

		return super.act();
	}

	@Override
	protected Char chooseEnemy() {
		Char enemy = super.chooseEnemy();
		if (supercharged && enemy == null){
			enemy = Dungeon.hero;
		}
		return enemy;
	}

	@Override
	public void move(int step) {
		super.move(step);

		Camera.main.shake( supercharged ? 6 : 1, 0.25f );

		if (Dungeon.level.map[step] == Terrain.INACTIVE_TRAP && state == HUNTING) {

			//don't gain energy from cells that are energized
			if (BlackMimicLevel.PylonEnergy.volumeAt(pos, BlackMimicLevel.PylonEnergy.class) > 0){
				return;
			}

			if (Dungeon.level.heroFOV[step]) {
				if (buff(Barrier.class) == null) {
					GLog.w(Messages.get(this, "shield"));
				}
				Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
				sprite.emitter().start(SparkParticle.STATIC, 0.05f, 20);
			}

			Buff.affect(this, Barrier.class).setShield( 30 + (HT - HP)/10);

		}
	}

	@Override
	public float speed() {
		return super.speed() * (supercharged ? 3 : 1);
	}

	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			turnsSinceLastAbility = 0;
			yell(Messages.get(this, "notice"));
			for (Char ch : Actor.chars()){
				if (ch instanceof DriedRose.GhostHero){
					((DriedRose.GhostHero) ch).sayBoss();
				}
			}
		}
	}

	public void onZapComplete(){
		ventGas(enemy);
		next();
	}

	public void ventGas( Char target ){
		Dungeon.hero.interrupt();

		int gasVented = 0;

		Ballistica trajectory = new Ballistica(pos, target.pos, Ballistica.STOP_TARGET);

		for (int i : trajectory.subPath(0, trajectory.dist)){
			GameScene.add(Blob.seed(i, 200, CorrosoGas.class).setStr(Dungeon.escalatingDepth() / 2));
			gasVented += 20;
		}

		GameScene.add(Blob.seed(trajectory.collisionPos, 290, CorrosoGas.class).setStr(Dungeon.escalatingDepth() / 2));

		if (gasVented < 250){
			int toVentAround = (int)Math.ceil((250 - gasVented)/8f);
			for (int i : PathFinder.NEIGHBOURS8){
				GameScene.add(Blob.seed(pos+i, toVentAround, CorrosoGas.class).setStr(Dungeon.escalatingDepth() / 2));
			}

		}

	}

	public void onSlamComplete(){
		dropRocks(enemy);
		next();
	}

    public void onThrowComplete(){
        NewTengu.throwBomb(this, enemy);
        next();
    }

	public void dropRocks( Char target ) {

		Dungeon.hero.interrupt();
		final int rockCenter;

		if (Dungeon.level.adjacent(pos, target.pos)){
			int oppositeAdjacent = target.pos + (target.pos - pos);
			Ballistica trajectory = new Ballistica(target.pos, oppositeAdjacent, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(target, trajectory, 2, false);
			if (target == Dungeon.hero){
				Dungeon.hero.interrupt();
			}
			rockCenter = trajectory.path.get(Math.min(trajectory.dist, 2));
		} else {
			rockCenter = target.pos;
		}

		//we handle this through an actor as it gives us fine-grainted control over when the blog acts vs. when the hero acts
		//FIXME this is really messy to just get some fine-grained control. would be nice to build this into blob functionality, or just not use blobs for this at all
		Actor a = new Actor() {

			{
				actPriority = HERO_PRIO+1;
			}

			@Override
			protected boolean act() {

				//pick an adjacent cell to the hero as a safe cell. This cell is less likely to be in a wall or containing hazards
				int safeCell;
				do {
					safeCell = rockCenter + PathFinder.NEIGHBOURS8[Random.Int(8)];
				} while (safeCell == pos
						|| (Dungeon.level.solid[safeCell] && Random.Int(2) == 0)
						|| (Blob.volumeAt(safeCell, BlackMimicLevel.PylonEnergy.class) > 0 && Random.Int(2) == 0));

				int start = rockCenter - Dungeon.level.width() * 3 - 3;
				int pos;
				for (int y = 0; y < 7; y++) {
					pos = start + Dungeon.level.width() * y;
					for (int x = 0; x < 7; x++) {
						if (!Dungeon.level.insideMap(pos)) {
							pos++;
							continue;
						}
						//add rock cell to pos, if it is not solid, and isn't the safecell
						if (!Dungeon.level.solid[pos] && pos != safeCell && Random.Int(Dungeon.level.distance(rockCenter, pos)) == 0) {
							//don't want to overly punish players with slow move or attack speed
							GameScene.add(Blob.seed(pos, 1, FallingRocks.class));
						}
						pos++;
					}
				}
				Actor.remove(this);
				return true;
			}
		};
		Actor.addDelayed(a, Math.min(target.cooldown(), 3*TICK));

	}

	private boolean invulnWarned = false;

	@Override
	public void damage(int dmg, Object src) {
	    if (Dungeon.cycle == 4) dmg /= 3.5f;
		super.damage(dmg, src);
		if (isInvulnerable(src.getClass())){
			return;
		}

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !isImmune(src.getClass())) lock.addTime(dmg);

		int threshold = HT/3 * (2- pylonsActivated);

		if (HP < threshold){
			HP = threshold;
			supercharge();
		}

	}

	@Override
	public boolean isInvulnerable(Class effect) {
		if (supercharged && !invulnWarned){
			invulnWarned = true;
			GLog.w(Messages.get(this, "charging_hint"));
		}
		if (isCopy) return true;
		return supercharged;
	}

	public void supercharge(){
		supercharged = true;
		((BlackMimicLevel)Dungeon.level).activatePylon();
		pylonsActivated++;

		spend(3f);
		yell(Messages.get(this, "charging"));
		sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "invulnerable"));
		chargeAnnounced = false;

	}

	public boolean isSupercharged(){
		return supercharged;
	}

	public void loseSupercharge(){
		supercharged = false;
		sprite.resetColor();

		if (pylonsActivated < 2){
			yell(Messages.get(this, "charge_lost"));
		} else {
			yell(Messages.get(this, "pylons_destroyed"));
			BossHealthBar.bleed(true);
		}
	}

	@Override
	public boolean isAlive() {
		return HP > 0 || pylonsActivated < 2;
	}

	@Override
	public void die( Object cause ) {

		super.die( cause );

		GameScene.bossSlain();
		Dungeon.level.unseal();

        Dungeon.level.drop( new Amulet(), pos).sprite.drop( pos );

		LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
		if (beacon != null) {
			beacon.upgrade();
		}

        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                if (mob.isAlive()) {
                    mob.die(Dungeon.hero);
                }
            }

		yell( Messages.get(this, "defeated") );
	}

	@Override
	protected boolean getCloser(int target) {
		if (super.getCloser(target)){
			return true;
		} else {

			if (rooted || target == pos) {
				return false;
			}

			int bestpos = pos;
			for (int i : PathFinder.NEIGHBOURS8){
				if (Dungeon.level.openSpace[pos+i] && Actor.findChar(pos+i) == null &&
						Dungeon.level.distance(bestpos, target) > Dungeon.level.distance(pos+i, target)){
					bestpos = pos+i;
				}
			}
			if (bestpos != pos){
				move( bestpos );
				return true;
			}

			if (!supercharged || state != HUNTING || Dungeon.level.adjacent(pos, target)){
				return false;
			}

			for (int i : PathFinder.NEIGHBOURS8){
				if (Actor.findChar(pos+i) == null &&
						Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
					bestpos = pos+i;
				}
			}
			if (bestpos != pos){
				Sample.INSTANCE.play( Assets.Sounds.ROCKS );

				Rect gate = NewCavesBossLevel.gate;
				for (int i : PathFinder.NEIGHBOURS9){
					if (Dungeon.level.map[pos+i] == Terrain.WALL || Dungeon.level.map[pos+i] == Terrain.WALL_DECO){
						Point p = Dungeon.level.cellToPoint(pos+i);
						if (p.y < gate.bottom && p.x > gate.left-2 && p.x < gate.right+2){
							continue; //don't break the gate or walls around the gate
						}
						Level.set(pos+i, Terrain.EMPTY_DECO);
						GameScene.updateMap(pos+i);
					}
				}
				Dungeon.level.cleanWalls();
				Dungeon.observe();
				spend(2f);

				bestpos = pos;
				for (int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(pos+i) == null && Dungeon.level.openSpace[pos+i] &&
							Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
						bestpos = pos+i;
					}
				}

				if (bestpos != pos) {
					move(bestpos);
				}
				Camera.main.shake( 12, 1f );

				return true;
			}

			return false;
		}
	}

	@Override
	public String description() {
		String desc = super.description();
		if (supercharged) {
			desc += "\n\n" + Messages.get(this, "desc_supercharged");
		}
		return desc;
	}

	{
		immunities.add(Sleep.class);
		immunities.add(CorrosoGas.class);

		resistances.add(Terror.class);
		resistances.add(Charm.class);
		resistances.add(Vertigo.class);
		resistances.add(Cripple.class);
		resistances.add(Chill.class);
		resistances.add(Frost.class);
		resistances.add(Roots.class);
		resistances.add(Slow.class);
	}

	public static class FallingRocks extends Blob {

		{
			alwaysVisible = true;
		}

		@Override
		protected void evolve() {

			boolean rocksFell = false;

			int cell;
			for (int i = area.left; i < area.right; i++){
				for (int j = area.top; j < area.bottom; j++){
					cell = i + j* Dungeon.level.width();
					off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

					if (off[cell] > 0) {
						volume += off[cell];
					}

					if (cur[cell] > 0 && off[cell] == 0){

						CellEmitter.get( cell ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );

						Char ch = Actor.findChar(cell);
						if (ch != null && !(ch instanceof BlackMimic)){
							Buff.prolong( ch, Paralysis.class, 3 );
						}

						rocksFell = true;
					}
				}
			}

			if (rocksFell){
				Camera.main.shake( 3, 0.7f );
				Sample.INSTANCE.play(Assets.Sounds.ROCKS);
			}

		}

		@Override
		public void use(BlobEmitter emitter) {
			super.use(emitter);

			emitter.bound = new RectF(0, -0.2f, 1, 0.4f);
			emitter.pour(EarthParticle.FALLING, 0.1f);
		}

		@Override
		public String tileDesc() {
			return Messages.get(this, "desc");
		}
	}

	public static class CorrosoGas extends CorrosiveGas{
        public CorrosoGas setStr(int str){
            if (str > strength) {
                strength = str;
            }
            return this;
        }
    }
}
