# 🚨 Rakshak - Women Safety Application

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/)
[![Made with Kotlin](https://img.shields.io/badge/Made%20with-Kotlin-blueviolet)](https://kotlinlang.org/)
[![Backend: IBM Cloud](https://img.shields.io/badge/Backend-IBM%20Cloud-blue)](https://www.ibm.com/cloud)
[![Firebase](https://img.shields.io/badge/Realtime%20Database-Firebase-orange)](https://firebase.google.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## Overview

**Rakshak** is an advanced mobile application designed to enhance women’s safety through real-time emergency alerts and intelligent response mechanisms. The app integrates voice-activated SOS, manual SOS triggers, real-time location tracking, and AI-powered emergency handling to provide swift assistance during distress situations. The application also extends support for senior citizens, ensuring assistance in medical or emergency conditions.

---

## Features

### 🚨 **Voice-Activated SOS**
   - Continuously listens for a predefined SOS keyword without storing audio.
   - Automatically triggers alerts and shares real-time location with emergency contacts and responders.

### 🆘 **Manual SOS Trigger**
   - Allows users to manually trigger an SOS by pressing a button.
   - Sends real-time location updates to emergency contacts, police, hospitals, and nurses.

### 🤖 **AI-Powered Emergency Handling**
   - Utilizes AI for analyzing distress situations and providing intelligent responses.
   - LLM models assist users with guidance during emergencies.

### 📍 **Real-Time Location Sharing**
   - Shares the user’s live location with predefined emergency contacts and responders.
   - Clustering algorithm identifies and notifies the nearest responders (police, hospitals, nurses).

### 👵 **Scalability for Senior Citizens**
   - Extended functionality for senior citizens, allowing them to call for help during medical emergencies.
   - A dedicated nurse application enables healthcare providers to receive alerts and respond efficiently.

---

## Technology Stack

- 🌐 **IBM Cloud**: Backend services, AI model deployment, and cloud storage.
- ⚡ **Firebase**: Real-time database, authentication, and cloud messaging.
- 🗺️ **Google Maps API**: Real-time location tracking.
- 🧠 **Large Language Models (LLMs)**: Used for voice recognition and intelligent emergency responses.

---

## System Architecture

- 📱 **Mobile App**: Developed for Android (Kotlin) and Flutter (cross-platform).
- ☁️ **Backend Services**: Deployed on IBM Cloud to handle user authentication, emergency alerts, and AI-based processing.
- 🔔 **Real-Time Database & Notifications**: Managed using Firebase.
- 🌐 **Admin Dashboard**: Web-based panel for monitoring and managing SOS alerts and responders.

---

### App Screenshots:

<div style="display: flex; flex-wrap: wrap; justify-content: space-around;">
  <img src="https://github.com/user-attachments/assets/a24715f4-f58c-4d7f-b12c-bffada726f6a" width="200" height="400" alt="Screen 1">
  <img src="https://github.com/user-attachments/assets/17ed4c05-daef-419d-b1d8-ef6b0bba69cd" width="200" height="400" alt="Screen 2">
  <img src="https://github.com/user-attachments/assets/927885c7-28e8-4751-b6e6-2e4b1ebcd822" width="200" height="400" alt="Screen 3">
  <img src="https://github.com/user-attachments/assets/d611309b-80d5-4371-b50c-4979636ae3ab" width="200" height="400" alt="Screen 4">
  <img src="https://github.com/user-attachments/assets/9a431239-d5ab-4c1d-92ab-d9af9f176f60" width="200" height="400" alt="Screen 5">
</div>

<div style="display: flex; flex-wrap: wrap; justify-content: space-around;">
  <img src="https://github.com/user-attachments/assets/7b665fb1-20a3-46e0-a108-f5d51cefa2d9" width="200" height="400" alt="Screen 6">
  <img src="https://github.com/user-attachments/assets/daff908c-4d28-42c3-a6d6-45d6a90bae4d" width="200" height="400" alt="Screen 7">
  <img src="https://github.com/user-attachments/assets/a1817a96-f45c-497d-aa3b-d092fc8bb8f4" width="200" height="400" alt="Screen 8">
</div>

## Deployment

- 📱 **Mobile Application**: Available on Google Play Store and iOS App Store.
- ☁️ **Backend Services**: Hosted on IBM Cloud Functions for scalability and high availability.
- ⚡ **Real-Time Database**: Firebase for instant SOS notifications.

---

## Future Scope

- 🎥 **Public Camera Integration**: Deploy cameras in public spaces for automatic detection of distress situations.
- 👶👵 **Child and Senior Citizen Safety**: Expand functionality for children and senior citizens with unique safety features.
- 🌍 **Advanced Language Support**: Integrate multilingual support for global accessibility.

---

## License

This project is licensed under the **MIT License**.
