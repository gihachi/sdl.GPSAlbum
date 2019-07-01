package jp.ac.titech.itpro.sdl.gpsalbum;

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

import jp.ac.titech.itpro.sdl.gpsalbum.adapter.PhotoGridAdapter;
import jp.ac.titech.itpro.sdl.gpsalbum.constantval.ExtraString;
import jp.ac.titech.itpro.sdl.gpsalbum.db.GroupDatabase;
import jp.ac.titech.itpro.sdl.gpsalbum.db.PhotoDatabase;
import jp.ac.titech.itpro.sdl.gpsalbum.db.entity.Group;
import jp.ac.titech.itpro.sdl.gpsalbum.db.entity.PhotoData;
import jp.ac.titech.itpro.sdl.gpsalbum.util.FileUtil;
import jp.ac.titech.itpro.sdl.gpsalbum.util.PhotoDataUtil;

public class GroupAlbumActivity extends AppCompatActivity {

    private final static String TAG = AllPhotoAlbumActivity.class.getSimpleName();

    long groupID;
    private int groupListID;
    private List<PhotoData> photoDataList;
    private List<String> photoDateList;
    private PhotoGridAdapter photoGridAdapter;
    private File externalPath;

    public static final String EXTRA_DELETE_GROUP_LIST_ID = "DELETE_GROUP_LIST_ID";
    private static final int REQ_PHOTO_VIEW = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoDataList = new ArrayList<>();
        photoDateList = new ArrayList<>();

        setContentView(R.layout.activity_group_album);

        externalPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        photoGridAdapter = new PhotoGridAdapter(getApplicationContext(), photoDateList, externalPath);

        Intent intent = getIntent();
        groupID = intent.getLongExtra(ExtraString.EXTRA_GROUP_ID, -1);
        groupListID = intent.getIntExtra(ExtraString.EXTRA_GROUP_LIST_ID, -1);

        photoGridAdapter = new PhotoGridAdapter(getApplicationContext(), photoDateList, externalPath);
        GridView gridView = findViewById(R.id.group_grid_view);
        gridView.setAdapter(photoGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PhotoData clickedPhoto = photoDataList.get(position);
                Intent intent = new Intent(getApplication(), PhotoActivity.class);
                intent.putExtra(ExtraString.EXTRA_DATE, clickedPhoto.date);
                intent.putExtra(ExtraString.EXTRA_LATITUDE, clickedPhoto.latitude);
                intent.putExtra(ExtraString.EXTRA_LONGITUDE, clickedPhoto.longitude);
                intent.putExtra(ExtraString.EXTRA_LIST_INDEX, position);

                Log.d(TAG, "clicked photo data"+clickedPhoto.date+","+clickedPhoto.latitude+","+clickedPhoto.longitude+","+clickedPhoto.groupID+","+clickedPhoto.isOutSide);
                startActivityForResult(intent, REQ_PHOTO_VIEW);
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

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data){
        super.onActivityResult(reqCode, resCode, data);
        switch (reqCode){
            case REQ_PHOTO_VIEW:
                if(resCode == RESULT_OK) {
                    deletePhoto(data);
                }
                break;
        }
    }


    private void deletePhoto(Intent data){
        int listIndex = data.getIntExtra(ExtraString.EXTRA_DELETE_INDEX, -1);
        if(listIndex < 0){
            return;
        }

        PhotoData deletePhotoData = photoDataList.get(listIndex);
        new Thread(new Runnable() {
            @Override
            public void run() {// ファイル情報の消去
                File photoFile = new File(externalPath, FileUtil.makePhotoFileName(deletePhotoData.date));
                photoFile.delete();

                PhotoDatabase photoDB = Room.databaseBuilder(getApplicationContext(), PhotoDatabase.class, "photos").build();
                photoDB.photoDao().deletePhoto(deletePhotoData);

                List<PhotoData> sameGroupPhotos = photoDB.photoDao().loadPhotoDataByGroupID(deletePhotoData.groupID);
                if(sameGroupPhotos.size() == 0){
                    GroupDatabase groupDB = Room.databaseBuilder(getApplicationContext(), GroupDatabase.class, "groups").build();
                    List<Group> groupList = groupDB.groupDao().loadSpecificGroupFromID(groupID);

                    if(groupList.get(0)._id != groupID){
                        throw new Error("group id is not equal");
                    }

                    groupDB.groupDao().deleteGroup(groupList.get(0));
                    groupDB.close();
                }
                photoDB.close();
            }
        }).start();


        if (photoDataList.size() == 1){
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DELETE_GROUP_LIST_ID, groupListID);
            setResult(RESULT_OK, intent);
            finish();
        }else{
            photoDataList.remove(listIndex);
            photoDateList.remove(listIndex);
            photoGridAdapter.notifyDataSetChanged();
        }
    }
}
