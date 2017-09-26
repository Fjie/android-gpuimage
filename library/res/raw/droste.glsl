// This implementation uses GLSL code by ArKano22:
//uniform float globalTime;
//uniform sampler2D inputImageTexture; // iChannel0 in Shadertoy
//uniform vec2 sketchSize; // iResolution in Shadertoy

const highp float globalTime = 150587.0;
uniform sampler2D inputImageTexture; // iChannel0 in Shadertoy
const highp vec2 sketchSize = vec2(400.0,400.0); // iResolution in Shadertoy

const highp float TWO_PI = 3.141592*2.0;
//ADJUSTABLE PARAMETERS:
const highp float Branches = 1.0;
const highp float scale = 0.5;
//Complex Math:
highp vec2 complexExp(in highp vec2 z){
	return vec2(exp(z.x)*cos(z.y),exp(z.x)*sin(z.y));
}
highp vec2 complexLog(in highp vec2 z){
	return vec2(log(length(z)), atan(z.y, z.x));
}
highp vec2 complexMult(in highp vec2 a,in highp vec2 b){
	return vec2(a.x*b.x - a.y*b.y, a.x*b.y + a.y*b.x);
}
highp float complexMag(in highp vec2 z){
	return float(pow(length(z), 2.0));
}
highp vec2 complexReciprocal(in highp vec2 z){
	return vec2(z.x / complexMag(z), -z.y / complexMag(z));
}
highp vec2 complexDiv(in highp vec2 a,in highp vec2 b){
	return complexMult(a, complexReciprocal(b));
}
highp vec2 complexPower(in highp vec2 a, in highp vec2 b){
	return complexExp( complexMult(b,complexLog(a)));
}
//Misc Functions:
highp float nearestPower(in highp float a, in highp float base){
	return pow(base,  ceil(  log(abs(a))/log(base)  )-1.0 );
}
highp float map(highp float value, highp float istart, highp float istop, highp float ostart,highp  float ostop) {
	   return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
}

void main( void ){

	//SHIFT AND SCALE COORDINATES TO <-1,1>
	highp vec2 uv=gl_FragCoord.xy/sketchSize.xy-.5;
	uv.y*=sketchSize.y/sketchSize.x;

	//ESCHER GRID TRANSFORM:
	highp float factor = pow(1.0/scale,Branches);
	uv= complexPower(uv, complexDiv(vec2( log(factor) ,TWO_PI), vec2(0.0,TWO_PI) ) );

	//RECTANGULAR DROSTE EFFECT:
	highp float FT = fract(globalTime);
	FT = log(FT+1.)/log(2.);
	uv *= 1.0+FT*(scale-1.0);

    highp float npower = max(nearestPower(uv.x,scale),nearestPower(uv.y,scale));
	uv.x = map(uv.x,-npower,npower,-1.0,1.0);
	uv.y = map(uv.y,-npower,npower,-1.0,1.0);

	//UNDO SHIFT AND SCALE:
	gl_FragColor =  texture2D(inputImageTexture,-uv*0.5+vec2(0.5));
}
