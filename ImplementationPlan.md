# Implementation plan #

  * DONE! List all currently known packages pkg-config --list-all in property page. (Properties -> C/C++ Build -> Settings -> Pkg-config tab)
  * DONE! Icon for the pkg-config property tab
  * DONE! Ability to check packages that are then used as part of compiler and linker flags. Unchecking removes the added options.
  * DONE! Add include paths and other gcc switches that pkg-config utility outputs to GCC compiler. Add libraries and library search paths to linker. These values are added on a per project basis to selected build configuration.
  * DONE! Get the output (--cflags, --libs) from pkg-config and parse them such as they can be added to compiler and linker (remove -I, -L, -l; separate lib paths from lib files; separate values into an array).
  * DONE! Project templates for gtk+ and gtkmm projects.
  * Add gtk+ or gtkmm package automatically to gtk+/gtkmm project templates
  * DONE! Preference page for PKG-CONFIG specific configurations.
    * Environment paths (default values from system environment variables if set)
      * PKG\_CONFIG\_PATH
      * PKG\_CONFIG\_LIBDIR
    * Option to remove multiple PKG\_CONFIG\_PATHs at once (multiselection)
  * (Add gtk project nature.)
  * Extra: Select packages automatically based on source code (Indexer on/off feature).