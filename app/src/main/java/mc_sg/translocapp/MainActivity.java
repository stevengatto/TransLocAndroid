package mc_sg.translocapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import mc_sg.translocapp.model.Agency;
import mc_sg.translocapp.model.Response;
import mc_sg.translocapp.network.NetworkUtil;

public class MainActivity extends AppCompatActivity {

    Button btnGet;
    ListView responseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGet = (Button) findViewById(R.id.btn_get);
        responseList = (ListView) findViewById(R.id.lv_response);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkUtil.getTransLocApi().getAgencies(null, null, new GetAgenciesCallback(getApplicationContext()));
            }
        });
    }

    private class GetAgenciesCallback extends NetworkUtil.RetroCallback<Response<List<Agency>>> {

        public GetAgenciesCallback(Context context) {
            super(context);
        }

        @Override
        public void success(Response<List<Agency>> listResponse, retrofit.client.Response response) {
            List<Agency> agencies = listResponse.data;
            List<String> descriptions = new ArrayList<>();


            for(Agency agency : agencies) {
                descriptions.add(agency.agencyId + " - " + agency.shortName);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, descriptions);

            responseList.setAdapter(adapter);
        }
    }
}
