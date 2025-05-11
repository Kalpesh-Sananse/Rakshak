# Rakshak - Women Safety Application

## Overview

**Rakshak** is an advanced mobile application designed to enhance women’s safety through real-time emergency alerts and intelligent response mechanisms. The app integrates voice-activated SOS, manual SOS triggers, real-time location tracking, and AI-powered emergency handling to provide swift assistance during distress situations. The application also extends support for senior citizens, ensuring assistance in medical or emergency conditions.

---

## Features

### 1. **Voice-Activated SOS**
   - Continuously listens for a predefined SOS keyword without storing audio.
   - Automatically triggers alerts and shares real-time location with emergency contacts and responders.

### 2. **Manual SOS Trigger**
   - Allows users to manually trigger an SOS by pressing a button.
   - Sends real-time location updates to emergency contacts, police, hospitals, and nurses.

### 3. **AI-Powered Emergency Handling**
   - Utilizes AI for analyzing distress situations and providing intelligent responses.
   - LLM models assist users with guidance during emergencies.

### 4. **Real-Time Location Sharing**
   - Shares the user’s live location with predefined emergency contacts and responders.
   - Clustering algorithm identifies and notifies the nearest responders (police, hospitals, nurses).

### 5. **Scalability for Senior Citizens**
   - Extended functionality for senior citizens, allowing them to call for help during medical emergencies.
   - A dedicated nurse application enables healthcare providers to receive alerts and respond efficiently.

---

## Technology Stack

- **IBM Cloud**: Backend services, AI model deployment, and cloud storage.
- **Firebase**: Real-time database, authentication, and cloud messaging.
- **Google Maps API**: Real-time location tracking.
- **Large Language Models (LLMs)**: Used for voice recognition and intelligent emergency responses.

---

## System Architecture

- **Mobile App**: Developed for Android (Kotlin) and Flutter (cross-platform).
- **Backend Services**: Deployed on IBM Cloud to handle user authentication, emergency alerts, and AI-based processing.
- **Real-Time Database & Notifications**: Managed using Firebase.
- **Admin Dashboard**: Web-based panel for monitoring and managing SOS alerts and responders.

---

## Deployment

- **Mobile Application**: Available on Google Play Store and iOS App Store.
- **Backend Services**: Hosted on IBM Cloud Functions for scalability and high availability.
- **Real-Time Database**: Firebase for instant SOS notifications.

---

## Future Scope

- **Public Camera Integration**: Deploy cameras in public spaces for automatic detection of distress situations.
- **Child and Senior Citizen Safety**: Expand functionality for children and senior citizens with unique safety features.
- **Advanced Language Support**: Integrate multilingual support for global accessibility.

---

## License

This project is licensed under the **MIT License**.
