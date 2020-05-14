package edu.bluejack19_2.chronotes.home.ui.calendar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.model.Task;

public class ColaboratorAdapter extends RecyclerView.Adapter<ColaboratorAdapter.MyViewHolder>{
    private Task t;
    private Context con;

    public ColaboratorAdapter(Task t, Context con) {
        this.t = t;
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

    }

    @Override
    public int getItemCount() {

        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView rm;
        TextView colab;

        public MyViewHolder(@NonNull View v) {
            super(v);
            colab = v.findViewById(R.id.tv_colab);
            rm = v.findViewById(R.id.iv_remove);
        }
    }
}
