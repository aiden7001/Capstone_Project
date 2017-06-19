package test.com.a170326;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by Heera on 2017-06-06.
 */

public class NaviListViewItem {

    private Drawable imageStr;
    private String contentStr;

    public void setImage(Drawable ima){
        imageStr = ima;
    }
    public void setContent(String con){
        contentStr = con;
    }

    public Drawable getImage(){
        return this.imageStr;
    }

    public String getContent(){
        return this.contentStr;
    }

}
