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

import jp.ac.titech.itpro.sdl.cameraalbum.adapter.GroupThumbnailAdapter;
import jp.ac.titech.itpro.sdl.cameraalbum.db.GroupDatabase;
import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.Group;
import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.PhotoData;

public class GroupThumbnailActivity extends MainGridActivity {

    private final static String TAG = GroupThumbnailActivity.class.getSimpleName();

    public static final String EXTRA_GROUP_ID = "GROUP_ID";

    private List<Group> groupList;
    private GroupThumbnailAdapter groupThumbnailAdapter;

    @Override
    protected void doAdditionalInitialization(){

        groupList = new ArrayList<>();
        groupThumbnailAdapter = new GroupThumbnailAdapter(getApplicationContext(), groupList, externalPath);

        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(groupThumbnailAdapter);

        Button button = findViewById(R.id.jump_another_activity_button);
        button.setText("Go to All Photo Page");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Group clickedGroup = groupList.get(position);
                Log.d(TAG, "group id "+clickedGroup._id);
                Intent intent = new Intent(getApplication(), GroupAlbumActivity.class);
                intent.putExtra(EXTRA_GROUP_ID, clickedGroup._id);

                startActivity(intent);
            }
        });

        initializeGroupList();
    }

    private void initializeGroupList(){
        Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {

                GroupDatabase groupDB = Room.databaseBuilder(getApplicationContext(), GroupDatabase.class, "groups").build();
                groupList.addAll(groupDB.groupDao().loadAllGroup());
                groupDB.close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        groupThumbnailAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void goToAnotherActivity(){
        Intent intent = new Intent(getApplication(), AllPhotoAlbumActivity.class);
        startActivity(intent);
    }

    @Override
    protected void doAfterStorePhotoData(PhotoData photoData){

    }

    @Override
    protected void notifyStorePhotoDataToUIThread(){

    }

}