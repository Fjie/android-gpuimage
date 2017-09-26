uniform highp vec3      iResolution;           // viewport resolution (in pixels)
uniform  highp float     iTime;                 // shader playback time (in seconds)
//uniform float     iTimeDelta;            // render time (in seconds)
//uniform highp  int       iFrame;                // shader playback frame
//uniform  highp float     iChannelTime[4];       // channel playback time (in seconds)
//uniform highp  vec3      iChannelResolution[4]; // channel resolution (in pixels)
//uniform  highp vec4      iMouse;                // mouse pixel coords. xy: current (if MLB down), zw: click
//uniform mediump sampler2D iChannel0;          // input channel. XX = 2D/Cube

uniform sampler2D inputImageTexture;
varying highp vec2 textureCoordinate;

#define N(h) fract(sin(vec4(6,9,1,0)*h) * 9e2)

//highp vec2 randomFloat(int i){
//    return fract(sin(vec4(6,9,1,0)*i) * 9e2);
//}

void main(){
//    gl_FragColor-=gl_FragColor;//重置像素颜色，不知道起什么作用
    highp vec4 bg = texture2D(inputImageTexture, textureCoordinate);//先着个色
    gl_FragColor += bg * .3;//变暗点
    highp vec2 mCoor = textureCoordinate;//copy一份像素坐标
//    mCoor /= iResolution.y;//做些骚操作，暂且保持不变
    highp float e;
    highp float d;//里层下标
    highp float i=-2.;//外层下标，爆炸次数

    for(highp vec4 p; i<9.; i++){// p 作为颜色向量
        d = floor(e = i*9.1+iTime);//起个随机数的作用？
        p = fract(sin(vec4(6,9,1,0)*d) * 9e2)+.3;//随机生成一个颜色向量
        e -= d;
        for(d = 0.;d < 20.;d++){//控制粒子数量求不卡（依然卡穿）
            highp vec4 mFract1 = fract(sin(vec4(6,9,1,0)*d*i) * 9e2);
//            这里用 += ，使像素在原色 + p 基础上进行亮度放大，实现效果
//            有一个大基数 p ，使粒子颜色接近爆炸颜色
            gl_FragColor += p*(1.-e) / 1e3 / length(mCoor-(p-e*(mFract1-.5)).xy);
        }
    }

 }


 /*
 EXPLANATION OF HOW THIS WORKS


 // turns 1 float into a pseudo random vec4 in the 0-1 range
 #define N(h) fract(sin(vec4(6,9,1,0)*h) * 9e2)

 void mainImage(out vec4 o,vec2 u )
 {
     //initialize o to 0,0,0,0 in the shortest way possible
     // o is what will hold the final pixel color
     o-=o;

     // divide the uv pixel coordinates by the height to get aspect corrected 0-1 coords
     u /= iResolution.y;

     // loop iterator defined here because it saves characters
     // starts at -2 so it goes through 0, which gives the occasional rocket
     float e, d, i=-2.;

     // outer loop, defines number of simultaneous explosions
     // other var assignments inside of the for statement which
     // saves chars on brackets around the for loop
     // i++<9 which saves the i++; that usually goes at the end of the for
     for(vec4 p; i++<9.;
         // e = the time since the shader started + an offset per explosion
         // d = the floored version of that, which stays the same for a second, then jumps
         d = floor(e = i*9.1+iTime),
         // the position of the explosion, as well as the color
         // which is a pseudo random number made by throwing a large number into a sine function
         // +.3 so the explosions are centered horizontally (because aspect ratio of screen)
         p = N(d)+.3,
         // turn e into the fractional component of time e.g. 10.546 -> 0.546
         e -= d)
         // inner loop, renders the particles in the explosion
         for(d=0.; d++<50.;)
             // add to final pixel col
             // p = the color, 1.-e is a number that starts at 1 and goes to 0
             // over the duration of the explosion, essentially fading the particle
             o += p*(1.-e)
             // divide by 1000, otherwise the pixel colors will get too bright
             / 1e3
             // divide by the distance to the particle, the farther away, the darker
             // note that this never gets to 0, each tiny particle has an effect over the
             // entire screen
             // dist to particle is the length of the vector from the current uv coordinate (u)
             // to the particle pos (p-e*(N(d*i)-.5)).xy
             // particle pos starts at p, when e is 0
             // N(d*i) gives a pseudo random vec4 in 0-1 range
             // d*i to give different vec4 for each particle
             // *i is not really necessary but when i=0 it gives 0 for the whole vec4
             // which makes the appearance of the occasional rocket
             // N(d*i)-.5 to go from 0-1 range to -.5 .5 range
             / length(u-(p-e*(N(d*i)-.5)).xy);

    // draw skyline
    // uv.x goes from 0 to 1.6  *i to make it larger i=9. (save a char cuz 9. is 2 chars)
    // +d+e   d+e = iTime  -> this will make the skyline scroll
    // ceil to go in steps (stay at one height, then jump to the next)
    // N(..) to make a value 0, 1, 2, 3.. etc into random numbers in 0-1 range
    // .x*4   N returns a vec4, but we only need a float, *.4 so buildings are lower
    // o -= o*u.y   o-=o would make the buildings pitch black, *u.y to fade them towards the
    // bottom, creating a bit of a fog effect
    if(u.y<N(ceil(u.x*i+d+e)).x*.4) o-=o*u.y;
 }

 */

//void mainImage(out vec4 gl_FragColor,vec2 textureCoordinate )
//{
//    gl_FragColor-=gl_FragColor;
//
//    textureCoordinate /= iResolution.y;
//
//    float e, d, i=-2.;
//
//
//    for(vec4 p; i<9.; i++){
//        d = floor(e = i*9.1+iTime);
//        p = mFract(d)+.3, e -= d;
//        for(d = 0.;d < 50.;d++){
//            gl_FragColor += p*(1.-e) / 1e3 / length(textureCoordinate-(p-e*(mFract(d*i)-.5)).xy);
//        }
//    }
//
//    if(textureCoordinate.y<mFract(ceil(textureCoordinate.x*i+d+e)).x*.4) gl_FragColor-=gl_FragColor*textureCoordinate.y;
//
////    for(vec4 p; i++<9.; d = floor(e = i*9.1+iTime),p = N(d)+.3, e -= d)
////        for(d=0.; d++<50.;)
////            o += p*(1.-e) / 1e3 / length(u-(p-e*(N(d*i)-.5)).xy);
////
////    if(u.y<N(ceil(u.x*i+d+e)).x*.4) o-=o*u.y;
//}

