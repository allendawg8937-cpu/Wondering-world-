// src/app/core/auth/firebase.config.ts
import { initializeApp, getApps, getApp, FirebaseApp } from 'firebase/app';
import { getAuth, Auth } from 'firebase/auth';

export const firebaseConfig = {
  projectId: 'gen-lang-client-0751970896',
  appId: '1:1040803210617:android:b916d2a6497a0bdf9ebba8',
  storageBucket: 'gen-lang-client-0751970896.firebasestorage.app',
  apiKey: 'AIzaSyD376x6HQw_WqfbDjG1SukMEPbrwajgyQA',
  authDomain: 'gen-lang-client-0751970896.firebaseapp.com',
};

export function getFirebaseApp(): FirebaseApp {
  return getApps().length > 0 ? getApp() : initializeApp(firebaseConfig);
}

export function getFirebaseAuth(): Auth {
  return getAuth(getFirebaseApp());
}
