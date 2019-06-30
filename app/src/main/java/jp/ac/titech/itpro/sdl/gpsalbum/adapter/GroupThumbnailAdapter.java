package jp.ac.titech.itpro.sdl.gpsalbum.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import jp.ac.titech.itpro.sdl.gpsalbum.R;
import jp.ac.titech.itpro.sdl.gpsalbum.db.entity.Group;

public class GroupThumbnailAdapter extends BaseAdapter {

    private final static String TAG = PhotoGridAdapter.class.getSimpleName();

    private Context mContext;
    private List<Group> mGroupList;
    private LayoutInflater inflater;
    private File externalPhotoDir;

    @Override
    public int getCount(){
        return mGroupList.size();
    }

    @Override
    public Object getItem(int i){
        return mGroupList.get(i);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public GroupThumbnailAdapter(Context context, List<Group> groupList , File externalPhotoDir){
        super();
        this.mContext = context;
        this.mGroupList = groupList;
        this.externalPhotoDir = externalPhotoDir;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View returnView;
        Group group = mGroupList.get(position);
        if(convertView == null){
            returnView = inflater.inflate(R.layout.grid_group_view,parent,false);
        }else{
            returnView = convertView;
        }

        ImageView imageView = returnView.findViewById(R.id.group_image_view);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        TextView textView = returnView.findViewById(R.id.group_text_view);
        textView.setText("group"+group._id);

        String photoFileName = group.thumbnailName;
        File photoFile = new File(externalPhotoDir, photoFileName);

        if(!photoFile.exists()){
            Log.d(TAG, "not exist files");
        }
        Picasso.with(mContext).load(photoFile).resize(200,200).into(imageView);

        return returnView;
    }
}
