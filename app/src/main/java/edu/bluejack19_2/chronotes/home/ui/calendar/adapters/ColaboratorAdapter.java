package edu.bluejack19_2.chronotes.home.ui.calendar.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.model.Task;
import edu.bluejack19_2.chronotes.utils.TaskHandler;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class ColaboratorAdapter extends RecyclerView.Adapter<ColaboratorAdapter.MyViewHolder>{
    private Task t;
    private ArrayList<String> ids, emails;
    private Context con;

    public ColaboratorAdapter(Task t, ArrayList<String> ids, ArrayList<String> emails, Context con) {
        this.t = t;
        this.ids = ids;
        this.emails = emails;
        this.con = con;
    }

    @NonNull
    @Override
    public ColaboratorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(con).inflate(R.layout.item_colab, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColaboratorAdapter.MyViewHolder holder, int position) {
        Log.d("DEBUG", ids.size() + " " + position);
        holder.colab.setText(emails.get(position));
        holder.rm.setTag(ids.get(position));
    }

    @Override
    public int getItemCount() {

        return emails.size();
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView rm;
        TextView colab;

        public MyViewHolder(@NonNull View v) {
            super(v);

            colab = v.findViewById(R.id.tv_colab);
            rm = v.findViewById(R.id.iv_remove);

            rm.setOnClickListener(view -> {
                String id = view.getTag().toString();
                if(!ids.get(0).equals(SessionStorage.getSessionStorage(v.getContext()))){
                    Toast.makeText(v.getContext(),"You are not authorized to remove collaborator", Toast.LENGTH_LONG).show();
                    return;
                }
                for(int z=0;z<emails.size();z++){
                    if(id.equals(ids.get(z))){
                        ids.remove(z);
                        emails.remove(z);
                        t.removeUserId(id);
                        TaskHandler hand = TaskHandler.GetInstance();
                        hand.updateTask(t,v.getContext());
                        notifyDataSetChanged();
                        break;
                    }
                }
            });
        }
    }
}
