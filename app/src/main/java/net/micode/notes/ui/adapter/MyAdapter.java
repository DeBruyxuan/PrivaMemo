package net.micode.notes.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.micode.notes.R;
import net.micode.notes.ui.EditActivity;
import net.micode.notes.ui.NoteDbOpenHelper;
import net.micode.notes.ui.bean.Note;
import net.micode.notes.util.ToastUtil;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Note> mBeanList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private NoteDbOpenHelper mNoteDbOpenHelper;

    public MyAdapter(Context context, List<Note> mBeanList){
        this.mBeanList = mBeanList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mNoteDbOpenHelper = new NoteDbOpenHelper(mContext);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_item_layout,parent,false);

        MyViewHolder myViewHolder= new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Note note = mBeanList.get(position);
        holder.mTvContent.setText(note.getContent());
        holder.mTvTime.setText(note.getCreatedTime());
        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, EditActivity.class);
                intent.putExtra("note",note);
                mContext.startActivity(intent);

            }
        });

        holder.rlContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //长按
                Dialog dialog = new Dialog(mContext,android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
                View view = mLayoutInflater.inflate(R.layout.list_item_dialog_layout, null);

                TextView tvDelete = view.findViewById(R.id.tv_delete);


                tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int row = mNoteDbOpenHelper.deleteFromDbById(note.getId());
                        if (row > 0){
                            removeData(position);

                            ToastUtil.toastShort(mContext,"删除成功");
                        }else{
                            ToastUtil.toastShort(mContext,"删除失败");
                        }
                        dialog.dismiss();
                    }
                });

                dialog.setContentView(view);

                dialog.show();

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {

        return mBeanList.size();
    }

    public void refreshData(List<Note> notes){
        this.mBeanList = notes;
        notifyDataSetChanged();
    }

    public void removeData(int pos){
        mBeanList.remove(pos);
        notifyItemRemoved(pos);
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mTvContent;
        TextView mTvTime;
        ViewGroup rlContainer;

      public MyViewHolder(@NonNull View itemView) {
          super(itemView);

          this.mTvContent = itemView.findViewById(R.id.tv_content);
          this.mTvTime = itemView.findViewById(R.id.tv_time);
          this.rlContainer = itemView.findViewById(R.id.rl_item_container);


      }
  }

}
