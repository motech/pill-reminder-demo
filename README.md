MOTECH Pill Reminder Demo
=========================

This project aims to demonstrate a number of modules within the MOTECH Suite.

*Use Case*: Enroll Patients into a Pill Reminder schedule such that, that will receive a call prompting them whether 
they have taken their medication dosage. The response is captured and saved within an OpenMRS instance.

MOTECH Modules
--------------

The following is a list of MOTECH modules used in this demo:

*  Commcare
*  Decision Tree
*  IVR (Verboice)
*  MRS (OpenMRS)
*  Pill Reminder

Get the Pill Reminder Demo Code
-------------------------------

    $ git clone git@github.com:motech/pill-reminder-demo.git

Build Demo Project
------------------

To build the project:

    $ mvn clean install

Import Demo Project into Eclipse
--------------------------------

You can import the pill reminder demo into Eclipse. First, generate the Eclipse project files:

    $ mvn eclipse:eclipse

Then, import the project within Eclipse:

    File -> Import... -> [General] Existing Projects into Workspace -> Browse for the Pill Reminder Folder -> Finish

Custom MOTECH Release
---------------------

This demo works against a custom release of MOTECH 0.17. There were a number of minor changes that had to made in order 
for this demo to work properly. We anticipate most of these changes will be incorporated back into MOTECH proper in a future release.

Getting Custom MOTECH Release
-----------------------------

There is a fork of the MOTECH repo here https://github.com/motech/motech-server-pillreminder

The branch 0.17-pr-X is based off the MOTECH 0.17 release. We use a qualifier in the version to distinguish this is a 
a custom release of MOTECH 0.17.

You can clone this repository if you need to make further changes:

    $ git clone git@github.com:motech/motech-server-pillreminder.git

Demo Deployment Instructions
----------------------------

This demo requires the following software to be available and configured:

* Verboice
* OpenMRS 1.8.4
* Commcare

In addition to these pieces of software, we assume you have the necessary software for MOTECH: ActiveMQ, CouchDB and Tomcat.

To be continued...
