package bobo.shanche.dbDo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import bobo.shanche.jsonDo.Station;

/**
 * Created by bobo1 on 2016/7/10.
 */
public class DbHelper extends SQLiteOpenHelper {

    final static private String DbName = "stBus.db";
    final static private String DbTable_C = "collection";
    final static private String DbTable_R = "record";
    final static private String DbTable_S = "settings";
    final static private int Version = 1;

    public DbHelper(Context context) {
        super(context, DbName, null,Version );

    }


    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE IF NOT EXISTS " + DbTable_R +
                " (" +
                "_id integer PRIMARY KEY," +
                "busID text," +
                "lineName text," +
                "startSite text," +
                "endSite text," +
                "upDown integer" +
                ")";
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + DbTable_C +
                " (" +
                "_id integer PRIMARY KEY," +
                "busID text," +
                "lineName text," +
                "startSite text," +
                "endSite text," +
                "upDown integer" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void add(String tableName, Station station){
        ContentValues values = new ContentValues();
        values.put("busID",station.getLineId());
        values.put("lineName",station.getLineName());
        values.put("startSite",station.getList().get(0).getSiteName());
        values.put("endSite",station.getList().get(station.getList().size()-1).getSiteName());
        values.put("upDown",station.getUpDown());
        this.getWritableDatabase().insert(tableName,null,values);
    }
    public void delete(String tableName,String lineName){
        this.getWritableDatabase().delete(tableName,"lineName='"+lineName+"'",null);
    }
    public Cursor searchAll(String tableName ){
        Cursor cursor= this.getReadableDatabase().query(tableName,null,null,null,null,null,"_id DESC limit");
        return cursor;
    }

    public Cursor search(String tableName,String selection){
        Cursor cursor= this.getReadableDatabase().query(tableName,null,selection,null,null,null,"_id DESC");

        return cursor;
    }
    public void deleteUpDown(String tableName,String lineName,int upDown){
        this.getWritableDatabase().delete(tableName,"lineName=? and upDown=?",new String[]{lineName,Integer.toString(upDown)});
    }

}
