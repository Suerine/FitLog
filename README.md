# 🏋️ Fitness Tracker App

A modern Android fitness tracking application built with **Java and Android Studio** that helps users discover workouts, track progress, and monitor important health metrics.

The application provides an intuitive interface with **workout discovery, analytics tools, workout tracking, and user profile management**.

---

## 📱 App Preview

| Home | Workouts | Analytics | Profile |
|-----|-----|-----|-----|
| Screenshot | Screenshot | Screenshot | Screenshot |

Example screenshot paths:
/screenshots/home.png
/screenshots/workouts.png
/screenshots/analytics.png
/screenshots/profile.png


---

# 🚀 Features

## 🏠 Workout Hub

The Home screen displays different workouts users can perform.

Features include:

- Workout cards with images  
- Exercise titles  
- Short workout descriptions  
- Scrollable workout grid  
- Quick navigation between workouts  

Example workouts:

- Push Ups  
- Squats  
- Plank  
- Cardio Training  
- Strength Training  
- Core Workouts  

---

## 📊 Analytics Tools

The app includes useful tools to monitor health and performance.

### Tools Available

✔ **BMI Calculator**

Allows users to calculate Body Mass Index using height and weight.

✔ **Calorie Counter**

Helps estimate calories burned during workouts.

✔ **Water Intake Tracker**

Encourages proper hydration throughout the day.

✔ **Step Counter**

Tracks daily steps and activity levels.

---

## 📈 Workout Tracking

Users can track their fitness journey through:

- Workout history  
- Days worked out  
- Activity tracking  
- Workout streaks  

---

## 👤 Profile System

The profile page displays user fitness information including:

- Profile picture  
- Current weight  
- Fitness goals  
- Total workouts completed  
- Workout streak  

---

## ⚙️ Settings

Allows users to customize their app experience:

- Account settings  
- Profile updates  
- Preferences  

---

## 🧭 Navigation

The app uses a **Bottom Navigation Bar** for smooth navigation between pages.

Navigation sections include:

- 🏠 Home  
- 📈 Track  
- 👤 Profile  
- ⚙️ Settings  

Navigation is handled using **Fragments inside MainActivity**.

---

# 🏗️ Architecture

The project follows a **Fragment-based architecture**, keeping the application modular and scalable.

### Main Components
MainActivity

│

├── HomeFragment

├── TrackFragment

├── ProfileFragment

└── SettingsFragment


---

# 🛠️ Built With

- Java  
- Android Studio  
- XML Layouts  
- Material Design Components  
- ViewBinding  
- Fragments  
- RecyclerView  
- CardView  

---

# 📂 Project Structure
fitness-tracker-app

│

├── activities

│ └── MainActivity.java

│

├── fragments

│ ├── HomeFragment.java

│ ├── TrackFragment.java

│ ├── ProfileFragment.java

│ └── SettingsFragment.java

│

├── adapters

│ └── WorkoutAdapter.java

│
├── models

│ └── Workout.java

│

├── res

│ ├── layout

│ ├── drawable

│ ├── menu

│ └── values

---

# 📱 Android App Project

This is an Android application developed using Android Studio.

## 🚀 Getting Started with Android Studio

Follow these instructions to import this project into Android Studio directly from GitHub.

---

## 🧰 Prerequisites

Make sure you have the following installed:

- ✅ [Android Studio](https://developer.android.com/studio) (latest version recommended)
- ✅ Git installed on your system

---

## 📥 Import Project from GitHub

To import this project into Android Studio:

1. Open **Android Studio**
2. Go to **File > New > Project from Version Control**
3. Select **Git**
4. In the **URL field**, paste the following repository link: (https://github.com/Suerine/FitLog.git)
5. Choose a directory to save the project locally
6. Click **Clone**

---

## ⚙️ Building the Project

Once the project is cloned:

1. Android Studio will automatically start syncing the Gradle files
2. Wait for the sync to complete and dependencies to install
3. If prompted, install any missing SDK components
4. Select a device or emulator
5. Click **Run ▶️** or press **Shift + F10** to build and launch the app

---

## 🛠 Troubleshooting

- Use `File > Sync Project with Gradle Files` if Gradle fails to sync
- Use `Build > Clean Project` and `Build > Rebuild Project` if build issues occur
- Ensure you are connected to the internet to download all dependencies

---

## 🤝 Contributing

Feel free to fork this repository, open issues, or submit pull requests.

