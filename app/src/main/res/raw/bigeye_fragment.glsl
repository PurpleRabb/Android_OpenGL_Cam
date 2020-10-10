//大眼特效的算法处理（针对纹理数据进行处理）【局部放大算法】

//中等精度
precision mediump float;
//从顶点着色器传过来的
varying vec2 aCoord;
//采样器
uniform sampler2D vTexture;
//左眼
uniform vec2 left_eye;
//右眼
uniform vec2 right_eye;

//rmax 局部放大最大作用半径
float fs(float r, float rmax){
    float a = 0.4;//放大系数
    //    return (1.0 - pow(r / rmax - 1.0) * a);
    return (1.0 - pow(r / rmax - 1.0, 2.0) * a);
}

//oldCoord：旧的采样点坐标；  eye：眼睛坐标；rmax ：局部放大最大作用半径
vec2 calcNewCoord(vec2 oldCoord, vec2 eye, float rmax){
    vec2 newCoord = oldCoord;
    float r = distance(oldCoord, eye);
    float fsr = fs(r, rmax);
    if (r > 0.0f && r< rmax){
        //新点-眼睛/老点-眼睛 = 新距离/老距离
        //    (newCoord - eye)  / (oldCoord - eye)= fsr ;
        newCoord = fsr * (oldCoord - eye) + eye;
    }
    return newCoord;
}

void main(){
    //两眼间距的一半
    float rmax = distance(left_eye, right_eye) / 2.0;
    vec2 newCoord = calcNewCoord(aCoord, left_eye, rmax);//左眼放大位置的采样点
    newCoord = calcNewCoord(newCoord, right_eye, rmax);//右眼放大位置的采样点
    gl_FragColor = texture2D(vTexture, newCoord);
}