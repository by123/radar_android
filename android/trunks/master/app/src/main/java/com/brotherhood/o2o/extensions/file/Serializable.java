package com.brotherhood.o2o.extensions.file;


/**
 * Created by by.huang on 2015/7/1.
 */

import java.io.IOException;


public interface Serializable {

    public void serialize(Output out) throws IOException;

    public void deserialize(Input in) throws IOException;
}
