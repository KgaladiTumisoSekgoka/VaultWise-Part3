Vaultwise Android App
Vaultwise is a secure personal finance management app that allows users to track income, expenses, and budget goals. The app uses Firebase Authentication for secure login and Room Database for local data storage. It also includes features like budget tracking, data visualization (charts), and spending alerts.

Features
User Authentication via Firebase

Local data storage using Room Database

Budget tracking with spending alerts

Data visualization with charts and progress bars

Prerequisites
Android Studio: Ensure you have Android Studio installed. If you don't have it, download and install it from the official site: Android Studio.

Java: Make sure you have the correct version of Java (JDK 11 or higher) installed on your machine.

Steps to Run the Application
Clone the Repository
First, clone the repository to your local machine using the following command:

bash
Copy
Edit
git clone https://github.com/your-username/vaultwise-android-app.git
Replace your-username with your actual GitHub username if needed.

Open the Project in Android Studio

Open Android Studio.

Click on Open an existing project.

Navigate to the folder where you cloned the repository and select it.

Install Dependencies

After opening the project, Android Studio will automatically prompt you to install any required dependencies.

If prompted, click on Install to install the necessary Gradle dependencies.

You can also manually sync the project with Gradle by going to File → Sync Project with Gradle Files.

Configure Firebase
To use Firebase Authentication and other Firebase services:

Go to Firebase Console.

Create a new project (if you don't have one already).

Follow the steps to set up Firebase Authentication.

Download the google-services.json file from the Firebase Console and place it in the app/ folder of your project.

Run the Application

Once all dependencies are installed and Firebase is set up, click on the Run button in Android Studio.

Select a device (either a connected physical device or an emulator) and run the app.

Test the App

Once the app is installed on the device or emulator, you can start using it by creating an account via Firebase Authentication and entering your income, expenses, and budget goals.
