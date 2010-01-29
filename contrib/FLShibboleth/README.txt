--------------------------------------------------------------------------------
Plugin Details
--------------------------------------------------------------------------------
Name:       ShibAuth
Version:    0.1
Date:       01-26-2010
Authors:    Chris Barnes  (cpb@ichp.ufl.edu)
            Narayan Raum  (ndr@ichp.ufl.edu)
            Yang Li       (yxl@ichp.ufl.edu)
Support:    http://ctrip.ufl.edu/contact
        
--------------------------------------------------------------------------------
Plugin Description
--------------------------------------------------------------------------------
Adding Shibboleth authentication to the Vitro application. An example of how
ShibAuth . This package serves as an example of how ShibAuth has been 
implemented at the University of Florida. In order to use this plugin, your
institution must have a Shibboleth Identity Provider established. For more
information about Shibboleth, visit http://shibboleth.internet2.edu/.

--------------------------------------------------------------------------------
Plugin Installation
--------------------------------------------------------------------------------
Detailed instructions and documentation are available in the INSTALL.pdf
document. All files in the "includes" directory must be uploaded to the
server running Vitro in order to complete the installation procedure.

This procedure is an example installation of a Shibboleth 2 Service Provider on 
a Linux (Debian Lenny) system. All commands were executed as the root user. 
In this example, the following applications have already been installed 
and configured:

    - OpenSSL
    - Apache 2
    - Tomcat 6
    - Vitro

The ShibAuth plugin allows a Vitro system administrator to authenticate using 
the Shibboleth Service Provider. It is assumed that the user already has an 
account in the “Users” table of the database. The field name for the user at 
UF is the “glid” field.
