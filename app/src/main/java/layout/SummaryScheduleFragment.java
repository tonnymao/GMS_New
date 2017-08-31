package layout;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.inspira.gms.IndexInternal.global;

/**
 * Created by shoma on 30/08/17.
 */

public class SummaryScheduleFragment extends Fragment {
    private String target;
    private String targetID;
    private String date;
    private String time;
    private String type;
    private String reminder;
    private String description;

    public void setDataSchedule(String target, String targetID, String date, String time, String type, String reminder, String description) {
        this.target = target;
        this.targetID = targetID;
        this.date = date;
        this.time = time;
        this.type = type;
        this.reminder = reminder;
        this.description = description;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview_schedule_task, container, false);
        getActivity().setTitle("Schedule Preview");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) getView().findViewById(R.id.txvTarget)).setText(targetID);
        ((TextView) getView().findViewById(R.id.txvType)).setText(type);
        ((TextView) getView().findViewById(R.id.txvDate)).setText(date);
        ((TextView) getView().findViewById(R.id.txvTime)).setText(time);
        ((TextView) getView().findViewById(R.id.txvMinute)).setText(reminder);
        ((TextView) getView().findViewById(R.id.txvDescription)).setText(description);
        ((Button) getView().findViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String actionUrl = "Master/getBarang/";
//                new setSchedule().execute(actionUrl);
            }
        });
    }

    private class setSchedule extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;
        String creator;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            description.replace('"', '\"');
            description.replace("'", "\'");
            creator = LibInspira.getShared(global.userpreferences, global.user.nomor, "");
        }

        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            try {
                jsonObject.put("target", target);
                jsonObject.put("type", type);
                jsonObject.put("date", date);
                jsonObject.put("time", time);
                jsonObject.put("reminder", reminder);
                jsonObject.put("description", description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return LibInspira.executePost(getContext(), urls[0], jsonObject);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            LibInspira.ShowLongToast(getContext(), "Schedule Created");
        }
    }
}
