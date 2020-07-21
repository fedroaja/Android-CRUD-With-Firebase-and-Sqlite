package umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class HalamanProfile extends AppCompatActivity {
    Switch aSwitch;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_profile);

        this.getSupportActionBar().setTitle("About me");
        this.getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_1));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        aSwitch = (Switch) findViewById(R.id.swithcMode);
        relativeLayout = (RelativeLayout) findViewById(R.id.profile_halaman);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    relativeLayout.setBackgroundResource(R.drawable.black);
                }else{
                    relativeLayout.setBackgroundResource(R.drawable.gradient_1);
                }
            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
