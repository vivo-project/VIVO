VIVO-languages
==============

Files that enable VIVO (supported by Vitro) to operate in languages beyond American English.

The repository is structured this way
* Each top-level directory represents a different release of VIVO.
* Within these, each Second-level directory contains files for a specific language, for that release. 
These directories are named for the "locale" of the language and region that they represent.

For example, a directory of files for the French language (fr), as used in Belgium (BE), for VIVO release 1.6:

    /vivo-1.6/fr_BE/

Available language files
------------------------

All members of the core development team speak only American English, 
so we have produced an example language set that holds the English-language strings
in VIVO:

    /vivo-1.6/en_US

This set of files is provided as an example, so those who wish to produce a translation
may have a template to copy.

Our first translation is in Spanish, contributed by Federico Sancho, of IICA (http://iica.int),
as part of a project with eScire (http://escire.mx/). We are very grateful to them for contributing 
this translation to the VIVO community.

    /vivo-1.6/es

Using the language files
------------------------

If the files you want already exist here, you can add them to your VIVO instance by
following the instructions in the VIVO wiki for [Adding a language to VIVO][1].

If the files for your desired language do not exist, then you may use this example as a 
starting point for doing the translations yourself. Please send a note to [the VIVO Tech group][2], 
to find out if someone else is already working on a translation.

If you create a translation, please consider contributing your language files to the VIVO community.

[1]: https://wiki.duraspace.org/display/VIVODOC19x/Internationalization
[2]: mailto:vivo-tech@googlegroups.com
