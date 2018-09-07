package com.yocn.glsurface.myglsurfaceview;

import android.graphics.Matrix;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @Author yocn
 * @Date 2018/6/29 下午3:09
 * @ClassName DirectDrawer
 */
public class DirectDrawer {

    private static String fragmentShader = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 textureCoordinate;\n" +
            "uniform samplerExternalOES s_texture;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_FragColor = texture2D( s_texture, textureCoordinate );\n" +
            "}\n";
    private static String vertextShader = "uniform mat4 uMVPMatrix;\n" +
            "attribute vec4 vPosition;\n" +
            "attribute vec2 inputTextureCoordinate;\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_Position =  vPosition * uMVPMatrix ;\n" +
            "    textureCoordinate = inputTextureCoordinate;\n" +
            "}\n";
    private FloatBuffer vertexBuffer, mTextureCoordsBuffer;
    private ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int mMVPMatrixHandle;

    private short drawOrder[] = {0, 2, 1, 0, 3, 2}; // order to draw vertices

    // number of coordinates per vertex in this array
    private final int COORDS_PER_VERTEX = 2;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private float mVertices[] = new float[8];

    private float mTextureCoords[] = new float[8];
    private float mTextHeightRatio = 0.1f;

    private int texture;
    public float[] mMVP = new float[16];

    public void resetMatrix() {
        mat4f_LoadOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, mMVP);
    }


    public DirectDrawer(int texture) {

        mProgram = GlUtil.createProgram(vertextShader, fragmentShader);

        if (mProgram == 0) {
            throw new RuntimeException("Unable to create program");
        }

        //get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GlUtil.checkLocation(mPositionHandle, "vPosition");

        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        GlUtil.checkLocation(mTextureCoordHandle, "inputTextureCoordinate");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GlUtil.checkLocation(mMVPMatrixHandle, "uMVPMatrix");


        this.texture = texture;
        // initialize vertex byte buffer for shape coordinates
        updateVertices();

        setTexCoords();

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        mat4f_LoadOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, mMVP);
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);

        // get handle to vertex shader's vPosition member

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the <insert shape here> coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, getVertexBuffer());

        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, mTextureCoordsBuffer);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVP, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
    }

    public static void mat4f_LoadOrtho(float left, float right, float bottom, float top, float near, float far, float[] mout) {
        float r_l = right - left;
        float t_b = top - bottom;
        float f_n = far - near;
        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        mout[0] = 2.0f / r_l;
        mout[1] = 0.0f;
        mout[2] = 0.0f;
        mout[3] = 0.0f;

        mout[4] = 0.0f;
        mout[5] = 2.0f / t_b;
        mout[6] = 0.0f;
        mout[7] = 0.0f;

        mout[8] = 0.0f;
        mout[9] = 0.0f;
        mout[10] = -2.0f / f_n;
        mout[11] = 0.0f;

        mout[12] = tx;
        mout[13] = ty;
        mout[14] = tz;
        mout[15] = 1.0f;
    }

    public void updateVertices() {
        mVertices = getmVertices();
        vertexBuffer = ByteBuffer.allocateDirect(mVertices.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(mVertices);
        vertexBuffer.position(0);
    }

    public FloatBuffer getVertexBuffer() {
        mVertices = getmVertices();
        vertexBuffer = ByteBuffer.allocateDirect(mVertices.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(mVertices);
        vertexBuffer.position(0);
        return vertexBuffer;
    }

    public int ORITATION_0 = 0;
    public int ORITATION_90 = 1;
    public int ORITATION_180 = 2;
    public int ORITATION_270 = 3;

    private int mCurrentOritation = ORITATION_0;

    public void setRotate() {
        mCurrentOritation++;
        if (mCurrentOritation > ORITATION_270) {
            mCurrentOritation = ORITATION_0;
        }
    }

    public float[] getmVertices() {
        float verts[] = new float[8];
        final float w = 1.0f;
        final float h = 1.0f;
        verts[0] = -w;
        verts[1] = h;
        verts[2] = -w;
        verts[3] = -h;
        verts[4] = w;
        verts[5] = -h;
        verts[6] = w;
        verts[7] = h;

        float[] result = new float[8];
        Matrix matrix = new Matrix();
        matrix.reset();
        int degree = 90 * mCurrentOritation;
        matrix.postRotate(degree);
        matrix.mapPoints(result, verts);
//        print("90->" + degree, result);

        return result;
    }

    private void print(String tag, float[] result) {
        LogUtil.d("matrix->" + tag + ":" + result[0] + "." + result[1] + "/"
                + result[2] + "." + result[3] + "/"
                + result[4] + "." + result[5] + "/"
                + result[6] + "." + result[7]);
    }

    public void setTexCoords() {
        mTextureCoords[0] = 0;
        mTextureCoords[1] = 1 - mTextHeightRatio;
        mTextureCoords[2] = 1;
        mTextureCoords[3] = 1 - mTextHeightRatio;
        mTextureCoords[4] = 1;
        mTextureCoords[5] = 0 + mTextHeightRatio;
        mTextureCoords[6] = 0;
        mTextureCoords[7] = 0 + mTextHeightRatio;
        mTextureCoordsBuffer = ByteBuffer.allocateDirect(mTextureCoords.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(mTextureCoords);
        mTextureCoordsBuffer.position(0);
    }
}
