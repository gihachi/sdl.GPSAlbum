package jp.ac.titech.itpro.sdl.cameraalbum.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import jp.ac.titech.itpro.sdl.cameraalbum.R;

public class DeletePhotoDialog extends DialogFragment {

    public interface DeletePhotoListener{
        public void onDeletePositiveClick(DialogFragment dialog);
    }

    DeletePhotoDialog.DeletePhotoListener mListener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Activity activity = getActivity();
        try{
            mListener = (DeletePhotoDialog.DeletePhotoListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(" cannot cast ");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("delete this photo?")
                .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       mListener.onDeletePositiveClick(DeletePhotoDialog.this);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

}
