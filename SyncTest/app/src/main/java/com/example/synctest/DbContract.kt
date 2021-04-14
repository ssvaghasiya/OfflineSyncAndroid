package com.example.synctest

class DbContract {

    var SYNC_STATUS_OK = 0;
    var SYNC_STATUS_FAILED = 1;
    var SERVER_URL: String = "http://192.168.0.7/syncdemo/syncinfo.php"
    var UI_UPDATE_BROADCAST: String = "com.example.synctest.uiupdatebroadcast"

    var DATABASE_NAME = "contactdb"
    var TABLE_NAME = "contactinfo"
    var NAME = "name"
    var SYNC_STATUS = "syncstatus"

}