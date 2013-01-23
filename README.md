MOTECH Pill Reminder Demo
=========================

This project aims to demonstrate a number of modules within the MOTECH Suite.

*Use Case*: Enroll Patients into a Pill Reminder schedule such that, that will receive a call prompting them whether they have taken their medication dosage. The response is captured and saved within an OpenMRS instance.

MOTECH Modules
--------------

The following is a list of MOTECH modules used in this demo:

*  Commcare
*  Decision Tree
*  IVR (Verboice)
*  MRS (OpenMRS)
*  Pill Reminder

Build
-----

To build the project: mvn clean install

Running the demo
----------------

This demo works against the latest release of MOTECH (0.17). Unfortunately, there were a few modifications to the core MOTECH codebase to make this demo work. You will need to clone the MOTECH repo, and apply the patch in this repo labeled changes-0.17.patch.

Once you have deployed a patched MOTECH, modify the configuration files indicated by config-file-changes.txt
