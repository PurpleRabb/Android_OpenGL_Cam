#extension GL_OES_EGL_image_external : require

precision mediump float; //设置精度

varying vec2 aCoord; //来自顶点着色器

uniform samplerExternalOES vTexture; //相机着色器不能直接使用sampler2D

void main() {
    gl_FragColor =  texture2D(vTexture, aCoord);//内置变量，纹理颜色
}
