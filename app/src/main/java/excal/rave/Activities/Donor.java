package excal.rave.Activities;

import android.os.Bundle;

import excal.rave.Assistance.Party;
import excal.rave.R;

public class Donor extends Party {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);
    }
}
