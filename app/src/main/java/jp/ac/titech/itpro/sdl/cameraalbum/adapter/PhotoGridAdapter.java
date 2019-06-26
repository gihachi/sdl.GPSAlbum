package jp.ac.titech.itpro.sdl.cameraalbum.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.PhotoData;
import jp.ac.titech.itpro.sdl.cameraalbum.util.FileUtil;
import jp.ac.titech.itpro.sdl.sdlcameraalbum.R;

public class PhotoGridAdapter extends BaseAdapter {

    private final static String TAG = PhotoGridAdapter.class.getSimpleName();

    private Context mContext;
    private List<PhotoData> mPhotoDataList;
    private LayoutInflater inflater;
    private File externalPhotoDir;

    @Override
    public int getCount(){
        return mPhotoDataList.size();
    }

    @Override
    public Object getItem(int i){
        return mPhotoDataList.get(i);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public PhotoGridAdapter(Context context, List<PhotoData> photoDataList, File externalPhotoDir){
        super();
        this.mContext = context;
        this.mPhotoDataList = photoDataList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.externalPhotoDir = externalPhotoDir;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View returnView;
        PhotoData photoData = mPhotoDataList.get(position);
        if(convertView == null){
            returnView = inflater.inflate(R.layout.grid_image_view,parent,false);
        }else{
            returnView = convertView;
        }

        ImageView imageView = returnView.findViewById(R.id.image_view);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        String photoFileName = FileUtil.makePhotoFileName(photoData.date);
        File photoFile = new File(externalPhotoDir, photoFileName);

        if(!photoFile.exists()){
            Log.d(TAG, "not exist files");
        }
        Picasso.with(mContext).load(photoFile).resize(200,200).into(imageView);

        return returnView;
    }
}
