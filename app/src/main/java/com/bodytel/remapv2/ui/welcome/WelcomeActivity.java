package com.bodytel.remapv2.ui.welcome;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.remapv2.ui.debug.DebugActivity;

public class WelcomeActivity extends AppCompatActivity {

    private TextView txtSubjectId;

    private PrefGlobal prefGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        txtSubjectId = findViewById(R.id.txt_subject_id);

        prefGlobal = PrefHelper.providePrefGlobal();

        String subjectId = prefGlobal.getSubjectId();
        if(TextUtils.isEmpty(subjectId)) inputSubjectId();
        else {
            txtSubjectId.setText(subjectId);
        }
    }

    public void onClickDebug(View view){
        //Toast.makeText(this, "Debug", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, DebugActivity.class));
    }

    private void inputSubjectId(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.prompt_input_subject_id, null);

        final EditText etSubjectId = view.findViewById(R.id.prompt_input_subject_id_et_subject_id);
        TextView txtOk = view.findViewById(R.id.prompt_input_subject_id_txt_ok);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subjectId = etSubjectId.getText().toString();

                if(TextUtils.isEmpty(subjectId))
                    Toast.makeText(WelcomeActivity.this, "Please, insert a valid subject ID", Toast.LENGTH_SHORT).show();
                else {
                    prefGlobal.setSubjectId(subjectId);
                    prefGlobal.setBdiVersion("1.0");
                    txtSubjectId.setText(subjectId);
                    dialog.dismiss();
                }
            }
        });


        dialog.show();
    }
}
