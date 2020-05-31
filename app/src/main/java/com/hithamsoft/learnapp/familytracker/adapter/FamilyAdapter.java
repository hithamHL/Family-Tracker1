package com.hithamsoft.learnapp.familytracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.hithamsoft.learnapp.familytracker.R;
import com.hithamsoft.learnapp.familytracker.model.Family;

import java.util.List;

public class FamilyAdapter extends

        RecyclerView.Adapter<FamilyAdapter.ViewHolder> {

    private static final String TAG = FamilyAdapter.class.getSimpleName();

    private Context context;

    private List<Family> list;

    private OnItemClickListener onItemClickListener;

    public FamilyAdapter(Context context, List<Family> list,

                         OnItemClickListener onItemClickListener) {

        this.context = context;

        this.list = list;

        this.onItemClickListener = onItemClickListener;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView familyName;

        public ViewHolder(View itemView) {

            super(itemView);
            avatar=itemView.findViewById(R.id.personIcon_iv);
            familyName=itemView.findViewById(R.id.personName_txt);

        }

        public void bind(final Family model,

                         final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    listener.onItemClick(getLayoutPosition());

                }

            });

        }

    }

    @Override
    public int getItemViewType(int position) {
        return (position==list.size())? R.layout.add_family:R.layout.family_item;
    }

    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        if (viewType==R.layout.add_family){
            itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.add_family,parent,false);
        }else {
            itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.family_item,parent,false);
        }

        return new ViewHolder(itemView);

    }

    @Override

    public void onBindViewHolder(ViewHolder holder, int position) {

        if (list.size()==position){
            //is the last item that is add button
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "family member Added", Toast.LENGTH_SHORT).show();
                }
            });

        }else {

            Family item = list.get(position);

            holder.bind(item, onItemClickListener);
            holder.familyName.setText(item.getPersonName());
            holder.avatar.setImageResource(item.getPersonIcon());
        }

    }

    @Override

    public int getItemCount() {

        return list.size()+1;

    }

    public interface OnItemClickListener {

        void onItemClick(int position);

    }

}