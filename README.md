Vaultwise Budget Tracker App

Welcome to Vaultwise, your smart and secure way to manage your budget and track your expenses. Vaultwise is designed with functionality, data persistence, and a user-friendly interface at its core,helping you take full control of your financial journey.

 	Demo Video:
[https://youtu.be/KxKAjwp79xQ]      OR 

 [https://drive.google.com/drive/folders/1fnK6hCdYosS9jndpb5l1-c8bdm67W0de]

 	GitHub Repository:
[https://github.com/KgaladiTumisoSekgoka/TeamCollaboration-Vaultwise]

Overview

Vaultwise allows users to securely log in and manage their finances with the ability to:
•	Log in with a unique username and password
•	Create custom categories for budgeting and expense tracking
•	Add expense entries with:
o	Date
o	Start & end times
o	Description
o	Category
o	Optional photograph
•	Set monthly minimum and maximum spending goals
•	View a list of expense entries filtered by a user-selectable date range
•	View photos attached to any expense entry
•	View total spending per category during a specific period



Prerequisites

To run the Vaultwise application locally, you need to have the following software installed on your PC:
1.	Android Studio (latest stable version)
2.	Java Development Kit (JDK 8+)
3.	Gradle
4.	Android SDK
5.	Emulator or Android device

Installation Guide

1.	Clone the repository:
https://github.com/KgaladiTumisoSekgoka/TeamCollaboration-Vaultwise.git
2.	Open in Android Studio
3.	Allow Gradle to sync all dependencies
4.	Connect an Android device or emulator
5.	Click Run > Run 'app'

Folder Structure

The Vaultwise project is organized as follows:
•	Vaultwise/ – Root directory of the project
o	app/ – Contains the core application module
	src/ – Source directory for app files
	main/ – Main source set
	java/com/vaultwise/ – This folder contains all the Java (or Kotlin) source code for the application, including activities, view models, and business logic.
	res/ – This folder contains all the XML resources such as layouts, drawables, strings, styles, and more.
	AndroidManifest.xml – The manifest file that defines app permissions, components, and configuration.
	build.gradle – The Gradle build script specific to the app module.
o	build.gradle – The top-level Gradle build script that defines global build configurations and dependencies.
o	settings.gradle – The Gradle settings file used to define the modules that are included in the project.

Database Structure

Vaultwise uses RoomDB  for local data persistence

Access Control

•	Users must log in with a valid username and password
•	Data is user-specific and securely stored
•	No data sharing between accounts

Logout Behaviour

•	Logout clears active session and returns user to the login screen
•	All in-memory sensitive data is cleared on logout

Pages and Functionalities

•	Login Page
o	User authentication via username and password
•	Dashboard
o	Overview of spending, current goals, and quick links
•	Add Expense
o	Add a new entry with date, time, description, category, and optional photo

•	Expense History
o	View all expenses for a selected time period
o	Tap entries to view photos if available
•	Category Breakdown
o	View total spent per category during a selected period
•	Monthly Goals
o	Set or update minimum and maximum spending goals
•	Settings/Profile
o	Manage account preferences

Dependencies

•	Android Jetpack (Room, ViewModel, Navigation, LiveData)
•	Material Design Components
•	Glide or Picasso (for image handling)
•	MPAndroidChart (for graph-based reports) 

Conclusion

Vaultwise is your go-to financial tracking tool—personal, powerful, and privacy-focused. With essential features like goal setting, photo attachments, and category summaries, Vaultwise turns budgeting into a breeze. Take control of your finances today with Vaultwise.
