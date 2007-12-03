AC_INIT(apoc-desktop-manager, 1.0)

AM_INIT_AUTOMAKE
AC_JAVA_OPTIONS

AC_EXPAND(datadir, NONE, EXPANDED_DATADIR)
AC_EXPAND(prefix, NONE, EXPANDED_PREFIX)

dnl ---------------
dnl -- APOC Path --
dnl ---------------

APOCPATH=$EXPANDED_DATADIR'/lib/apoc'
AC_ARG_WITH(apoc-path,
AC_HELP_STRING( [--with-apoc-path=DIR],
		[specify a path to install the apoc libraries (DATADIR/lib/apoc)]),
[
	if test "$withval" != "no"
	then
		APOCPATH="$withval"
	fi
])

dnl -- Adding the SPI to the classpath --

SPIJDK=$APOCPATH/spi.jar
CLASSPATH=$CLASSPATH:$SPIJDK

dnl -----------------------
dnl -- Checking for JATO --
dnl -----------------------

JATOJDK=$EXPANDED_DATADIR/lib/jato/jato.jar
AC_ARG_WITH(jato-path,
AC_HELP_STRING( [--with-jato-path=DIR],
		[specify the JATO JDK path or jar file (DATADIR/lib/jato/jato.jar)]),
[
	if test "$withval" != "no"
	then
		JATOJDK=$withval
	fi
])
CLASSPATH=$CLASSPATH:$JATOJDK

LDAPJDK=$EXPANDED_DATADIR/lib/apoc/ldapjdk.jar
AC_ARG_WITH(ns-ldap-path,
AC_HELP_STRING( [--with-ns-ldap-path=DIR],
		[specify the LDAP JDK path or jar file (DATADIR/lib/apoc/ldapjdk.jar)]),
[
	if test "$withval" != "no"
	then
		LDAPJDK=$withval
	fi
])

WCROOT="$prefix"/share/webconsole
AC_ARG_WITH(webconsole-prefix,
AC_HELP_STRING( [--with-webconsole-prefix=DIR],
		[specify the webconsole installation path (PREFIX)]),
[
	if test "$withval" != "no"
	then
		WCROOT="$withval"
		PATH=$PATH:"$withval"/sbin
	fi
])
APOC_WCROOT=$WCROOT/apoc
WC_LIBPATH=$WCROOT/lib
WC_PRIVLIBS=$WCROOT/private/container/common/lib
CLASSPATH=$CLASSPATH:$WC_PRIVLIBS/servlet-api.jar
PATH=$PATH:$WCROOT/bin

AC_CHECK_PROG(WCADMIN, wcadmin, wcadmin, no)
if test $WCADMIN == "no"
then
	AC_MSG_ERROR(Java Web Console not found.
If it is already installed plase use --with-webconsole-prefix=DIR to point to the path where the webconsole files are stored.)
fi

AC_PROG_JAVAC
AC_PROG_JAVA
AC_PROG_JAR

AC_CHECK_PROG(ANT, ant, ant, no)
if test $ANT == "no"
then
	AC_MSG_ERROR(Apache's Ant was not found in path.)
fi

export CLASSPATH
AC_CHECK_CLASS(com.sun.apoc.spi.PolicyManager, [], [
AC_MSG_ERROR(
APOC SPI not found in Classpath
If it is already installed please use --with-apoc-path=DIR to point to the path where the spi.jar file is stored.)
])

AC_CHECK_CLASS(javax.servlet.ServletException, [], [
AC_MSG_ERROR(
Servlet classes not found in Classpath.
The Java Web Console is not correctly isntalled. Please use --with-webconsole-prefix=DIR to poont to a correct webconsole installation.)
])

AC_CHECK_CLASS(com.iplanet.jato.ModelManager, [], [
AC_MSG_ERROR(
Jato classes not found in Classpath.
If it is already installed please use --with-jato-path=DIR to point to the path or jar file.)
])

dnl Workaround to override the SPI manifest Class-Path
dnl For some reason, ant ignores the manifest attr
export CLASSPATH=".:..:$LDAPJDK"

AC_CHECK_CLASS(netscape.ldap.LDAPConnection, [], [
AC_MSG_ERROR(
Mozilla/Netscape LDAP JDK classes not found in Classpath.
If it is already installed please use --with-ns-ldap-path=DIR to point to the path or jar file.)
])


AC_SUBST(SPIJDK)
AC_SUBST(WC_LIBPATH)
AC_SUBST(WC_PRIVLIBS)
AC_SUBST(JATOJDK)
AC_SUBST(LDAPJDK)

AC_SUBST(APOC_WCROOT)

AC_OUTPUT([
Makefile
src/Makefile
src/com/Makefile
src/com/sun/apoc/templates/Makefile
src/com/sun/apoc/templates/build.xml
src/com/sun/apoc/Makefile
src/com/sun/apoc/manager/Makefile
src/com/sun/apoc/manager/build.xml
src/com/sun/Makefile
webconsole/html/zh_CN/Makefile
webconsole/html/zh_CN/help/JavaHelpSearch/Makefile
webconsole/html/zh_CN/help/Makefile
webconsole/html/zh_TW/Makefile
webconsole/html/zh_TW/help/JavaHelpSearch/Makefile
webconsole/html/zh_TW/help/Makefile
webconsole/html/ja/Makefile
webconsole/html/ja/help/JavaHelpSearch/Makefile
webconsole/html/ja/help/Makefile
webconsole/html/de/help/Makefile
webconsole/html/de/help/JavaHelpSearch/Makefile
webconsole/html/de/Makefile
webconsole/html/Makefile
webconsole/html/ko/Makefile
webconsole/html/ko/help/Makefile
webconsole/html/ko/help/JavaHelpSearch/Makefile
webconsole/html/images/Makefile
webconsole/html/en/help/css/Makefile
webconsole/html/en/help/graphics/Makefile
webconsole/html/en/help/ents/Makefile
webconsole/html/en/help/Makefile
webconsole/html/en/help/JavaHelpSearch/Makefile
webconsole/html/en/Makefile
webconsole/html/fr/Makefile
webconsole/html/fr/help/Makefile
webconsole/html/fr/help/JavaHelpSearch/Makefile
webconsole/html/es/help/Makefile
webconsole/html/es/help/JavaHelpSearch/Makefile
webconsole/html/es/Makefile
webconsole/jsp/Makefile
webconsole/Makefile
])
