import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';

class ShowData extends StatefulWidget {
  final String title;
  final String uid;

  ShowData(this.title, this.uid);

  @override
  _ShowDataState createState() => _ShowDataState();
}

class _ShowDataState extends State<ShowData> {
  int _tabIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Container(
        margin: EdgeInsets.all(20),
        child: StreamBuilder(
          initialData: null,
          stream: FirebaseDatabase.instance
              .reference()
              .child(widget.uid)
              .child(widget.title)
              .child(_tabIndex == 0
                  ? 'planned'
                  : _tabIndex == 1 ? 'progress' : 'done')
              .onValue,
          builder: (ctx, snap) {
            if (snap.hasData) {

              List dataList = snap.data.snapshot.value;

              return dataList == null
                  ? Text('No Data')
                  : ListView.builder(
                      itemCount: dataList.length,
                      itemBuilder: (ctx, index) {
                        return Card(

                          elevation: 8.0,
                          child: ListTile(
                            onTap: () => null,
                            title: Text(dataList[index].toString().toUpperCase()),
                          ),
                        );
                      });
            }
            return Text('No Project Added yet');
          },
        ),
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: this._tabIndex,
        onTap: (i) {
          setState(() {
            this._tabIndex = i;
          });
        },
        items: [
          BottomNavigationBarItem(
              icon: Icon(Icons.note_add), title: Text('Planned')),
          BottomNavigationBarItem(
              icon: Icon(Icons.error), title: Text('On Progress')),
          BottomNavigationBarItem(icon: Icon(Icons.check), title: Text('Done'))
        ],
      ),
    );
  }
}
