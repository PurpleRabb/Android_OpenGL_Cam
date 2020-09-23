precision mediump float; //设置精度

varying vec2 aCoord; //来自顶点着色器

uniform sampler2D vTexture;

void main() {
    gl_FragColor =  texture2D(vTexture, aCoord);//内置变量，纹理颜色
}