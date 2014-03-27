Archimedes Metadata Rules Analyzer v0.4 (MArch 19 2003)

What is Archimedes?
	This is an open source utility (see License.html) based on the Automation Analyzer 
	work from Wim Wandermassen, Max Tardiveau, and Tyler Band.

	Band Software Design, LLC has added additional set of Java classes and
	utilities to take the output of the Automation Analyzer and persist it to
	a Versata Logic Server (see MetaRepos).

How do I install it?
	Simply download and unzip the files to a directory on your hard disk.
	You will need to modify these 2 files:
		Archimedes.bat 
		Archimedes.properties  

	These contain class path settings to Java home
	and Versata Home.
	[Optional] The directory \MetaRepos contains a Versata 5.5.x application that
	can be deployed to a Versata Transaction Logic Engine (aka. VLS).  You
	will also need to open Versata and deploy the SQL to your DBMS and configure
	the database properties in the VLS Console.

How do I run Archimedes?
	If the bat file is setup correctly - Archimedes.bat will launch a GUI 
	window.  The file menu will allow you to point to an existing Versata 5.5.x
	repository.  After completion, it will display statistics and messages on 
	the GUI window.  You can persist this to an HTML report (which is Excel
	loadable).  You can also deploy this version to a running VLS (the properties
	are setup in Archimedes.properties).

What can I do with this infomration?
	Archimedes contains the entire Versata XML repository content.  This data
	is now persisted to a Versata runtime server and an SQL DBMS.  You can
	generate reports, write runtime applications (an HTML and Java sample 
	are included), use the statistics to manage projects (e.g. how many
	lines of code or methods did this group generate in the last release).
	You can also write transformations - that is - create new output from existing
	metadata.  Finally - you can now access runtime metadata.  For example - if 
	you need to get the caption from attribute to use in your exception handler,
	simply copy some of the MetaRepos XML objects to your repository (Repository,
	DataObject, Attribute) and then you can read these values at runtime. OR
	you could write a few lines of sample java code (see ReposDiff) to create
	an external resource bundle for each DataObject/Attribute.

	There is a new feature that includes a Repository Difference engine - this
	will compare 2 different versions and tell you what has been added/removed/
	or changed.


Who can I contact for support?

	Band Software Design, LLC
	Tyler@Bandsoftware.com

Directories
	\help			contains HTML pages used by online help
	\docs			contains Java doc for source code and an Archimedes report 
				of the MetaRepos
	\images			images used by Archimedes and HTML pages
	\libs			contains the JAR files used by Archimedes
	\src			contains the source .java files for Archimedes
	\MetaRepos		Versata 5.5.x application used to persist Archimedes statistics

=====================================================================================
	
please report any issues to tyler@bandsoftware.com