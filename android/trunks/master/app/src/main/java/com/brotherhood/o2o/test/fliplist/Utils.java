package com.brotherhood.o2o.test.fliplist;

import com.brotherhood.o2o.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by by.huang on 2015/7/7.
 */

public class Utils {
    public static final List<Friend> friends = new ArrayList<>();

    static {
        friends.add(new Friend(R.drawable.anastasia, "ANASTASIA", R.color.sienna));
        friends.add(new Friend(R.drawable.irene, "IRENE", R.color.main_red));
        friends.add(new Friend(R.drawable.kate, "KATE", R.color.green));
        friends.add(new Friend(R.drawable.paul, "PAUL", R.color.pink));
        friends.add(new Friend(R.drawable.daria, "DARIA", R.color.orange));
        friends.add(new Friend(R.drawable.kirill, "KIRILL", R.color.yellow));
        friends.add(new Friend(R.drawable.julia, "JULIA", R.color.green));
        friends.add(new Friend(R.drawable.test, "ALEX", R.color.gold));
    }
}