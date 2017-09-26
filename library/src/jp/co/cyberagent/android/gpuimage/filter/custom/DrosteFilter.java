package jp.co.cyberagent.android.gpuimage.filter.custom;

import android.content.Context;

import jp.co.cyberagent.android.gpuimage.R;
import jp.co.cyberagent.android.gpuimage.core.GPUImageFilter;

import static jp.co.cyberagent.android.gpuimage.core.OpenGlUtils.readShaderFromRawResource;

/**
 * Created by fanjie on 2017/9/20.
 */

public class DrosteFilter extends GPUImageFilter {

    public DrosteFilter(Context context) {
        super(GPUImageFilter.NO_FILTER_VERTEX_SHADER, readShaderFromRawResource(context, R.raw.droste));
    }

}
