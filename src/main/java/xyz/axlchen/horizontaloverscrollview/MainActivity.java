package xyz.axlchen.horizontaloverscrollview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HorizontalOverScrollView horizontalOverScrollView = (HorizontalOverScrollView) findViewById(R.id.horizontal_over_scroll_view);
        horizontalOverScrollView.setMoreActionListener(new HorizontalOverScrollView.MoreActionListener() {
            @Override
            public void moreAction() {
                Toast.makeText(MainActivity.this, "more action", Toast.LENGTH_LONG).show();
            }
        });
    }
}
