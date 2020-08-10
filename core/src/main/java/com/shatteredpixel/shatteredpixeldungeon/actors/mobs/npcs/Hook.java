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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth.Wealth;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HookSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collection;

public class Hook extends NPC {
    public int tries;
    public ArrayList<Item> items = new ArrayList<>();
    public int tier = 1;
    public int power = 0;

    {
        spriteClass = HookSprite.class;
        properties.add(Property.IMMOVABLE);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("tries", tries);
        bundle.put("items", items);
        bundle.put("tier", tier);
        bundle.put("power", power);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        tries = bundle.getInt("tries");
        tier = bundle.getInt("tier");
        power = bundle.getInt("power");
        items = new ArrayList<>((Collection<Item>) ((Collection<?>) bundle.getCollection("items")));
    }

    @Override
    public CharSprite sprite() {
        HookSprite sprite = (HookSprite) super.sprite();
        sprite.linkVisuals(this);
        return sprite;
    }

    @Override
    public void updateSpriteState() {
        super.updateSpriteState();
        ((HookSprite)sprite).updateTier(tier);
        sprite.place(pos);
    }

    @Override
    public int defenseSkill( Char enemy ) {
        return INFINITE_EVASION;
    }

    @Override
    public float speed() {
        return 2f;
    }

    @Override
    protected Char chooseEnemy() {
        return null;
    }

    @Override
    public void damage( int dmg, Object src ) {
    }

    @Override
    public void add( Buff buff ) {
        if (buff instanceof Wealth) super.add(buff);
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    protected void onAdd() {
        RingOfWealth.level = power;
        super.onAdd();
    }

    @Override
    protected boolean act() {
        if (!items.isEmpty()) {
            items.clear();
            new Flare(6, 20).color(0xFF0000, true).show(sprite, 3f);
            Sample.INSTANCE.play( Assets.Sounds.DEGRADE );
        }
        ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(tries);
        if (bonus != null && !bonus.isEmpty()) {
            items.addAll(bonus);
            RingOfWealth.showFlareForBonusDrop(sprite);
            Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
            sprite.showStatus( CharSprite.POSITIVE, "!!!");
            spend(1f);
        }
        return super.act();
    }
}
