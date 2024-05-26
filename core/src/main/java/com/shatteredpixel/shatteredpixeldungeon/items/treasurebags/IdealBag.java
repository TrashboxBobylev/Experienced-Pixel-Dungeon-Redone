/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2020 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.treasurebags;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Perks;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class IdealBag extends TreasureBag {
    {
        image = ItemSpriteSheet.IDEAL_BAG;
    }

    @Override
    protected ArrayList<Item> items() {
        ArrayList<Item> items = new ArrayList<>();
        int amount = Random.Int(0, 15)*10;
        if (Dungeon.hero.perks.contains(Perks.Perk.MORE_BAG)) amount *= 1.5f;
        for(int i = 0; i < amount; i++) {
            if (Dungeon.Float(1) <= 0.045f){
                Item gift = null;
                switch (Random.Int(9)){
                    case 0: case 1:
                        gift = new Plutonium(); break;
                    case 2: case 3:
                        gift = new OsmiridiumPlate(); break;
                    case 4: case 5:
                        gift = new BrokenEnderiumBlade(); break;
                    case 6: case 7:
                        gift = new EnergyBottle(); break;
                    case 9:
                        gift = new RingOfElements(); break;
                }
                items.add(gift);
            } else {
                while (true){
                    Item loot = Generator.random();
                    if (loot.stackable || Random.Int(2) != 0) {
                        items.add(loot);
                        break;
                    }
                }
            }
        }
        return items;
    }

    public static class Plutonium extends Item {
        {
            image = ItemSpriteSheet.PLUTONIUM;
            stackable = true;
        }

        @Override
        public boolean doPickUp(Hero hero, int pos, float time) {
            Buff.affect(hero, Poison.class).set((hero.HT + hero.HTBoost) / 6f);
            return super.doPickUp(hero, pos, time);
        }

        @Override
        public boolean isIdentified() {
            return true;
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }



        @Override
        public long value() {
            return 66 * quantity;
        }
    }

    public static class OsmirdiumShield extends FlavourBuff {

        public static final float DURATION	= 8.99f;

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.OSMISHIELD;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                target.sprite.add(CharSprite.State.SHIELDED);
            } else if (target.buff(Blocking.BlockBuff.class) == null) {
                target.sprite.remove(CharSprite.State.SHIELDED);
            }
        }
    }

    public static class OsmiridiumPlate extends Spell {
        {
            image = ItemSpriteSheet.OSMIRIDIUM;
        }

        @Override
        protected void onCast(Hero hero) {
            Invisibility.dispel();
            curUser.spend( 1f );
            curUser.busy();
            ((HeroSprite)curUser.sprite).read();
            Buff.affect(curUser, OsmirdiumShield.class, OsmirdiumShield.DURATION);
            Sample.INSTANCE.play( Assets.Sounds.READ );
            Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
            detach( curUser.belongings.backpack );
        }
    }

    public static class BrokenEnderiumBlade extends Item {
        {
            image = ItemSpriteSheet.ENDERIUM;
            stackable = true;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public long value() {
            return (new Gold().random().quantity())*15 * quantity;
        }
    }

    public static class EnergyBottle extends Item {
        {
            image = ItemSpriteSheet.ENERGITE;
            stackable = true;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public long value() {
            return (new Gold().random().quantity())*4 * quantity;
        }

        @Override
        public long energyVal() {
            return 125 * quantity;
        }
    }
}
