package jp.ac.titech.itpro.sdl.cameraalbum;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.ac.titech.itpro.sdl.cameraalbum.adapter.PhotoGridAdapter;
import jp.ac.titech.itpro.sdl.cameraalbum.db.PhotoDatabase;
import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.PhotoData;
import jp.ac.titech.itpro.sdl.cameraalbum.util.PhotoDataUtil;

import static jp.ac.titech.itpro.sdl.cameraalbum.AllPhotoAlbumActivity.EXTRA_DATE;
import static jp.ac.titech.itpro.sdl.cameraalbum.AllPhotoAlbumActivity.EXTRA_LATITUDE;
import static jp.ac.titech.itpro.sdl.cameraalbum.AllPhotoAlbumActivity.EXTRA_LONGITUDE;

public class GroupAlbumActivity extends AppCompatActivity {

    private final static String TAG = AllPhotoAlbumActivity.class.getSimpleName();

    long groupID;
    private List<PhotoData> photoDataList;
    private List<String> photoDateList;
    private PhotoGridAdapter photoGridAdapter;
    private File externalPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoDataList = new ArrayList<>();
        photoDateList = new ArrayList<>();

        setContentView(R.layout.activity_group_album);

        externalPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        photoGridAdapter = new PhotoGridAdapter(getApplicationContext(), photoDateList, externalPath);

        Intent intent = getIntent();
        groupID = intent.getLongExtra(GroupThumbnailActivity.EXTRA_GROUP_ID, 0);

        photoGridAdapter = new PhotoGridAdapter(getApplicationContext(), photoDateList, externalPath);
        GridView gridView = findViewById(R.id.group_grid_view);
        gridView.setAdapter(photoGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PhotoData clickedPhoto = photoDataList.get(position);
                Intent intent = new Intent(getApplication(), PhotoActivity.class);
                intent.putExtra(EXTRA_DATE, clickedPhoto.date);
                intent.putExtra(EXTRA_LATITUDE, clickedPhoto.latitude);
                intent.putExtra(EXTRA_LONGITUDE, clickedPhoto.longitude);

                Log.d(TAG, "clicked photo data"+clickedPhoto.date+","+clickedPhoto.latitude+","+clickedPhoto.longitude+","+clickedPhoto.groupID+","+clickedPhoto.isOutSide);
                startActivity(intent);
            }
        });
        initializePhotoDataList();
    }

    private void initializePhotoDataList(){
        Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {

                PhotoDatabase photoDB = Room.databaseBuilder(getApplicationContext(), PhotoDatabase.class, "photos").build();
                photoDataList.addAll(photoDB.photoDao().loadPhotoDataByGroupID(groupID));
                photoDateList.addAll(PhotoDataUtil.makePhotoDateList(photoDataList));
                photoDB.close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG, "photoLength : "+ photoDateList.size());
                        photoGridAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}
