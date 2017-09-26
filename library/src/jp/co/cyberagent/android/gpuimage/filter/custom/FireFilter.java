package jp.co.cyberagent.android.gpuimage.filter.custom;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.R;
import jp.co.cyberagent.android.gpuimage.core.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.util.GlUtils;

import static jp.co.cyberagent.android.gpuimage.core.GLToolbox.checkGlError;
import static jp.co.cyberagent.android.gpuimage.core.OpenGlUtils.readShaderFromRawResource;

/**
 * Created by fanjie on 2017/9/20.
 */

public class FireFilter extends GPUImageFilter {

    public FireFilter(Context context) {
        super(GPUImageFilter.NO_FILTER_VERTEX_SHADER, readShaderFromRawResource(context, R.raw.fire));
    }

    private int timeLocation;

    @Override
    public void onInit() {
        super.onInit();
        timeLocation = GLES20.glGetUniformLocation(getProgram(), "iTime");
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
        checkGlError("vertex attribute setup");
        // TODO: 2017/9/12 处理纹理顶点
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);

        setTime();// TODO: 2017/9/21 模拟时间

        if (textureId != GlUtils.NO_TEXTURE) {
            // TODO: 2017/9/12 应用纹理
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }
        checkGlError("glActiveTexture");
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        checkGlError("glActiveTexture");
    }

    private float time;

    private void setTime() {
        setFloat(timeLocation, time);
        time += 0.05;
    }
}
