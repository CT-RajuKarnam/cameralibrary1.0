package com.cartrade.cameralib.camerafiles.camera2;

import android.os.Bundle;

/**
 * Created by Sudheer on 1/3/2018.
 */

public interface ActivityInterface {

    public void resume();
    public void pause();
    public void backPressed();
    public void destroy();
    public void saveInstanceState(Bundle outState);
}
