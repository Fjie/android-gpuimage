package jp.co.cyberagent.android.gpuimage.filter.custom;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.core.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.util.GlUtils;

/**
 * Created by fanjie on 2017/9/19. 渐变滤镜
 */

public class TransitionFilter extends GPUImageFilter {

    private static final String TAG = "TransitionFilter";

    public static final String VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    public static final String FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            // TODO: 2017/9/19  外部传值实现渐变
            "uniform highp float mR;\n" +
            "uniform highp float mG;\n" +
            "uniform highp float mB;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     highp vec4 a = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     gl_FragColor = vec4(a.r * mR, a.g * mG, a.b * mB, 1.0);\n" +// TODO: 2017/9/19 操作纹理rgb
            "}";

    private int rLocation;
    private int gLocation;
    private int bLocation;

    private List<RGB> rgbs;

    public TransitionFilter() {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
        rgbs = new ArrayList<>();
        rgbs.add(new RGB(.1f, .1f, .2f));
        rgbs.add(new RGB(.9f, .3f, .2f));
        rgbs.add(new RGB(.4f, .5f, .7f));
        rgbs.add(new RGB(1f, 1f, 1f));
        rgbs.add(new RGB(.4f, .5f, .7f));
        rgbs.add(new RGB(.9f, .3f, .2f));
        rgbs.add(new RGB(.1f, .1f, .2f));
    }

    public TransitionFilter(List<RGB> rgbList) {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
        this.rgbs = rgbList;
    }

    @Override
    public void onInit() {
        super.onInit();
        rLocation = GLES20.glGetUniformLocation(getProgram(), "mR");
        gLocation = GLES20.glGetUniformLocation(getProgram(), "mG");
        bLocation = GLES20.glGetUniformLocation(getProgram(), "mB");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mGLProgId);// TODO: 2017/9/12 使用通道
        runPendingOnDrawTasks();// TODO: 2017/9/12 先处理全家桶
        if (!mIsInitialized) {
            return;
        }
        // TODO: 2017/9/12 处理顶点
        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        // TODO: 2017/9/12 处理纹理顶点
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);

        setRGBTransition();// TODO: 2017/9/19 加入渐变

        if (textureId != GlUtils.NO_TEXTURE) {
            // TODO: 2017/9/12 应用纹理
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }


    /**
     * 用来记录渐变进行的下标，暂时根据屏幕每次渲染递增
     */
    private float index;

    /**
     * 根据递增的下标判断渐变进行程度
     */
    private void setRGBTransition() {
        int size = rgbs.size();
        for (int i = 0; i < size - 1; i++) {
            //遍历传入的rgbList结合下标判断递进的颜色区间
            if (index > i && index < i + 1) {
                //取个颜色区间
                RGB start = rgbs.get(i);
                RGB stop = rgbs.get(i + 1);
                //在对应的颜色区间内渐变
                setRGB(
                        transition(index - i, start.r, stop.r),
                        transition(index - i, start.g, stop.g),
                        transition(index - i, start.b, stop.b)
                );
            }
        }
        if (index > size) {
            index = 0f;
        }
        index += 0.01f;//每次递增
    }

    /**
     * @param index 渐变递增的标记，限制0-1
     * @param start 起始数值
     * @param stop  终止数值
     * @return 返回运算后的值
     */
    private float transition(float index, float start, float stop) {
        if (index > 1 || index < 0) {
            throw new RuntimeException("渐变递增的标记，限制1");
        }
        return start + (stop - start) * (index);//直线渐变
    }

    private void setRGB(float r, float g, float b) {
        Log.d(TAG, "setRGB: " + "r = [" + r + "], g = [" + g + "], b = [" + b + "]");
        setFloat(rLocation, r);
        setFloat(gLocation, g);
        setFloat(bLocation, b);
    }

    public static class RGB {
        public float r;
        public float g;
        public float b;

        public RGB(float r, float g, float b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

}
