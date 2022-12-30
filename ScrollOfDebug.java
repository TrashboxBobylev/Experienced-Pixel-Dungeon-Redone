package com.zrp200.scrollofdebug;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.*;
import static java.util.Arrays.copyOfRange;

import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.StringBuilder;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
// Commands
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
// needed for HelpWindow
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
// WndTextInput (added in v0.9.4)
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
// Output
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scroll of Debug uses ClassLoader to get every class that can be directly created and provides a command interface with which to interact with them.
 *
 *
 * @author  <a href="https://github.com/zrp200/scrollofdebug">
 *              Zrp200
 * @version v1.2.2
 *
 * @apiNote Compatible with Shattered Pixel Dungeon v1.3.0+, and compatible with any LibGDX Shattered Pixel Dungeon version (post v0.8) with minimal changes.
 * **/
@SuppressWarnings({"rawtypes", "unchecked"})
public class ScrollOfDebug extends Scroll {
    {
        image = ItemSpriteSheet.SCROLL_HOLDER;
    }

    static String lastCommand = ""; // used with '!!'

    /** this is where all the game files are supposed to be located. **/
    private static final String ROOT = "com.shatteredpixel.shatteredpixeldungeon";

    private enum Command {
        HELP(null, // ...
                "[COMMAND | all]",
                "Gives more information on commands",
                "Specifying a command after the help will give an explanation for how to use that command."),
        // todo add more debug-oriented commands
        CHANGES(null, "", "Gives a history of changes to Scroll of Debug."),
        // generation commands.
        GIVE(Item.class,
                "<item> [+<level>] [x<quantity>] [-f|--force] [<method> [<args..>] ]",
                "Creates and puts into your inventory the generated item",
                "Any method specified will be called prior to collection.",
                "Specifying _level_ will set the level of the item to the indicated amount using Item#level. This is the method called when restoring items from a save file. If it's not giving you want you want, please try passing \"upgrade\" <level> as your method.",
                "_--force_ (or _-f_ for short) will disable all on-pickup logic (specifically Item#doPickUp) that may be affecting how the item gets collected into your inventory."),
        SPAWN(Mob.class,
                "<mob> [x<quantity>|(-p|--place)] [<method>]",
                "Creates the indicated mob and places them on the depth.",
                "Specifying [quantity] will attempt to spawn that many mobs ",
                "_-p_ allows manual placement, though it cannot be combined with a quantity argument."),
        SET(Trap.class,
                "<trap>",
                "Sets a trap at an indicated position"),
        AFFECT(Buff.class,
                "<buff> [<duration>] [<method> [<args..>]]",
                "Allows you to attach a buff to a character in sight.",
                "This can be potentially hazardous if a buff is applied to something that it was not designed for.",
                "Specifying _duration_ will attempt to set the duration of the buff. In the cases of buffs that are active in nature (e.g. buffs.Burning), you may need to call a method to properly set its duration.",
                "The method is called after the buff is attached, or on the existing buff if one existed already. This means you can say \"affect doom detach\" to remove doom from that character."),
        SEED(Blob.class,
                "<blob> [<amount>]",
                "Seed a blob of the specified amount to a targeted tile"),
        USE(Object.class, "<object> method [args]", "Use a specified method from a desired class.",
                "It may be handy to see _inspect_ to see usable methods for your object",
                "If you set a variable from this command, the return value of the method will be stored into the variable."),
        INSPECT(Object.class, "<object>", "Gives a list of supported methods for the indicated class."),
        GOTO(null, "<depth>", "Sends your character to the indicated depth."),
        VARIABLES(null,
                "_@_<variable> [ [COMMAND ...] | i[nv] | c[ell] ]",
                "store game objects for later use as method targets or parameters",
                "The variables can be referenced later with their names for the purposes of methods from commands, as well as the _use_ and _inspect_ commands.",
                "You can see all active variable names by typing _@_.",
                "Specifying \"inv\" (or \"i\") will have the game prompt you to select an item from your inventory.",
                "Specifying \"cell\" (or \"c\") will allow you to select a tile. ",
                "When selecting a cell, you may or may not be able to directly select things in the tile you select, depending on the Scroll of Debug implementation.",
                "Please note that variables are not saved when you close the game."
        );

        final Class<?> paramClass;
        final String syntax;
        // a short description intended to fit on one line.
        final String summary;
        // more details on usage. a length of 1 will be treated as an extended description, more will be treated as a list.
        final String[] notes;

        Command(Class<?> paramClass, String syntax, String summary, String... notes) {
            this.paramClass = paramClass;
            this.syntax = syntax;
            this.summary = summary;
            this.notes = notes;
        }

        @Override public String toString() { return name().toLowerCase(); }

        String documentation() { return documentation(this, syntax, summary); }
        static String documentation(Object command, String syntax, String description) {
            return String.format("_%s_ %s\n%s", command, syntax, description);
        }

        // adds more information depending on what the paramClass actually is.
        String fullDocumentation(PackageTrie trie, boolean showClasses) {
            String documentation = documentation();
            if(notes.length > 0) {
                documentation += "\n";
                if(notes.length == 1) documentation += "\n" + notes[0];
                else for(String note : notes) documentation += "\n_-_ " + note;
            }
            if(showClasses && paramClass != null && !paramClass.isPrimitive() && paramClass != Object.class) {
                documentation += "\n\n_Valid Classes_:" + listAllClasses(trie,paramClass);
            }
            return documentation;
        }
        String fullDocumentation(PackageTrie trie) { return fullDocumentation(trie, true); }


        static Command get(String string) { try {
            return string.equals("@") ? VARIABLES :
                    valueOf(string.toUpperCase());
        } catch (Exception e) { return null; } }
    }

    private String storeLocation;

    @Override
    public void doRead() {
        collect(); // you don't lose scroll of debug.
        GameScene.show(new WndTextInput("Enter Command:", null, "", 100, false,
                "Execute", "Cancel") {
            @Override public void onSelect(boolean positive, String text) {
                if(!positive) return;

                // !! handling
                {
                    Matcher m = Pattern.compile("!!").matcher(text);
                    if(m.find()) {
                        GLog.newLine();
                        GLog.i("> %s", text = m.replaceAll(lastCommand));
                        GLog.newLine();
                    }
                }
                lastCommand = text;

                String[] initialInput = text.split(" ");
                Callback init = null;

                final String[] input;

                storeLocation = null;
                if(initialInput.length > 0 && initialInput[0].startsWith(Variable.MARKER)) {
                    // drop from the start, save for later.
                    storeLocation = initialInput[0];
                    if(storeLocation.length() == 1) {
                        if(initialInput.length > 1) GLog.w("warning: remaining arguments were discarded");
                        // list them all
                        StringBuilder s = new StringBuilder();
                        for(Map.Entry<String,Variable> e : Variable.assigned.entrySet()) if(e.getValue().isActive()) {
                            s.append("\n_").append(e.getKey()).append("_ - ").append(e.getValue());
                        }
                        GameScene.show(new HelpWindow("Active Variables: \n" + s));
                        return;
                    }
                    input = Arrays.copyOfRange(initialInput, 1, initialInput.length);

                    // variable-specific actions
                    if(input.length == 0){
                        GLog.p("%s = %s", storeLocation, Variable.toString(storeLocation));
                        return;
                    }
                    String vCommand = input[0].toLowerCase();
                    if(vCommand.matches("i(nv(entory)?)?")) {
                        Variable.putFromInventory(storeLocation);
                        return;
                    } else if(vCommand.matches("c(ell)?")) {
                        Variable.putFromCell(storeLocation);
                        return;
                    }

                } else input = initialInput;

                if(input.length == 0) return;

                Command command; try { command = Command.valueOf(input[0].toUpperCase()); }
                catch (Exception e) {
                    GLog.w("\""+input[0]+"\" is not a valid command.");
                    return;
                }

                if(command == Command.CHANGES) {
                    GameScene.show(new HelpWindow(CHANGELOG));
                }
                else if(command == Command.HELP) {
                    String output = null;
                    boolean all = false;
                    if (input.length > 1) {
                        // we only care about the initial argument.
                        Command cmd = Command.get(input[1]);
                        if (cmd != null) output = cmd.fullDocumentation(trie);
                        else all = input[1].equalsIgnoreCase("all");
                    }
                    if (output == null) {
                        StringBuilder builder = new StringBuilder();
                        for (Command cmd : Command.values()) {
                            if (all) {
                                // extensive. help is omitted because we are using help.
                                if (cmd != Command.HELP) {
                                    builder.append("\n\n")
                                            .append(cmd.fullDocumentation(trie, false));
                                }
                            } else {
                                // use documentation. (show syntax in addition to description)
                                builder.append('\n').appendLine(cmd.documentation());
                            }
                        }
                        output = builder.toString().trim();
                    }
                    GameScene.show(new HelpWindow(output));
                } else if(input.length > 1) {
                    Object storedVariable = Variable.get(input[1]);

                    if(command == Command.GOTO) {
                        if(storedVariable instanceof Integer) {
                            gotoDepth((Integer)storedVariable);
                        }
                        else try {
                            gotoDepth(Integer.parseInt(input[1]));
                        } catch (NumberFormatException e) {
                            GLog.w("Invalid depth provided: " + input[1]);
                        }
                        return;
                    }

                    Class _cls = storedVariable != null ? storedVariable.getClass()
                            : trie.findClass(input[1], command.paramClass);

                    if(command == Command.INSPECT) {
                        Class cls = _cls;
                        if(cls == null) {
                            Command c = Command.get(input[1]);
                            if(c != null) cls = c.paramClass;
                        }
                        if(cls != null) {
                            StringBuilder message = new StringBuilder();
                            for(Map.Entry<Class,Set<Method>> entry : hierarchy(cls).entrySet()) {
                                Class inspecting = entry.getKey();
                                String className = inspecting.getName();
                                int i = className.indexOf(ROOT);
                                if(i == -1) continue;
                                className = className.substring(i+ROOT.length()+1);
                                message.append("\n\n_").append(className).append("_");
                                Object[] enumConstants = inspecting.getEnumConstants();
                                if(enumConstants != null) for(Object member : entry.getKey().getEnumConstants()) {
                                    message.append("\n_->_ ").append(member.toString().replaceAll("_"," "));
                                }
                                for(Field f : inspecting.getFields()) {
                                    if(f.isEnumConstant()) continue;
                                    if(f.getDeclaringClass() != inspecting) continue;
                                    int modifiers = f.getModifiers();
                                    Class t = f.getType();
                                    // wonder if this should be sorted (possibly static -> instance)
                                    // also need to revisit the use of symbols, - is duplicated inappropriately.
                                    message.append("\n_")
                                            .append(Modifier.isStatic(modifiers) ? '-' : '#')
                                            .append('_').append(f.getName().replaceAll("_"," "));
                                    if(Modifier.isFinal(modifiers)) {
                                        boolean showValue = Modifier.isStatic(modifiers);
                                        if(showValue) try {
                                            // no point in showing if we're just going to get a meaningless hash
                                            showValue = t.isPrimitive()
                                                    || t.getMethod("toString")
                                                        .getDeclaringClass() != Object.class;
                                        } catch (NoSuchMethodException e) { showValue = false; }
                                        if(showValue) try {
                                            message.append("=").append(f.get(null));
                                        } catch (IllegalAccessException e) {/* do nothing*/}
                                        else {
                                            message.append(": ").append(t.getSimpleName());
                                        }
                                    } else {
                                        // this signifies that the getter can be accessed this way. hopefully no one was dumb enough to duplicate the name.
                                        message.append(" [<")
                                                .append(f.getType().getSimpleName())
                                                .append(">]");
                                    }
                                }
                                for(Method m : entry.getValue()) {
                                    message.append("\n_").append(Modifier.isStatic(m.getModifiers()) ? '*' : '-').append("_")
                                            .append(m.getName());
                                    Class[] types = m.getParameterTypes();
                                    int left = types.length;
                                    for(Class c : m.getParameterTypes()) {
                                        StringBuilder param = new StringBuilder("<");
                                        param.append(c.getSimpleName().toLowerCase());
                                        // varargs handling. Not supported, but...maybe someday?
                                        if(--left == 0 && m.isVarArgs()) param.append("..");
                                        param.append('>');
                                        // optional handling, currently only hero is handled.
                                        // todo have similar methods be merged, with the offending parameters marked as optional.
                                        if(c == Hero.class || c != Object.class && c.isInstance(Dungeon.level)) {
                                            param.insert(0,'[').append(']');
                                        }
                                        message.append(' ').append(param);
                                    }
                                }
                            }
                            GameScene.show(new HelpWindow(
                                    "inspection of _"+input[1]+"_:"
                                            + message.toString() ));
                            return;
                        }
                    }

                    final Class cls = _cls;

                    if(command == Command.USE) {
                        // alias for inspect when not enough args.
                        if(input.length == 2) onSelect(true, "inspect " + input[1]);
                        else {
                            Object o =
                                    storedVariable != null ? storedVariable : // use the variable if available.
                                    cls == Hero.class ? Dungeon.hero :
                                    cls != Object.class && cls != null && cls.isInstance(Dungeon.level) ? Dungeon.level :
                                    cls != null && canInstantiate(cls) ? Reflection.newInstance(cls) :
                                    null;
                            if(!executeMethod(o, cls, input, 2)) {
                                GLog.w(String.format("No method '%s' was found for %s", input[2], cls));
                            }
                        }
                        return;
                    }

                    boolean valid = true;
                    Object o = null; try {
                        o = Reflection.newInstanceUnhandled(cls);
                        if(o != null) Variable.put(storeLocation, o);
                    } catch (Exception e) { valid = false; }
                    if (valid) switch (command) {
                        case SPAWN: Mob mob = (Mob)o;
                            // process args
                            int quantity = 1;
                            boolean manualPlace = false;
                            boolean qSpecified = false;
                            if(input.length > 2) {
                                String opt = input[2];
                                // is this a forced use of regex?
                                Matcher matcher = Pattern.compile("x(\\d+)").matcher(opt);
                                if(matcher.find()) {
                                    quantity = Integer.parseInt(matcher.group(1));
                                    qSpecified = true;
                                } else if(opt.matches("-p|--place")) {
                                    manualPlace = true;
                                }
                            }
                            if(manualPlace) {
                                GameScene.selectCell(new CellSelector.Listener() {
                                    @Override public String prompt() {
                                        return "Select a tile to place " + mob.name();
                                    }
                                    @Override public void onSelect(Integer cell) {
                                        if(cell == null) return;
                                        // damn it evan for making me copy paste this
                                        if(level.findMob(cell) != null
                                                || !level.passable[cell]
                                                || level.solid[cell]
                                                || !level.openSpace[cell] && mob.properties().contains(Char.Property.LARGE)
                                        ) {
                                            GLog.w("You cannot place %s here.", mob.name());
                                            return;
                                        }
                                        mob.pos = cell;
                                        GameScene.add(mob);
                                        // doing this means that I can't actually let you select cells for methods; it'll be immediately cancelled.
                                        executeMethod(mob,input,3);
                                        GLog.w("Spawned " + mob.name());
                                    }
                                });
                            } else {
                                int spawned = 0;
                                boolean canExecute = true;
                                // nonstandard for loop that generates mobs. first mob is the original one.
                                for(Mob m = mob; m != null && spawned++ < quantity; m = (Mob)Reflection.newInstance(cls)) {
                                    m.pos = level.randomRespawnCell(m);
                                    if(m.pos == -1) break;
                                    GameScene.add(m);
                                    // if it fails we don't want to flood the screen with messages.
                                    if(canExecute) canExecute = executeMethod(m, input, qSpecified?3:2);
                                }
                                spawned--;
                                GLog.w("Spawned "
                                        + mob.name()
                                        + (spawned == 1 ? "" : " x" + spawned)
                                );
                            }
                            break;
                        case SET:
                            Trap t = (Trap)o;
                            GameScene.selectCell(new CellSelector.Listener() {
                                @Override
                                public void onSelect(Integer cell) {
                                    if(cell ==  null || cell == -1) return;
                                    // currently manually set traps are always revealed.
                                    Dungeon.level.setTrap(t.set(cell).reveal(), cell);
                                    Level.set(cell, Terrain.TRAP);
                                }
                                @Override public String prompt() {
                                    return "Select location of trap:";
                                }
                            });
                            break;
                        case GIVE: Item item = (Item)o;
                            item.identify();
                            // todo add enchants/glyphs for weapons/armor?
                            // process modifiers left to right (so later ones have higher precedence)
                            boolean collect = false;
                            for(int i=2; i < input.length; i++) {
                                if(input[i].startsWith("--force") || input[i].equalsIgnoreCase("-f")) {
                                    collect = true;
                                }
                                else if(input[i].matches("[\\-x+]\\d+")) {
                                    switch (input[i].charAt(0)) {
                                        case 'x':
                                            item.quantity(Integer.parseInt(input[i].substring(1)));
                                            break;
                                        case '-':
                                        case '+':
                                            item.level(Integer.parseInt(input[i]));
                                            break;
                                    }
                                }
                                else {
                                    if(!executeMethod(item,input,i)) {
                                        GLog.w("Unrecognized option or method '%s'", input[i]);
                                        onSelect(true, "help " + input[0]);
                                        return;
                                    }
                                    break;
                                }
                            }
                            Item toPickUp = collect ? new Item() {
                                // create wrapper item that simulates doPickUp while actually just calling collect.
                                { image = item.image; }
                                @Override public boolean collect(Bag container) {
                                    return item.collect(container);
                                }
                            } : item;
                            String itemName = item.name();
                            if (toPickUp.doPickUp(curUser)) {
                                // ripped from Hero#actPickUp, kinda.
                                boolean important = item.unique && (item instanceof Scroll || item instanceof Potion);
                                String pickupMessage = Messages.get(curUser, "you_now_have", itemName);
                                if(important) GLog.p(pickupMessage); else GLog.i(pickupMessage);
                                // attempt to nullify turn usage.
                                curUser.spend(-curUser.cooldown());
                            } else {
                                GLog.n(Messages.get(curUser, "you_cant_have", itemName));
                            }
                            break;
                        case AFFECT:
                            Buff buff = (Buff)o;
                            // fixme perhaps have special logic for when additional arguments in general are passed to non-flavor buffs.
                            GameScene.selectCell(new CellSelector.Listener() {
                                @Override public String prompt() {
                                    return "Select the character to apply the buff to:";
                                }
                                @Override public void onSelect(Integer cell) {
                                    Char target;
                                    if(cell == null || cell == -1 || (target = Actor.findChar(cell)) == null) return;
                                    Buff added = null;
                                    int index = 2;

                                    boolean success = false;

                                    if(index >= input.length)
                                    {
                                        // no additional arguments.
                                        Buff.affect(target, cls);
                                    }
                                    else {
                                        if(buff instanceof FlavourBuff) {
                                            try {
                                                added = Buff.affect(target,cls,Float.parseFloat(input[index]));
                                                index++;
                                            } catch (NumberFormatException e) {
                                                added = Buff.affect(target,cls);
                                            }
                                        } else {
                                            added = Buff.affect(target, cls);
                                            // check some common methods for active buffs
                                            String[] methodNames = {"set", "reset", "prolong", "extend"};
                                            for(String methodName : methodNames) {
                                                if(success = executeMethod(added, methodName, copyOfRange(input,index,input.length)))
                                                    break;
                                            }
                                        }
                                        // attempt to call a specified method.
                                        if(!success &&
                                                index < input.length
                                                && !executeMethod(added, input, index)
                                        ) GLog.w("Warning: No supported method matching "+input[index]+" was found.");
                                    }
                                    if(added == null) {
                                        added = Buff.affect(target, cls);
                                    }
                                    // manual announce.
                                    if(added.icon() == BuffIndicator.NONE && !added.announced) {
                                        int color; switch(added.type) {
                                            case POSITIVE:
                                                color = CharSprite.POSITIVE;
                                                break;
                                            case NEGATIVE:
                                                color = CharSprite.NEGATIVE;
                                                break;
                                            default:
                                                color = CharSprite.NEUTRAL;
                                        }
                                        String buffName; try {
                                            // Evan attempted to screw me over by changing toString implementations of buff to a new name() method (see be01254)
                                            // Unfortunately for him, I can just check for it.
                                            buffName = (String)added.getClass()
                                                    .getMethod("name")
                                                    .invoke(added);
                                        } catch(Exception e) { buffName = added.toString(); }
                                        target.sprite.showStatus(color, buffName);
                                    }
                                }
                            });
                            break;
                        case SEED:
                            int a = 1;
                            if(input.length > 2) try {
                                a = Integer.parseInt(input[2]);
                            } catch (Exception e) {/*do nothing*/}
                            final int amount = a;
                            GameScene.selectCell(new CellSelector.Listener() {
                                @Override public String prompt() {
                                    return "Select the tile to seed the blob:";
                                }
                                @Override public void onSelect(Integer cell) {
                                    if(cell == null) return;
                                    GameScene.add(Blob.seed(cell, amount, (Class<Blob>)cls));
                                }
                            });
                    } else GLog.w( "%s \"%s\" not found.", command.paramClass.getSimpleName(), input[1]);
                } else onSelect(true, "help " + text); // lazy me, I know.
            }
        });
    }

    /** level transition was implemented in 1.3.0 **/
    private static final boolean before1_3_0;
    static {
        boolean preRework = false;
        try {
            Class.forName(ROOT + ".levels.features.LevelTransition");
        } catch (ClassNotFoundException e) { preRework = true; }
        before1_3_0 = preRework;
    }
    // force sends you to the corresponding depth.
    private static void gotoDepth(int targetDepth) {
            Mob.holdAllies( Dungeon.level );
            try { saveAll(); } catch (IOException e) {
                Game.reportException(e);
                GLog.w("Unable to save game, aborting.");
                return;
            }
            try {
                // needed for certain implementations of this mechanic.
                Game.scene().destroy();
            } catch (Exception e) {
                // if it fails for some unknown reason I really don't care, move on.
                Game.reportException(e);
            }
            depth = targetDepth;
            Level level; try { level = loadLevel(GamesInProgress.curSlot); } catch (IOException e) {
                // generating a new level before the feature rework incremented the level automatically.
                if(before1_3_0) depth--;
                level = newLevel();
            }
            switchLevel(level, -1);
            Game.switchScene(GameScene.class);
    }

    @Override public String name() {
        return "Scroll of Debug";
    }
    @Override public String desc() {
        StringBuilder builder = new StringBuilder();
        builder.appendLine("A scroll that gives you great power, letting you create virtually any item or mob in the game.")
                .appendLine("\nSupported Commands:");
        for(Command cmd : Command.values()) builder.appendLine(
                // this should hopefully fit on one line.
                String.format("_- %s_: %s", cmd, cmd.summary)
        );
        return builder.append("\nPlease note that some possible inputs may crash the game or cause other unexpected behavior, especially if their targets weren't intended to be created or otherwise used arbitrarily.")
                .toString();
    }
    @Override public boolean isIdentified() {
        return true;
    }
    @Override public boolean isKnown() { return true; }
    {
        unique = true;
    }


    // todo change return type to integer to indicate how many spaces were used, possibly add option to force all to be used. This would allow stacking.
    // variant that derives class from the object given
    <T> boolean executeMethod(T obj, String methodName, String... args) {
        return executeMethod(obj, (Class<T>)obj.getClass(), methodName, args);
    }
    // fixme there's no way to know how many arguments were actually used, which forces this to be the last command.
    /** dynamic method execution logic **/
    <T> boolean executeMethod(T obj, Class<? super T> cls, String methodName, String... args) {
        ArrayList<Method> methods = new ArrayList<>();
        for(Method method : cls.getMethods()) {
            if(args.length > method.getParameterTypes().length) continue; // prevents arbitrary hiding.
            if(method.getName().equalsIgnoreCase(methodName)) methods.add(method);
        }
        Collections.sort(methods, (m1, m2) -> m2.getParameterTypes().length - m1.getParameterTypes().length );
        for(Method method : methods) try {
            Object[] arguments = getArguments(method.getParameterTypes(), args);
            Object result = method.invoke(obj, arguments);
            if(result != null) {
                printMethodOutput(cls,method,method.getModifiers(),result,arguments);
                if(storeLocation != null) Variable.put(storeLocation, result);
            }
            return true;
        } catch (Exception e) {/*do nothing */}
        // check if it is actually a field.
        try {
            Field field = null;
            // this is needed because it's currently not case sensitive, while getField() is.
            for(Field f : cls.getFields()) {
                if(f.getName().equalsIgnoreCase(methodName)) {
                    field = f;
                    break;
                }
            }
            if(field == null) return false;
            Object result;
            if(args.length == 0) {
                result = field.get(obj);
            }
            // fixme this will need to be revisited when I implement stacking of methods
            else if(args.length == 1) {
                // convert the argument to a proper object and assign
                // fixme should not have to do this much wrangling
                field.set(obj, result=getArguments(new Class[]{field.getType()}, args)[0]);
            } else throw new IllegalArgumentException();
            if(storeLocation != null) Variable.put(storeLocation, result);
            printMethodOutput(cls,field,field.getModifiers(), result);
            return true;
        } catch(Exception e) {/*not a valid match*/}
        return false;
    }
    // shortcut methods that interpret input to get the arguments needed
    <T> boolean executeMethod(T obj, Class<? super T> cls, String[] input, int startIndex) {
        return startIndex < input.length && executeMethod(obj, cls, input[startIndex++], startIndex < input.length
                ? copyOfRange(input, startIndex, input.length)
                : new String[0]
        );
    }
    <T> boolean executeMethod(T obj, String[] input, int startIndex) { return executeMethod(obj, (Class<T>)obj.getClass(), input, startIndex); }

    // prints out the result of a method call.
    static void printMethodOutput(Class cls, Member m, int modifiers, Object result, Object... arguments) {
        String argsAsString = Arrays.deepToString(arguments);
        String argFormat = m instanceof Method ? "(%5$s):" : " =";
        GLog.w("%s%s%s"+argFormat+" %4$s",
                cls.getSimpleName(),
                Modifier.isStatic(modifiers) ? '.' : '#',
                m.getName(),
                // this displays arrays properly.
                result.getClass().isArray() ? Arrays.deepToString((Object[])result) : result,
                // snip first and last brace
                argsAsString.substring(1,argsAsString.length()-1)
        );
    }

    // throws an exception if it fails. This removes the need for me to handle errors at all.
    Object[] getArguments(Class[] params, String[] input) throws Exception {
        // todo make a #getArgument(Class, String... input)
        Object[] args = new Object[params.length];
        int j = 0;
        for(int i=0; i < params.length; i++) {
            Class type = params[i];
            args[i] = Variable.get(input[j], type);
            if(args[i] != null) j++; // successful variable call.
            else if (type == int.class || type == Integer.class) {
                args[i] = Integer.parseInt(input[j++]);
            }
            else if (type == float.class || type == Float.class)
                args[i] = Float.parseFloat(input[j++]);
            else if (type == String.class)
                args[i] = input[j++];
            else if (type == Boolean.class || type == boolean.class)
                args[i] = Boolean.parseBoolean(input[j++]);
            else if(input[j].equalsIgnoreCase("null")) {
                // sometimes you want this.
                args[i] = null;
                j++;
                continue;
            }
            else if (Enum.class.isAssignableFrom(type)) {
                for (String name : new String[]{
                        input[j], input[j].toUpperCase(), input[j].toLowerCase()
                })
                    try {
                        args[i] = Enum.valueOf(type, name);
                    } catch (IllegalArgumentException e) {/*continue*/}
                j++;
            }
            else {
                // non-primitive object retrieval
                if(Char.class.isAssignableFrom(type)) {
                    // two subclasses, which means two possible ways for this to go.
                    if (type.isAssignableFrom(Hero.class) && input[j].equalsIgnoreCase("hero")) {
                        params[i] = Hero.class;
                        j++;
                    }
                }
                else if(type != Object.class && type.isInstance(Dungeon.level)) {
                    if(input[j].equals("level")) j++; // for easier understanding.
                    args[i] = Dungeon.level;
                    continue;
                }

                args[i] =
                        type == Hero.class ? curUser :// autofill hero
                        Class.class.isAssignableFrom(type) ? trie.findClass(input[j++], Object.class) :
                        type != Object.class && type.isInstance(Dungeon.level) ? Dungeon.level : // todo add handling if it is in fact passed
                        // blindly instantiate, any error indicates invalid method.
                        Reflection.newInstanceUnhandled(trie.findClass(input[j++], type));
            }
            // todo determine the exact cases where this is reached.
            if (args[i] == null) throw new IllegalArgumentException();
        }
        return args;
    }

    TreeMap<Class,Set<Method>> hierarchy(Class base) {
        TreeMap<Class, Set<Method>> map = new TreeMap<>((c1, c2) -> {
            int res = 0;
            if (c1.isAssignableFrom(c2)) res++;
            if (c2.isAssignableFrom(c1)) res--;
            return res;
        });
        for (Method m : base.getMethods()) {
            Class key = m.getDeclaringClass();
            Set<Method> value = map.get(key);
            if(value == null) map.put(key, value = new HashSet<>());
            value.add(m);
        }
        return map;
    }

    // ensures name uniqueness for help display. treemap so things are sorted.
    private static class ClassNameMap extends HashMap<String, Class> {
        ArrayList<String> getNames() {
            ArrayList<String> names = new ArrayList<>();
            for(Map.Entry<String,Class> entry : entrySet()) {
                if(entry.getValue() != null) names.add(entry.getKey());
            }
            Collections.sort(names);
            return names;
        }

        @Override public Class put(String key, Class cls) {
            String newKey = key;
            if(containsKey(key)) {
                // null means it's been moved.
                Class existing = get(key);
                if(existing != null) { // if it hasn't already been moved.
                    // assumes we can't create conflicts this way.
                    super.put(key, null);
                    put(extendPath(key,existing), existing);
                }
                newKey = extendPath(key, cls);
            }
            //noinspection StringEquality
            return key == newKey ? super.put(key, cls) : put(newKey, cls);
        }

        private static String extendPath(String name, Class cls) {
            if(cls == null) return name;

            String fullName = cls.getName();

            int right = fullName.indexOf(name);
            if(right == 0) return name;

            int left = fullName.lastIndexOf('$', right-2);
            if(left == -1) left = fullName.lastIndexOf('.', right-2);

            return fullName.substring(left+1, right) + name;
        }
    }

    // reflection logic.

    public static ClassLoader loader = ScrollOfDebug.class.getClassLoader();
    public static PackageTrie trie = null; // loaded when needed.
    static {
        try {
            trie = PackageTrie.getClassesForPackage(ROOT);
        } catch (ClassNotFoundException e) { Game.reportException(e); }
    }

    static String listAllClasses(PackageTrie trie, Class<?> parent) {
        ClassNameMap names = new ClassNameMap();
        for(Class cls : trie.getAllClasses()) {
            if(parent.isAssignableFrom(cls)) names.put(cls.getSimpleName(), cls);
        }
        StringBuilder result = new StringBuilder();
        if(!names.isEmpty()) {
            for(String name : names.getNames()) if(canInstantiate(names.get(name))) result.append("\n_-_ ").append(name);
        }
        return result.toString();
    }


    // including RKPD2 scrolling window code.
    private static class HelpWindow extends Window {
        private static final int WIDTH_MIN=120, WIDTH_MAX=220;
        ScrollPane scrollPane;
        HelpWindow(String message) {
            int width = WIDTH_MIN;

            RenderedTextBlock text = PixelScene.renderTextBlock(6);
            text.text(message, width);
            while (PixelScene.landscape()
                    && text.bottom() > (PixelScene.MIN_HEIGHT_L - 10)
                    && width < WIDTH_MAX) {
                text.maxWidth(width += 20);
            }

            int height = (int)text.bottom();
            int maxHeight = (int)(PixelScene.uiCamera.height * 0.9);
            boolean needScrollPane = height > maxHeight;
            if(needScrollPane) height = maxHeight;
            resize((int)text.width(), height);
            if(needScrollPane) {
                add(scrollPane = new ScrollPane(new Component()) {
                    {
                        content.add(text);
                    }
                    // vertical margin is required to prevent text from getting cut off.
                    final float VERTICAL_MARGIN = 1;
                    @Override
                    protected void layout() {
                        text.setPos(0, VERTICAL_MARGIN);
                        // also set the width of the scroll pane
                        content.setSize(width = text.right(), text.bottom()+VERTICAL_MARGIN);
                        width += 2; // padding on the right to cause the controller to be flush against the window.
                        super.layout();
                    }
                });
                scrollPane.setSize(width, height);
            }
            else {
                add(text);
            }
        }

        @Override // this should be removed for pre-v1.2 builds, this method was added in v1.2
        public void offset(int xOffset, int yOffset) {
            super.offset(xOffset, yOffset);
            // this prevents issues in the full ui mode.
            if(scrollPane != null) scrollPane.setSize(scrollPane.width(), scrollPane.height());
        }
    }

    /** this checks if we can create this class using Reflection. **/
    public static boolean canInstantiate(Class c) {
        // check if there's a valid constructor
        try { c.getConstructor(); } catch (NoSuchMethodException e) { return false; }
        return !( Modifier.isAbstract(c.getModifiers()) || Reflection.isMemberClass(c) && !Reflection.isStatic(c) );
    }

    private static final String CHANGELOG
        = ""
        +"_1.2.2_:"
            +"\n_-_ Goto no longer relies on version code in any form."
            +"\n_-_ Variables now attempt to show their ingame name rather than built-in toString."
            +"\n_-_ Seeing the value of a specific variable now uses the same template as when setting them."
            +"\n_-_ Fixed variables being cleared when cancelling a command to set them."
            +"\n_-_ Fixed goto crash when warping to post-v1.3.0 demon halls."
            +"\n_-_ Fixed erroneous assertion in goto description; it does not generate depths in between."
            +"\n"
        +"_1.2.1_:"
            +"\n_-_ Implemented goto, which immediately sends the hero to the targeted depth."
            +"\n_-_ Fixed 1.4.X shattered changes breaking give command text output."
            +"\n"
        +"_1.2.0_:"
            +"\n_-_ Implemented variables! You are now able to store the result of commands that create game objects, as well as anything generated from the use command. You can also store stuff from the map (variable name followed by 'cell' or 'c') and your inventory (variable name followed by 'inv' or 'i')."
            +"\n_-_ Adjusted some descriptions of commands, and added more detail to their extended descriptions."
            +"\n_- help all_ no longer displays all usable classes for every command. To get the functionality, please use _help <command>_."
            +"\n_- For methods, Level arguments are now optional (autofilled with Dungeon.level)"
            +"\n_-_ You can now pass 'null' to methods."
            +"\n_-_ Fixed info window for Scroll of Debug being too big for most devices."
            +"\n_-_ Fixed cases where hero wouldn't be inferred when used in methods"
            +"\n_-_ Fixed not being able to pass true to methods expecting true or false"
        +"\n\n"
        +"_1.1.1_:"
            +"\n_-_ methods that have less parameters than given arguments are now ignored, preventing inappropriate hiding of fields"
            +"\n_-_ fields are no longer case sensitive"
            +"\n_-_ fixed bug making inspection field types way longer"
            +"\n_-_ Fixed scrollpane issues. Really."
            +"\n_-_ There is, however, a bug caused by resizing your window while a help window is active. So don't do that."
        +"\n\n_1.1.0_:"
            +"\n_-_ Actually fixed scrollpane issues this time"
            +"\n_-_ Added the ability to retrieve and set public fields of objects, though such functionality cannot be used to pass them to methods at this time."
        +"\n\n_1.0.0_:"
            +"\n_-_ Changes to Shattered Pixel Dungeon in v1.3.0 mean scroll of debug no longer directly supports versions before it."
            +"\n_-_ Changed formatting style of commands."
            +"\n_-_ Enumerated types now list their values in _inspect_"
            +"\n_- give_ now only requires a - to give degraded items, rather than +-"
            +"\n_- give_ now rejects more invalid inputs."
            +"\n_-_ Scroll of Debug's add implementation only triggers when it is not in the player's inventory."
            +"\n_-_ Fixed scrollpanes being offset incorrectly in full shpd view"
        +"\n\n\n_0.4.0_:"
            +"\n_-_ Added this command."
            +"\n_-_ Added _use_ command, which can call a desired method on any game class that supports it (see _inspect_ for valid methods)."
            +"\n_-_ Including _!!_ in a command will replace it with the previously written command."
            +"\n_- spawn_ command now supports either a quantity argument or a --place (-p) option for manual placing of the mob."
            +"\n_- spawn_ command now supports methods, which are called directly after placing the mob."
            +"\n_-_ When calling methods that yield output, the output is now displayed in the game log."
            +"\n_-_ Scroll of Debug is now considered unique, and thus will not burn."
            +"\n_-_ Fixed more bugs in class finding for jar version caused by 0.3."
            +"\n_-_ Fixed Hero method arguments not being automatically resolved to the hero."
        +"\n\n\n_0.3.3_:"
            +"\n_-_ Scroll Of Debug now automatically adds itself to the first open quickslot, rather than always quickslot #3."
        +"\n_0.3.1, 0.3.2_:"
            +"\n_-_ Fixed faulty package logic caused by 0.3.0"
        +"\n_0.3.0_:"
            +"\n_-_ Scroll of Debug now works on Android";
}