VIVO-languages
==============

Files that enable VIVO (supported by Vitro) to operate in languages beyond American English.

The repository is structured this way
* Each top-level directory represents a different release of VIVO.
* Within these, each Second-level directory contains files for a specific langauge, for that release. 
These directories are named for the "locale" of the language and region that they represent.

For example, a directory of files for French (fr), as used in Belgium (BE), for VIVO release 1.6:

    /vivo-1.6/fr_BE/

All members of the core development team speak only American English, 
so the first set of files is an example of broken Spanish, as produced by the Google Translate service.
An imaginary locale has been invented to represent "Google Spanish":

    /vivo-1.6/es_GO
    
These files probably are not a valid Spanish translation for VIVO. However, 
they can be an example for anyone who will provide a more serious translation.

Using the language files
------------------------

If the files you want already exist here, you can add them to your VIVO instance by
following the instructions in the VIVO wiki for [Adding a language to VIVO][1].

If the files for your desired language do not exist, then you may use this example as a 
starting point for doing the translations yourself. Please send a note to [the developer's list][2],
or directly to the [VIVO release manager][3], to find out if someone else is already working
on a translation.

If you create a translation, please consider contributing your langauge files to the VIVO community.

[1]: https://wiki.duraspace.org/display/VIVO/Adding+a+language+to+VIVO
[2]: mailto:vivo-dev-all@lists.sourceforge.net
[3]: mailto:jeb228@cornell.edu
