package jp.co.cyberagent.android.gpuimage.util;

import jp.co.cyberagent.android.gpuimage.filter.GPUImage3x3TextureSamplingFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSobelThresholdFilter;
import jp.co.cyberagent.android.gpuimage.filter.base.GPUImageFilterGroup;

/**
 * Applies sobel edge detection on the image.
 */
public class GPUImageThresholdEdgeDetection extends GPUImageFilterGroup {
    public GPUImageThresholdEdgeDetection() {
        super();
        addFilter(new GPUImageGrayscaleFilter());
        addFilter(new GPUImageSobelThresholdFilter());
    }

    public void setLineSize(final float size) {
        ((GPUImage3x3TextureSamplingFilter) getFilters().get(1)).setLineSize(size);
    }

    public void setThreshold(final float threshold) {
        ((GPUImageSobelThresholdFilter) getFilters().get(1)).setThreshold(threshold);
    }
}
