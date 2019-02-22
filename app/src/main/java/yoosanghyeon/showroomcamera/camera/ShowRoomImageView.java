package yoosanghyeon.showroomcamera.camera;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by desmond on 9/8/15.
 */
public class ShowRoomImageView extends AppCompatImageView {

    public ShowRoomImageView(Context context) {
        super(context);
    }

    public ShowRoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowRoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int squareLen = width > height ? height : width;
        setMeasuredDimension(squareLen, squareLen);
    }
}
