import 'package:cgxtaskview/show.dart';
import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  FirebaseUser user;
  List data;

  @override
  void initState() {
    super.initState();
    FirebaseAuth.instance.currentUser().then((value) {
      setState(() {
        this.user = value;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
          alignment: Alignment.center,
          padding: EdgeInsets.all(20),
          child: user == null
              ? CircularProgressIndicator()
              : StreamBuilder(
                  initialData: null,
                  stream: FirebaseDatabase.instance
                      .reference()
                      .child(user.uid)
                      .child('projects')
                      .onValue,
                  builder: (ctx, snap) {
                    if (snap.hasData) {
                      Map data = snap.data.snapshot.value;
                      List dataList = [];
                      data.forEach((key, value) {
                        dataList.add(value);
                      });
                      return ListView.builder(
                          itemCount: data.length,
                          itemBuilder: (ctx, index) {
                            return Card(
                              elevation: 8.0,
                              child: ListTile(
                                onTap: () => Navigator.push(context,
                                    new MaterialPageRoute(builder: (ctx) {
                                  return ShowData(
                                      dataList[index].toString(), user.uid);
                                })),
                                title: Text(
                                    dataList[index].toString().toUpperCase()),
                              ),
                            );
                          });
                    }
                    return Text('No Project Added yet');
                  },
                )),
      appBar: AppBar(
        title: Text('CGX Taskview'),
        actions: <Widget>[
          IconButton(
            icon: Icon(Icons.exit_to_app),
            onPressed: () => FirebaseAuth.instance.signOut(),
          )
        ],
      ),
    );
  }
}
