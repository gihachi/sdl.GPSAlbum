package jp.ac.titech.itpro.sdl.cameraalbum;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import jp.ac.titech.itpro.sdl.cameraalbum.adapter.PhotoGridAdapter;
import jp.ac.titech.itpro.sdl.cameraalbum.db.PhotoDatabase;
import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.PhotoData;
import jp.ac.titech.itpro.sdl.cameraalbum.util.PhotoDataUtil;

public class AllPhotoAlbumActivity extends MainGridActivity {


    private final static String TAG = AllPhotoAlbumActivity.class.getSimpleName();
    private List<PhotoData> photoDataList;
    private List<String> photoDateList;
    private PhotoGridAdapter photoGridAdapter;

    public static final String EXTRA_DATE = "PHOTO_DATE";
    public static final String EXTRA_LATITUDE = "PHOTO_LATITUDE";
    public static final String EXTRA_LONGITUDE = "PHOTO_LONGITUDE";

    @Override
    protected void doAdditionalInitialization(){

        photoDataList = new ArrayList<>();
        photoDateList = new ArrayList<>();

        photoGridAdapter = new PhotoGridAdapter(getApplicationContext(), photoDateList, externalPath);
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(photoGridAdapter);

        Button button = findViewById(R.id.jump_another_activity_button);
        button.setText("Go to Group Album Page");

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

        initialisePhotoList();
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
    protected void doAfterStorePhotoData(PhotoData photoData){
        photoDataList.add(photoData);
        photoDateList.add(photoData.date);
    }
    @Override
    protected void notifyStorePhotoDataToUIThread(){
        photoGridAdapter.notifyDataSetChanged();
    }
}
