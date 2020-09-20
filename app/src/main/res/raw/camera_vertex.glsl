attribute vec4 vPosition; //顶点坐标

attribute vec4 vTexturePos; //纹理坐标

uniform mat4 vMatrix; //变换矩阵

varying vec2 aCoord;//传递给片元着色器

void main() {
    gl_Position = vPosition; //内置变量
    aCoord = (vMatrix * vTexturePos).xy;
}
