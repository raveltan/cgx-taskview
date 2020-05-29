import firebase from 'firebase/app'
import 'firebase/database'

let firebaseConfig = {
  apiKey: 'AIzaSyDIL0Ip-z3DwSx2X0KaCOdPbcOK2SmqjtQ',
  authDomain: 'taskview-6358e.firebaseapp.com',
  databaseURL: 'https://taskview-6358e.firebaseio.com',
  projectId: 'taskview-6358e',
  storageBucket: 'taskview-6358e.appspot.com',
  messagingSenderId: '395983725951',
  appId: '1:395983725951:web:fc1986de235aec9b69264a',
}

let firebaseApp = firebase.initializeApp(firebaseConfig)
export default firebaseApp.database()
