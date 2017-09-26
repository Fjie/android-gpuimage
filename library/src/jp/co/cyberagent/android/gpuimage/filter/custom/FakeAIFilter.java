package jp.co.cyberagent.android.gpuimage.filter.custom;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageTwoInputFilter;

/**
 * Created by fanjie on 2017/9/12. 来一个屌炸天的双纹理滤镜
 */

public class FakeAIFilter extends GPUImageTwoInputFilter {
    private static final String VERTEX_SHADER =
            "attribute vec4 position;\n" +
                    "attribute vec4 inputTextureCoordinate;\n" +
                    "attribute vec4 inputTextureCoordinate2;\n" +
                    " \n" +
                    "varying vec4 v_position;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "varying vec2 textureCoordinate2;\n" +
                    " \n" +
                    "void main()\n" +
                    "{\n" +
                    "    gl_Position = position;\n" +
                    "    v_position = position;\n" +
                    "    textureCoordinate = inputTextureCoordinate.xy;\n" +
                    "    textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
                    "}";
    public static final String FRAGMENT_SHADER_H =
                    "precision mediump float;\n" +
                    "varying vec4 v_position;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    "varying highp vec2 textureCoordinate2;\n" +
                    "\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "uniform sampler2D inputImageTexture2;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "  highp vec4 a = texture2D(inputImageTexture, textureCoordinate);\n" +
                    "  highp vec4 b = texture2D(inputImageTexture2, textureCoordinate2);\n" +
                    "  highp vec4 c = mix(a,b,0.5);\n" +
//                    "  highp vec4 c = vec4( min(a.r,b.r), min(a.g,b.g), min(a.b,b.b), 1.0);\n" +
//                    "  highp vec4 c = vec4( max(a.r,b.r), max(a.g,b.g), max(a.b,b.b), 1.0);\n" +
//                    "  highp vec4 c = vec4( a.r, b.g, a.b, 1.0);\n" +
                    "  gl_FragColor = c;\n" +
                    "}";

    public FakeAIFilter() {
        super(VERTEX_SHADER, FRAGMENT_SHADER_H);
    }
}
