package jp.ac.titech.itpro.sdl.sdlcameraalbum;


import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import jp.ac.titech.itpro.sdl.sdlcameraalbum.adapter.PhotoGridAdapter;
import jp.ac.titech.itpro.sdl.sdlcameraalbum.db.PhotoDatabase;
import jp.ac.titech.itpro.sdl.sdlcameraalbum.db.entity.PhotoData;
import jp.ac.titech.itpro.sdl.sdlcameraalbum.util.FileUtil;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private ArrayAdapter<String> adapter;
    private final static int REQ_PHOTO = 1234;
    private File externalPath;
    private List<PhotoData> photoDataList;
    private PhotoGridAdapter photoGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        externalPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        initialisePhotoList();

        setContentView(R.layout.activity_main);

        photoGridAdapter = new PhotoGridAdapter(getApplicationContext(), photoDataList, externalPath);
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(photoGridAdapter);
    }

    public void initialisePhotoList(){
        // TODO 別スレッドで動かす
        PhotoDatabase photoDB = Room.databaseBuilder(getApplicationContext(), PhotoDatabase.class, "photos").allowMainThreadQueries().build();
        photoDataList = photoDB.photoDao().loadAllPhotoData();
    }

    public void takePhoto(View view){
        File photoFilePath = getTempFilePath();

        Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                "jp.ac.titech.itpro.sdl.sdlcameraalbum.fileprovider",
                photoFilePath);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        PackageManager manager = getPackageManager();
        List activities = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (!activities.isEmpty()) {
            startActivityForResult(intent, REQ_PHOTO);
        } else {
            Toast.makeText(MainActivity.this, R.string.toast_no_camera, Toast.LENGTH_LONG).show();
        }
    }

    private File getTempFilePath(){
        File storageDir = externalPath;
        File photoFilePath = new File(storageDir, getString(R.string.temp_photo_file_name));
        return photoFilePath;
    }


    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data){
        super.onActivityResult(reqCode, resCode, data);
        switch (reqCode){
            case REQ_PHOTO:
                if(resCode == RESULT_OK){
                    addPhotoDataToDataBase();
                }
                break;
        }
    }

    private void addPhotoDataToDataBase(){

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSSZ");
        String takenDate = sdf.format(date);
        String fileName = FileUtil.makePhotoFileName(takenDate);

        double latitude = 0.5;
        double longitude = 0.3;
        String description = "";

        FileUtil.renameFile(getTempFilePath(), new File(externalPath, fileName));
        PhotoData photoData = new PhotoData(takenDate, description, latitude, longitude);

        PhotoDatabase photoDB = Room.databaseBuilder(getApplicationContext(), PhotoDatabase.class, "photos").allowMainThreadQueries().build();
        photoDB.photoDao().insertPhoto(photoData);

        Log.d(TAG, "taken photo : "+fileName);
        photoDataList.add(photoData);
        photoGridAdapter.notifyDataSetChanged();
    }


}
