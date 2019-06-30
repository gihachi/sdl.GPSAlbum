package jp.ac.titech.itpro.sdl.gpsalbum;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.ac.titech.itpro.sdl.gpsalbum.adapter.PhotoGridAdapter;
import jp.ac.titech.itpro.sdl.gpsalbum.db.GroupDatabase;
import jp.ac.titech.itpro.sdl.gpsalbum.db.PhotoDatabase;
import jp.ac.titech.itpro.sdl.gpsalbum.db.entity.Group;
import jp.ac.titech.itpro.sdl.gpsalbum.db.entity.PhotoData;
import jp.ac.titech.itpro.sdl.gpsalbum.util.FileUtil;
import jp.ac.titech.itpro.sdl.gpsalbum.util.PhotoDataUtil;

public class AllPhotoAlbumActivity extends MainGridActivity {


    private final static String TAG = AllPhotoAlbumActivity.class.getSimpleName();
    private List<PhotoData> photoDataList;
    private List<String> photoDateList;
    private PhotoGridAdapter photoGridAdapter;

    private final static int REQ_PHOTO_VIEW = 2222;

    public static final String EXTRA_DATE = "PHOTO_DATE";
    public static final String EXTRA_LATITUDE = "PHOTO_LATITUDE";
    public static final String EXTRA_LONGITUDE = "PHOTO_LONGITUDE";
    public static final String EXTRA_LIST_INDEX = "LIST_INDEX";

    @Override
    protected void doAdditionalInitialization(){

        photoDataList = new ArrayList<>();
        photoDateList = new ArrayList<>();

        photoGridAdapter = new PhotoGridAdapter(getApplicationContext(), photoDateList, externalPath);
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(photoGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PhotoData clickedPhoto = photoDataList.get(position);
                Intent intent = new Intent(getApplication(), PhotoActivity.class);
                intent.putExtra(EXTRA_DATE, clickedPhoto.date);
                intent.putExtra(EXTRA_LATITUDE, clickedPhoto.latitude);
                intent.putExtra(EXTRA_LONGITUDE, clickedPhoto.longitude);
                intent.putExtra(EXTRA_LIST_INDEX, position);

                Log.d(TAG, "clicked photo data"+clickedPhoto.date+","+clickedPhoto.latitude+","+clickedPhoto.longitude+","+clickedPhoto.groupID+","+clickedPhoto.isOutSide);
                startActivityForResult(intent, REQ_PHOTO_VIEW);
            }
        });

        initialisePhotoList();
    }

    @Override
    protected void unvisibleSpecificMenuItem(Menu menu){
        MenuItem unvisibleItem = menu.findItem(R.id.go_all_photo_activity);
        unvisibleItem.setVisible(false);
    }

    @Override
    protected void goAllPhotoActivity(){

    }

    @Override
    protected  void goGropThumbnailActivity(){
        jampAnotherActivity();
    }

    private void initialisePhotoList(){
        Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {

                PhotoDatabase photoDB = Room.databaseBuilder(getApplicationContext(), PhotoDatabase.class, "photos").build();
                photoDataList.addAll(photoDB.photoDao().loadAllPhotoData());
                photoDateList.addAll(PhotoDataUtil.makePhotoDateList(photoDataList));
                photoDB.close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        photoGridAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void goToAnotherActivity(){
        Intent intent = new Intent(getApplication(), GroupThumbnailActivity.class);
        startActivity(intent);
    }

    @Override
    protected void notifyStorePhotoDataToUIThread(PhotoData photoData, Group group){
        Toast.makeText(getApplicationContext(), "new photo is registered in group"+photoData.groupID, Toast.LENGTH_LONG).show();
        photoDataList.add(photoData);
        photoDateList.add(photoData.date);
        photoGridAdapter.notifyDataSetChanged();
    }

    @Override
    protected void otherActionForActivityResult(int reqCode, int resCode, Intent data){
        switch (reqCode){
            case REQ_PHOTO_VIEW:
                if (resCode == RESULT_OK){
                    deletePhoto(data);
                }
                break;
        }
    }

    void deletePhoto(Intent intent){
        int listIndex = intent.getIntExtra(PhotoActivity.EXTRA_DELETE_INDEX, -1);
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
                    List<Group> groupList = groupDB.groupDao().loadAllGroup();
                    groupDB.groupDao().deleteGroup(groupList.get(0));
                    groupDB.close();
                }
                photoDB.close();
            }
        }).start();


        photoDataList.remove(listIndex);
        photoDateList.remove(listIndex);
        photoGridAdapter.notifyDataSetChanged();
    }
}
