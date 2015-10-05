## Method 1. Installing using Ubuntu's package manager ##

In case the Linux distribution you are using is Ubuntu the easiest way to install GTK+ is by executing the following command:

`sudo apt get install gnome-core-devel build-essential libgtk2.0-dev libgtk2.0-doc`

Package manager will resolve dependencies for you.

## Method 2. Compiling GTK+ and it's dependencies ##

Download the latest (stable) release of GTK+ from http://www.gtk.org/download-linux.html<br />

The following dependency packages need to be installed.

  * gdk`-`pixbuf
  * glib
  * pango
  * atk
  * cairo
  * cairo`-`gobject
  * tiff
  * pkg`-`config
  * GNU Make

Note: these packages must be development versions with source codes.

Most of these are found at http://ftp.acc.umu.se/pub/gnome/sources/.

These packages can be installed by typing ./configure, make and make install (admin rights) commands on a command line interpreter.
It is safe to run ldconfig after each package intallation (so that the newly installed libraries will be found).

### Installing GTK+ ###
  1. Set environment variables before running configure
    * CPPFLAGS="-I/usr/lib/gtk/include"
    * LDFLAGS="-L/usr/lib/gtk/lib"
    * PKG\_CONFIG\_PATH="/usr/lib/gtk/lib/pkgconfig"
    * LD\_LIBRARY\_PATH="/usr/lib/gtk/lib"
    * PATH="/usr/lib/gtk/bin:$PATH"
  1. export CPPFLAGS LDFLAGS PKG\_CONFIG\_PATH LD\_LIBRARY\_PATH PATH
  1. ./configure (optional --prefix=/usr/lib/gtk)
  1. make
  1. make install (admin rights)
  1. ldconfig (so that the newly installed libraries will be found)

<b>Note: you may want to change /usr/lib/gtk path</b>