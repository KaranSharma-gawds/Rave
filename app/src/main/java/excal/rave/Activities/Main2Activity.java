package excal.rave.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import excal.rave.R;

public class Main2Activity extends AppCompatActivity {
    Button createParty, joinParty;
    private static int partyCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        createParty = (Button) findViewById(R.id.create_party);
        joinParty = (Button) findViewById(R.id.join_party);
        final Intent createGroupIntent = new Intent(Main2Activity.this,Party.class);
        createParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroupIntent.putExtra("ROLE","MASTER");
                reseting();
                startActivity(createGroupIntent);
            }
        });
        joinParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroupIntent.putExtra("ROLE","SLAVE");
                startActivity(createGroupIntent);
            }
        });
    }

    private void reseting() {
        partyCount++;
        if(partyCount>1)
            Party.closeSockets();
    }

}
