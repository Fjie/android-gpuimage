package jp.co.cyberagent.android.gpuimage.filter.custom;

import jp.co.cyberagent.android.gpuimage.core.GPUImageFilter;

/**
 * Created by fanjie on 2017/9/12. 来一个屌炸天的双纹理滤镜
 */

public class DoubleFilter extends GPUImageFilter {

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
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate * vec2(2.0,1.0));\n" +
            "}";

    public DoubleFilter() {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
    }
}
