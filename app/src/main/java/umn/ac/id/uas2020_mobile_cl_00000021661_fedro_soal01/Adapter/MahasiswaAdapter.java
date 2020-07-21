package umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

import umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.HalamanDetail;
import umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.Model.Mahasiswa;
import umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.R;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.ViewHolder> {

    private Context mContext;
    private List<Mahasiswa> mMahasiswa;


    public MahasiswaAdapter(Context mContext, List<Mahasiswa> mMahasiswa) {
        this.mMahasiswa = mMahasiswa;
        this.mContext = mContext;
    }

    public MahasiswaAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_template,parent,false);
        return new MahasiswaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final Mahasiswa mahasiswa = mMahasiswa.get(position);
        holder.nim.setText(mahasiswa.getNIM());
        holder.nama.setText(mahasiswa.getNama());
        Glide.with(mContext).load(mahasiswa.getImageURL()).into(holder.profile_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HalamanDetail.class);
                intent.putExtra("IDnya", mahasiswa.getID());
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mMahasiswa.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView nim;
        public TextView nama;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            nim = itemView.findViewById(R.id.displayNIM);
            nama = itemView.findViewById(R.id.displayNAMA);
            profile_image = itemView.findViewById(R.id.profile_image);


        }
    }

}
