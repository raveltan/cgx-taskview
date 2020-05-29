import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class Login extends StatefulWidget {
  @override
  _LoginState createState() => _LoginState();
}

class _LoginState extends State<Login> {
  var emailController = new TextEditingController();
  var passwordController = new TextEditingController();
  bool loading = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('CGX Taskview'),

      ),
      body:  Container(
        padding: EdgeInsets.all(30),
        alignment: Alignment.center,
        child: loading ? CircularProgressIndicator() :Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            Text('Login',style: TextStyle(
                fontSize: 30,
                fontWeight: FontWeight.w700,
                color: Colors.black.withOpacity(0.75)
            ),),
            SizedBox(height:20),
            TextField(
              decoration: InputDecoration(
                  border: OutlineInputBorder(),
                  filled: true,
                  hintText: 'John@cgxmail.com',
                  labelText: 'Email'
              ),
              autocorrect: false,
              autofocus: true,
              controller: emailController,
            ),
            SizedBox(height:10,),
            TextField(
              decoration: InputDecoration(
                  border: OutlineInputBorder(),
                  filled: true,
                  labelText: 'Password'
              ),autocorrect: false,
              autofocus: false,
              obscureText: true,
              controller: passwordController,
            ),
            SizedBox(height:10,),
            RaisedButton(
              onPressed: (){
                if(emailController.text != "" && passwordController.text!=""){
                  setState(() {
                    loading = true;
                  });
                  FirebaseAuth.instance.signInWithEmailAndPassword(
                      email: emailController.text,
                      password: passwordController.text).catchError((e){
                        setState(() {
                          loading = false;
                        });
                    showDialog(context: context,child: AlertDialog(
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                      title: Text("Error"),
                      content: Text(e.message),
                      actions: <Widget>[
                        RaisedButton(
                          color:Colors.blue,
                          child: Text('Ok',style: TextStyle(color:Colors.white),),
                          onPressed: ()=>Navigator.pop(context),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                        )
                      ],
                    ));
                  });
                }else{
                  showDialog(context: context,child: AlertDialog(
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                    title: Text("Error"),
                    content: Text("Email and Password may not be empty"),
                    actions: <Widget>[
                      RaisedButton(
                        color:Colors.blue,
                        child: Text('Ok',style: TextStyle(color:Colors.white),),
                        onPressed: ()=>Navigator.pop(context),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                      )
                    ],
                  ));
                }
              },
              child: Text('Login',style:TextStyle(
                  color: Colors.white
              )),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
              color: Colors.blue,
            )
          ],
        ),
      ),
    );
  }
}
