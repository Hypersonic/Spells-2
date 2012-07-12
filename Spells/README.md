A guide to writing spells
=========================

So you'd like to write spells, eh? Well, lets show you the ropes.

What you **need**:
==================

  1. You need to extend the aor.Spells.Spell class. The best way to do this is:
```import aor.Spells.Spell;
    public class MySpell extends Spell {
        //Your spell here
    }
```

  2. You need to override a few methods from Spell for us to properly run your spell:

  You should override the following methods:

```public String getName()
    public String getDescription()
    public void cast(Player player)
```
  In addition, if you want custom requirements, such as that they be standing on top of a dirt block or whatever totally outlandish ideas you have, override
  ```public boolean checkRequirements(Player player)```

The cast method
===============

The cast method is automatically called when your spell is used, as long as checkRequirements returned true.

This is where most of your code will probably happen. From here, you can do just about anything a bukkit plugin could do. Refer to the [Bukkit API](http://jd.bukkit.org) for more information.
