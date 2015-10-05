## Mission ##
The aim of the Eclipse plug-in is to provide automation of configuration needed for projects using pkg-config, such as GTK+ and gtkmm.

## Features ##
The plug-in will set options and switches automatically that gcc needs to add in the selected packages - specifically the header file paths, the libraries, and some other gcc switches.

  * Cross platform support
> > Project can be held in a version control system and even if the packages have been selected in Linux, pulling the project in Windows will automatically update the flags (file-system paths etc).

  * Cross compilation environment support
> > Pkg-config plugin configuration is now per-project configuration rather than a global configuration. Path to the pkg-config binary can be set per-project.

  * Per-toolchain specified pkg-config binary (default and custom paths)

  * Property page


> Used to select packages and set pkg-config configuraion.

## What is PKG-CONFIG? ##
Pkg-config utility outputs the necessary options needed to build a given package that can be passed as compiler options.

  * Linux man page: http://linux.die.net/man/1/pkg-config

  * Wikipedia article: http://en.wikipedia.org/wiki/Pkg-config

## Mailing list ##
http://groups.google.com/group/pkg-config-support-for-eclipse-cdt
<br />
pkg-config-support-for-eclipse-cdt@googlegroups.com

## Update-site ##
http://petrituononen.com/pkg-config-support-for-eclipse-cdt/update

Note: Eclipse 3.7 (Indigo) and CDT 8 (or newer) are mandatory. Try first without setting PKG\_CONFIG\_PATH manually. It is found automatically in most cases.

Windows users: Do not install Gtk+ or Gtkmm  in a path containing spaces, because pkg-config does not support spaces in paths.

## Eclipse Marketplace entry ##
http://marketplace.eclipse.org/content/pkg-config-support-eclipse-cdt
<br />
Outdated screenshot below:
<img src='http://pkg-config-support-for-eclipse-cdt.googlecode.com/files/PropertyPage.png' alt='Screenshot' />