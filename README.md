# ScrollOfDebug
A scroll that dynamically detects and provides an interface to create classes for the game [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon) and its mods while running the game.

This repository can be added without impacting the functionality of the implementing project if desired.

## Installation

1. Obtain a fork of <https://github.com/00-Evan/shattered-pixel-dungeon> that is updated to be at least consistent with [v1.0.0](https://github.com/00-Evan/shattered-pixel-dungeon/releases/tag/v1.0.0).

2. Implement Scroll of Debug via one of the following methods described below.
3. Ensure the code compiles.
   * If the directory `shatteredpixel.shatteredpixeldungeon` was changed in the fork, a mass find and replace must be done for the scroll to work properly.

### Via `git pull`

#### Identify The Correct Branch

Scroll of Debug has 4 active "implementation" branches based on various places in Shattered's commit history as well as the history of Scroll of Debug itself.

```bash
git pull https://github.com/zrp200/ScrollOfDebug shpd/VERSION/IMPLEMENTATION
```
Where:
* `VERSION` is one of `1.0` or `1.3`
* `IMPLEMENTATION` is one of `pc-only` or `apk_support`

It is possible to upgrade from `1.0` to `1.3` and from `pc-only` to `apk_support` with no Scroll of Debug-related conflicts due to the nature of how the branches are implemented.

If you want to avoid pulling in Scroll of Debug's admittably cursed commit history, add a `--squash` argument to the command.

##### Version
Currently, the version ranges supported are:
* [[v1.0.0](https://github.com/00-Evan/shattered-pixel-dungeon/releases/tag/v1.0.0),
  [v1.3.0](https://github.com/00-Evan/shattered-pixel-dungeon/releases/tag/v1.3.0))
  (`shpd/1.0/` branches)
* [v1.3.0](https://github.com/00-Evan/shattered-pixel-dungeon/releases/tag/v1.3.0) and newer. (`shpd/1.3/` branches)

*Note*: Scroll of Debug is developed on the latest Shattered by default, so the `1.0` branches may be updated a bit more slowly when features have to be changed during backporting to ensure compatibility.

##### Implementation
There are two variants of Scroll of Debug: the minimal `pc-only` implementation, and the invasive `apk_support` implementation that allows Scroll of Debug to work properly on Android devices.

###### `pc-only`
The `pc-only` branch is a direct implementation of the project via the subtree method (described later).

The only added change is to `GameScene.java`, where the Scroll of Debug is automatically added to the hero's inventory whenever `GameScene` is loaded when the game is in debug mode (`-INDEV` suffix), and removes it automatically if the game is not in debug mode. This specific implementation ensures that Scroll of Debug is always available to the developer while preventing unintended leaks of the scroll to players.

However, this implementation does not have full functionality in Android builds --- the base implementation of Scroll of Debug will be unable to detect classes in `.apk` builds of the game, causing Scroll of Debug's reflection-based commands (`give`, `spawn`, `set`, `seed`, `use`, `inspect`) to not work properly (though variables may still work).

**`pc-only` implementation is best when**:
* You do not intend to share Scroll of Debug builds with others.
* You want to keep Scroll of Debug confined to one directory.

###### `apk_support` (NEW)

The `apk-support` implementation spreads out Scroll of Debug files across the implementing repository to ensure that Android builds have full functionality of Scroll of Debug.

It moves one of the core files (`PackageTrie`) to `SPD-CLASSES`, amends `PlatformSupport` to allow a custom implementation of class retrieval, and then provides a custom Android implementation of this to ensure classes are read correctly.

It is entirely incompatible with the subtree-based implementation of Scroll of Debug.

*Note:* You can upgrade from the `pc-only` implementation (or subtree implementation) to the `apk_support` implementation by pulling in the `apk_support` branch at any time. The `apk_support` branch is based on `pc-only`.

### Via `git subtree`

The master branch of the repository can be directly pulled using git subtrees:
```bash
git subtree -P core/src/main/java/com/zrp200/scrollofdebug add https://github.com/zrp200/ScrollOfDebug master
```
It can then also be updated by replacing `add` with `pull` in the previous command.

Implementing it this way is effectively identical to pulling in the `shpd/1.3/pc-only` branch, but it will lack the `GameScene.java` changes that let Scroll of Debug actually get added.

As such, it is not the preferred way to implement it, but if you lack Shattered's commit history (or desire a custom method of giving access to scroll of debug in-game) for any reason it may be the only viable option for implementation.

### Via copy-paste

There is virtually no difference between the subtree method and straight copy-pasting the files into `core/src/main/java/com/zrp200/scrollofdebug`.

## Usage

Scroll of Debug lets the reader create virtually any game-related object with a single command.

A more actively updated documentation can be found in the [wiki](https://github.com/Zrp200/ScrollOfDebug/wiki).

The main commands currently are:
* `give <item> [+<level>]` --- places an item of the corresponding `item` class into your inventory. The level can be optionally specified.
* `spawn <mob> [-p]` --- spawns a given `mob` somewhere on the level. `-p` lets you place the mob.
* `affect <buff> [duration]` applies the specified `buff` to a selected mob.
* `seed <blob>` ---
* `use <class> <method> [ARGS...]` --- which uses the corresponding method of the provided class. A method can also be appended to the end of the `give`, `spawn`, and `affect` commands to act on the created entity.
* `inspect <class>` gives a list of defined methods and fields for the specified class.
* `goto <depth>` --- warps you to the target depth immediately.
* `help [command]` --- gives an overview on all supported commands. Specifying the command will give more information about that specific command.

### Autofilled entities
The following classes are autofilled with the corresponding Dungeon object by default.
* `hero` < `Dungeon.hero`
* `level` < `Dungeon.level`

### Variables
Sometimes we need to run multiple methods on a created object or otherwise store an object for later commands.

Scroll of Debug supports **variables** to make this possible.

#### Storage of Variables

The result of methods that create objects can be stored by starting the command with `@<variable>` where `<variable>` is the name you want it to be stored as.

Alternatively, existing entities can be selected directly by using `@<variable> cell` to select a cell or `@<variable> inv` to select from the inventory.

#### Usage of variables
Once created, the variable can be used by using `@<variable>` in place of any class or argument of a command.