package com.example.anonchat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class GroupViewAdapter
        extends RecyclerView.Adapter<GroupViewAdapter.MyViewHolder> {

    static Context context;

    ArrayList<GroupModel> groupModels;

    public GroupViewAdapter(Context context, ArrayList<GroupModel> groupModels) {
        this.context = context;
        this.groupModels = groupModels;
    }

    @NonNull
    @Override
    public GroupViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new GroupViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewAdapter.MyViewHolder holder, int position) {
        holder.btnGroupName.setText(groupModels.get(position).getGroupName());
    }

    @Override
    public int getItemCount() {
        return groupModels.size();
    }

    public static class MyViewHolder
            extends RecyclerView.ViewHolder {

        Button btnGroupName = itemView.findViewById(R.id.btnGroupName);

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            btnGroupName.setOnClickListener(view -> {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("groupName", btnGroupName.getText().toString());
                context.startActivity(intent);
            });
        }
    }


}
