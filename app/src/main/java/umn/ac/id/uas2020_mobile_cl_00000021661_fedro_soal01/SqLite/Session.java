package umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.SqLite;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public Session(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("mahasiswaque", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedin(boolean logins){
        editor.putBoolean("loggedinmode",logins);
        editor.commit();
    }
    public boolean loggin(){
        return prefs.getBoolean("loggedinmode",false);
    }
}
