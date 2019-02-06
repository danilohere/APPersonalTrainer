package appersonal.development.com.appersonaltrainer.Activities;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import appersonal.development.com.appersonaltrainer.R;

public class AguaActivity extends AppCompatActivity {

    private static final String AGUA = "Agua";

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agua);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnOK = findViewById(R.id.btnOk);
        TextView txtAgua = findViewById(R.id.txtAgua);

        SharedPreferences agua = getSharedPreferences(AGUA, Context.MODE_PRIVATE);
        int vezes = agua.getInt("Agua", 0);

        Intent intent = getIntent();
        Bundle extra;
        extra = intent.getExtras();
        if (extra != null) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert manager != null;
            manager.cancel(extra.getInt("notificationId"));
            if (Objects.equals(extra.getString("Status"), "Tomei")) {
                vezes = vezes + 1;
                txtAgua.setText(vezes + "x ");
                SharedPreferences.Editor editor = agua.edit();
                editor.putInt("Agua", vezes);
                editor.apply();
            }
        }

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAndRemoveTask();
                } else {
                    finishAffinity();
                }
            }
        });
    }
}
