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
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import jp.ac.titech.itpro.sdl.sdlcameraalbum.db.PhotoDatabase;
import jp.ac.titech.itpro.sdl.sdlcameraalbum.db.entity.PhotoData;
import jp.ac.titech.itpro.sdl.sdlcameraalbum.util.FileUtil;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private ArrayAdapter<String> adapter;
    private final static int REQ_PHOTO = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        makePhotoDirIfNotExsist();

        setContentView(R.layout.activity_main);
        ListView photoList = findViewById(R.id.photo);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                new ArrayList<>());
        photoList.setAdapter(adapter);
        showPhotoList();
    }

    public void showPhotoList(){
        adapter.clear();
        PhotoDatabase photoDB = Room.databaseBuilder(getApplicationContext(), PhotoDatabase.class, "photos").allowMainThreadQueries().build();
        List<String> photoList = new ArrayList<>();
        for (PhotoData photoData : photoDB.photoDao().loadAllPhotoData()) {
            Log.d(TAG, "iterate photos");
            photoList.add(photoData._id+","+photoData.date+","+photoData.latitude+","+photoData.longitude);
            Log.d(TAG, photoData._id+","+photoData.date+","+photoData.latitude+","+photoData.longitude);
        }

        if(!photoList.isEmpty()){
            Log.d(TAG, "notify");
            adapter.addAll(photoList);
            adapter.notifyDataSetChanged();
        }
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

//    public void makePhotoDirIfNotExsist(){
//        File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"GeoAlbum");
//        if (! storageDir.exists()){
//            if (! storageDir.mkdirs()){
//                Log.d("MyCameraApp", "failed to create directory");
//                throw new Error("dir cannot make");
//            }
//        }
//    }

    private File getTempFilePath(){
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); //new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"GeoAlbum");
        File photoFilePath = new File(storageDir, getString(R.string.temp_photo_file_name));
        return photoFilePath;
    }

    private String makePhotoFileName(String takenDate){
        return getString(R.string.photo_file_nane_prefix)+takenDate+".jpg";
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
        String fileName = makePhotoFileName(takenDate);

        double latitude = 0.5;
        double longitude = 0.3;
        String description = "";

        FileUtil.renameFile(getTempFilePath(), new File(fileName));
        PhotoData photoData = new PhotoData(takenDate, description, latitude, longitude);

        PhotoDatabase photoDB = Room.databaseBuilder(getApplicationContext(), PhotoDatabase.class, "photos").allowMainThreadQueries().build();
        photoDB.photoDao().insertPhoto(photoData);

        showPhotoList();
    }


}
