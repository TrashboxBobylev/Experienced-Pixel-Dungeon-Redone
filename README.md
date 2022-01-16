# ScrollOfDebug
A scroll that dynamically detects and provides an interface to create classes for the game Shattered Pixel Dungeon and its mods while running the game.

Currently, the Scroll of Debug only works when ran on .jar versions of the game. It lacks the ability to detect files in the Android filesystem.

## Installation
1. Obtain a fork of <https://github.com/00-Evan/shattered-pixel-dungeon> that is updated to be at least consistent with [v1.0.0](https://github.com/00-Evan/shattered-pixel-dungeon/releases/tag/v1.0.0).
2. Add [`ScrollOfDebug.java`](https://github.com/Zrp200/ScrollOfDebug/blob/master/ScrollOfDebug.java) to [`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls`](https://github.com/00-Evan/shattered-pixel-dungeon/tree/master/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/scrolls).
   * You can do this via [`ScrollOfDebug.patch`](https://github.com/Zrp200/ScrollOfDebug/blob/master/ScrollOfDebug.patch), which will add ScrollOfDebug to the correct location automatically and amend [`Hero.java`](https://github.com/00-Evan/shattered-pixel-dungeon/blob/master/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java) to automatically place the Scroll of Debug into the hero's inventory when loading a game.
   * You can also directly put `ScrollOfDebug.java` into the correct spot manually, but you will also need to ensure it is accessible in the game.
   * If the directory `shatteredpixel.shatteredpixeldungeon` was changed in the fork, a mass find and replace must be done for the scroll to work properly. The patch will still work if "shatteredpixel/shatteredpixeldungeon" and "shatteredpixel.shatteredpixeldungeon" are found and replaced throughout the patch file.
      * The ScrollOfDebug.java, if added directly, only needs the latter mass find and replace operation. 
3. Compile and run the game.
