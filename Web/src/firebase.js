import firebase from 'firebase/app'
import 'firebase/database'

// Put your firebase credentials here

let firebaseApp = firebase.initializeApp(firebaseConfig)
export default firebaseApp.database()
